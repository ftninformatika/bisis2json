package bisis.export;

import bisis.librarian.dto.LibrarianContextDTO;
import bisis.librarian.dto.LibrarianDTO;

import java.io.IOException;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import bisis.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

/**
 * Created by Petar on 12/1/2017.
 */
public class ExportLibrarians {

    public static void export(String lib, Connection conn){
        Statement st = null;
        try {
            st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * from Bibliotekari");
            PreparedStatement pTp = conn.prepareStatement("SELECT * from Tipovi_obrade");

            List<LibrarianDTO> librarians = new ArrayList<>();
            while(rs.next()){
                LibrarianDTO l = new LibrarianDTO();
                l.setUsername(rs.getString("username") + "@" + lib);
                l.setIme(rs.getString("ime"));
                l.setPassword(rs.getString("password"));
                l.setPrezime(rs.getString("prezime"));
                l.setEmail(rs.getString("email"));
                l.setNapomena(rs.getString("napomena"));
                l.setObrada(rs.getInt("obrada") == 1);
                l.setCirkulacija(rs.getInt("cirkulacija") == 1);
                l.setAdministracija(rs.getInt("administracija") == 1);
                l.setBiblioteka(lib);
                LibrarianContextDTO cntx = null;

                try {
                    cntx = makeContextFromXML(rs.getString("context"));
                } catch (JDOMException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                l.setContext(cntx);
                librarians.add(l);
            }
            FileUtils.writeTextFile("export" + lib.toUpperCase() + "/librarians.json", mapper.writeValueAsString(librarians));
            rs.close();
            pTp.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private static LibrarianContextDTO makeContextFromXML(String xmlContext) throws JDOMException, IOException {
        LibrarianContextDTO retVal = new LibrarianContextDTO();

        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(new StringReader(xmlContext));
        Element classElement = document.getRootElement();

        retVal.setPref1(classElement.getChild("prefixes").getAttribute("pref1").getValue());
        retVal.setPref2(classElement.getChild("prefixes").getAttribute("pref2").getValue());
        retVal.setPref3(classElement.getChild("prefixes").getAttribute("pref3").getValue());
        retVal.setPref4(classElement.getChild("prefixes").getAttribute("pref4").getValue());
        retVal.setPref5(classElement.getChild("prefixes").getAttribute("pref5").getValue());



        return retVal;
    }

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
}
