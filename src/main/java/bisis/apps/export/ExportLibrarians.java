package bisis.apps.export;

import bisis.model.librarian.ProcessTypeBuilder;
import bisis.model.librarian.ProcessTypeCatalog;
import bisis.model.librarian.dto.LibrarianContextDTO;
import bisis.model.librarian.dto.LibrarianDTO;
import bisis.model.librarian.dto.ProcessTypeDTO;
import bisis.utils.DaoUtils;
import bisis.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by Petar on 12/1/2017.
 */
public class ExportLibrarians {

    private static Map<String, ProcessTypeDTO> processTypes = new HashMap<>();

    public static void export(String lib, Connection conn){
        Statement st = null;
        try {
            st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT * from Tipovi_obrade");
            processTypes = new HashMap<String, ProcessTypeDTO>();
            while (rs.next()){
                ProcessTypeDTO ptd = ProcessTypeBuilder.buildDTOFromProcessType(ProcessTypeBuilder.getProcessType(rs.getString("tipobr_spec")));
                ptd.setLibName(lib);
                //ptd.set_id(ObjectId.get());
                processTypes.put(ptd.getName(), ptd);
            }

            rs = st.executeQuery("SELECT * from Bibliotekari");
            List<LibrarianDTO> librarians = new ArrayList<>();
            while(rs.next()){
                LibrarianDTO l = new LibrarianDTO();
                l.setUsername(rs.getString("username") + "@" + lib);
                l.getAuthorities().add("ROLE_ADMIN");
                l.setIme(rs.getString("ime"));
                String plainPassword = rs.getString("password");
                String hashedPass = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
                l.setPassword(hashedPass);

                l.setPrezime(rs.getString("prezime"));
                l.setEmail(rs.getString("email"));
                l.setNapomena((rs.getString("napomena") == null ? "" : rs.getString("napomena"))
                + " stara lozinka je: " + plainPassword);
                l.setObrada(DaoUtils.getInteger(rs,"obrada") == 1);
                l.setCirkulacija(DaoUtils.getInteger(rs,"cirkulacija") == 1);
                l.setAdministracija(DaoUtils.getInteger(rs,"administracija") == 1);
                l.setBiblioteka(lib);
                LibrarianContextDTO cntx = null;

                ProcessTypeCatalog processTypeCatalog = new ProcessTypeCatalog();
                processTypeCatalog.init(conn);
                try {
                    cntx = makeContextFromXML(rs.getString("context"), processTypeCatalog, lib);
                } catch (JDOMException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                l.setContext(cntx);
                librarians.add(l);

            }
            FileUtils.writeTextFile("export" + lib.toUpperCase() + "/coders_json_output/processTypes.json", mapper.writeValueAsString(processTypes.values().toArray()));
            FileUtils.writeTextFile("export" + lib.toUpperCase() + "/librarians.json", mapper.writeValueAsString(librarians));
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private static LibrarianContextDTO makeContextFromXML(String xmlContext, ProcessTypeCatalog processTypeCatalog, String lib) throws JDOMException, IOException {
        LibrarianContextDTO retVal = new LibrarianContextDTO();

        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(new StringReader(xmlContext));
        Element classElement = document.getRootElement();

        retVal.setPref1(classElement.getChild("prefixes").getAttribute("pref1").getValue());
        retVal.setPref2(classElement.getChild("prefixes").getAttribute("pref2").getValue());
        retVal.setPref3(classElement.getChild("prefixes").getAttribute("pref3").getValue());
        retVal.setPref4(classElement.getChild("prefixes").getAttribute("pref4").getValue());
        retVal.setPref5(classElement.getChild("prefixes").getAttribute("pref5").getValue());

        //svi tipovi obrade
        if (classElement.getChildren("process-type").size() > 0){
            for (Element e: classElement.getChildren("process-type")){
                if(e.getAttribute("name").isSpecified()) {
                    ProcessTypeDTO pt = processTypes.get(e.getAttribute("name").getValue());
                    if (pt != null)
                        retVal.getProcessTypes().add(pt);
                  }
            }
        }

        //default tip
        if ((Arrays.asList(classElement.getChildren().stream().map(i -> i.getName()).toArray()).contains("default-process-type"))) {
            ProcessTypeDTO processTypeDTO = processTypes.get(classElement.getChild("default-process-type").getAttribute("name").getValue());
            if(processTypeDTO != null) {
                processTypeDTO.setLibName(lib);
                retVal.setDefaultProcessType(processTypeDTO);
            }
        }

        return retVal;
    }

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
}
