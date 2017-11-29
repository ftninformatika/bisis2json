package bisis.export;

import bisis.circ.*;
import bisis.coders.Coder;
import bisis.coders.ItemStatus;
import bisis.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.cli.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Petar on 8/31/2017.
 */
public class ExportCoders {

    public static void main(String[] args){

        Options options = new Options();
        options.addOption("a","Address", true,"MySQL server address (default: localhost");
        options.addOption("p", "port", true, "MySQL server port (default: 3306)");
        options.addOption("d", "database", true,
                "MySQL database name (default: bisis)");
        options.addOption("u", "username", true,
                "MySQL server username (default: bisis)");
        options.addOption("w", "password", true,
                "Password default(bisis)");
        options.addOption("l", "library", true,
                "Library code (gbns)");
        options.addOption("o", "output", true,
                "Output directory");
        CommandLineParser parser = new GnuParser();
        String address = "localhost";
        String port = "3306";
        String database = "bisis";
        String username = "bisis";
        String password = "bisis";
        String outputDir = "";
        String library = "";
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("a"))
                address = cmd.getOptionValue("a");
            if (cmd.hasOption("p"))
                port = cmd.getOptionValue("p");
            if (cmd.hasOption("d"))
                database = cmd.getOptionValue("d");
            if (cmd.hasOption("u"))
                username = cmd.getOptionValue("u");
            if (cmd.hasOption("w"))
                password = cmd.getOptionValue("w");
            if (cmd.hasOption("l"))
                library = cmd.getOptionValue("l");
            else
                throw new Exception("Specify library code!");
            if (cmd.hasOption("o"))
                outputDir = cmd.getOptionValue("o");
            else
                throw new Exception("Specify output directory!");

        } catch (Exception ex) {
            System.err.println("Invalid parameter(s), reason: " + ex.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("bisis2json-export-coders", options);
            return;
        }
        String codersOutputDirName = outputDir+"/coders_json_output";
        String circCodersOutputDirName = outputDir+"/circ_coders_json_output";

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + address
                    + ":" + port + "/" + database + "?useSSL=false&serverTimezone=CET", username, password);
            if(FileUtils.createDir(codersOutputDirName)) {
                exportCoders1(conn, library, codersOutputDirName);
            }
            if(FileUtils.createDir(circCodersOutputDirName)){
                exportCircCoders(conn, library, circCodersOutputDirName);
            }
            else
                throw new Exception("Output direcory not created");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void exportCircCoders(Connection conn, String library, String circCodersOutputDirName) throws SQLException, JsonProcessingException, FileNotFoundException, UnsupportedEncodingException {
        //circ_location??
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * from groups");

        {
            List<CorporateMember> corporateMembers = new ArrayList<>();
            while (rs.next()){
                CorporateMember c = new CorporateMember();
                c.setLibrary(library);
                c.setUserId(rs.getString("user_id"));
                c.setInstName(rs.getString("inst_name"));
                c.setSignDate(getDate(rs, "sign_date"));
                c.setAddress(rs.getString("address"));
                c.setCity(rs.getString("city"));
                c.setZip(rs.getInt("zip"));
                c.setPhone(rs.getString("phone"));
                c.setEmail(rs.getString("email"));
                c.setFax(rs.getString("fax"));
                c.setSecAddress(rs.getString("sec_address"));
                c.setSecCity(rs.getString("sec_city"));
                c.setSecZip(rs.getInt("sec_zip"));
                c.setSecPhone(rs.getString("sec_phone"));
                c.setContFirstName(rs.getString("cont_fname"));
                c.setContLastName(rs.getString("cont_lname"));
                c.setContEmail(rs.getString("cont_email"));
                corporateMembers.add(c);
            }
            writeToFile(circCodersOutputDirName + "/corporateMember.json" , mapper.writeValueAsString(corporateMembers));
        }


        rs = statement.executeQuery("SELECT * from edu_lvl");
        {
            List<EducationLvl> educationLvls = new ArrayList<>();
            while (rs.next()) {
                EducationLvl e = new EducationLvl();
                e.setDescription(rs.getString("name"));
                e.setLibrary(library);
                educationLvls.add(e);
            }
            writeToFile(circCodersOutputDirName + "/eduLvls.json", mapper.writeValueAsString(educationLvls));
        }

        rs = statement.executeQuery("SELECT * from languages");
        {
            List<Language> languages = new ArrayList<>();
            while (rs.next()){
                Language l = new Language();
                l.setLibrary(library);
                l.setDescription(rs.getString("name"));
                languages.add(l);
            }
            writeToFile(circCodersOutputDirName + "/languages.json", mapper.writeValueAsString(languages));
        }

        rs = statement.executeQuery("SELECT * from membership");
        {
            List<Membership> memberships = new ArrayList<>();
            while (rs.next()){
                Membership m = new Membership();
                m.setLibrary(library);
                m.setUserCateg(rs.getString("user_categ"));
                m.setMemberType(rs.getString("mmbr_type"));
                m.setCost(rs.getDouble("cost"));
                memberships.add(m);
            }
            writeToFile(circCodersOutputDirName + "/memberships.json", mapper.writeValueAsString(memberships));
        }

        rs = statement.executeQuery("SELECT * from mmbr_types");
        {
            List<MembershipType> membershipTypes = new ArrayList<>();
            while (rs.next()){
                MembershipType m = new MembershipType();
                m.setLibrary(library);
                m.setPeriod(rs.getInt("period"));
                m.setDescription(rs.getString("name"));
                membershipTypes.add(m);
            }
            writeToFile(circCodersOutputDirName + "/membershipTypes.json", mapper.writeValueAsString(membershipTypes));
        }

        rs = statement.executeQuery("SELECT * from organization");
        {
            List<Organization> organizations = new ArrayList<>();
            Map<Integer, String> idMigrationMap = new HashMap<>();
            while (rs.next()){
                Organization m = new Organization();
                m.set_id(ObjectId.get().toHexString());
                m.setLibrary(library);
                m.setZip(rs.getString("zip"));
                m.setAddress(rs.getString("address"));
                m.setCity(rs.getString("city"));
                m.setName(rs.getString("name"));
                organizations.add(m);

                idMigrationMap.put(rs.getInt("id"),m.get_id());

            }
            writeToFile(circCodersOutputDirName + "/organizations.json", mapper.writeValueAsString(organizations));
            writeToFile(circCodersOutputDirName + "/organization_id-id.json", mapper.writeValueAsString(idMigrationMap));
        }

        rs = statement.executeQuery("SELECT * from places");
        {
            List<Place> places = new ArrayList<>();
            while (rs.next()){
                Place m = new Place();
                m.setLibrary(library);
                m.setZip(rs.getString("zip"));
                m.setCity(rs.getString("city"));
                places.add(m);
            }
            writeToFile(circCodersOutputDirName + "/places.json", mapper.writeValueAsString(places));
        }

        rs = statement.executeQuery("SELECT * from user_categs");
        {
            List<UserCategory> userCategories = new ArrayList<>();
            while (rs.next()){
                UserCategory m = new UserCategory();
                m.setLibrary(library);
                m.setDescription(rs.getString("name"));
                m.setTitlesNo(rs.getInt("titles_no"));
                m.setPeriod(rs.getInt("period"));

                Integer max_period = rs.getInt("max_period");
                m.setMaxPeriod(max_period);
                userCategories.add(m);
            }
            writeToFile(circCodersOutputDirName + "/userCategories.json", mapper.writeValueAsString(userCategories));
        }

        rs = statement.executeQuery("SELECT * from warning_types");
        {
            List<WarningType> warningTypes = new ArrayList<>();
            while (rs.next()){
                WarningType m = new WarningType();
                m.setLibrary(library);
                m.setDescription(rs.getString("name"));
                m.setTemplate(rs.getString("wtext"));
                warningTypes.add(m);
            }
            writeToFile(circCodersOutputDirName + "/warningTypes.json", mapper.writeValueAsString(warningTypes));
        }

        rs = statement.executeQuery("SELECT * from location");
        {
            List<CircLocation> circLocations = new ArrayList<>();
            while (rs.next()){
                CircLocation m = new CircLocation();
                m.setLibrary(library);
                m.setDescription(rs.getString("name"));
                m.setLocationCode(rs.getString("id"));
                m.setLastUserId(rs.getInt("last_user_id"));
                circLocations.add(m);
            }
            writeToFile(circCodersOutputDirName + "/circLocations.json", mapper.writeValueAsString(circLocations));
        }

        //circ-_config
        rs = statement.executeQuery("SELECT * FROM configs");
        {
            CircConfig c = new CircConfig();
            c.setLibrary(library);
            while(rs.next()){
                if(rs.getString("name").equals("circ-options"))
                    c.setCircOptionsXML(rs.getString("text"));

                if(rs.getString("name").equals("circ-validator"))
                    c.setValidatorOptionsXML(rs.getString("text"));
            }
            writeToFile(circCodersOutputDirName + "/circConfigs.json", mapper.writeValueAsString(c));
        }
        System.out.println("Coders successfully exported!");
    }

    private static void writeToFile(String filePath, String jsonString) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(  filePath), "UTF8")));
        pw.write(jsonString);
        pw.close();
    }


    private static void exportCoders1(Connection conn, String lib, String outputDirName) throws SQLException, FileNotFoundException, UnsupportedEncodingException {
        String[] tableNames = { "Invknj", "Nacin_nabavke", "Dostupnost", "Povez", "SigFormat", "Interna_oznaka", "Status_Primerka", "location", "Podlokacija" };



        for (String coderName: tableNames) {

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from " + coderName);
            ResultSetMetaData rsmd = rs.getMetaData();

            if(!coderName.equals("status_primerka")){
                try {
                    writeCoder(rs,sortColumnsIdFirst(rsmd), coderName, lib, outputDirName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            else { //item status ima vise polja
                PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(  outputDirName + "/status_primerka.json"), "UTF8")));
                List<ItemStatus> statuses = new ArrayList<>();
                while(rs.next()) {
                    ItemStatus c = new ItemStatus();
                    c.setCoder_id(rs.getString("status_id"));
                    c.setDescription(rs.getString("status_opis"));
                    c.setLibrary(lib);
                    c.setLendable(rs.getInt("zaduziv") == 1);
                    c.setShowable(false);
                    statuses.add(c);
                }
                pw.write(toJSONItemStatus(statuses));
                pw.close();
            }
            stmt.close();

        }
    }

    private static void writeCoder(ResultSet rs, int[] cols, String tName, String libName, String outputDirName) throws FileNotFoundException, UnsupportedEncodingException, SQLException {
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream( outputDirName + "/" +tName + ".json"), "UTF8")));
        List<Coder> coders = new ArrayList<>();
        while(rs.next()) {
            Coder c = new Coder();
            String cid = rs.getString(cols[0]);
            if (cid.length() == 1)
                cid = "0" + cid;
            c.setCoder_id(cid);
            c.setDescription(rs.getString(cols[1]));
            c.setLibrary(libName);
            coders.add(c);
        }

        pw.write(toJSONCoder(coders));
        pw.close();
    }

    private static int[] sortColumnsIdFirst(ResultSetMetaData rsmd) throws SQLException {
        int[] retVal = new int[2];
        for (int i = 1; i <= rsmd.getColumnCount(); i++){
            if (rsmd.getColumnName(i).toLowerCase().contains("id"))  //kod sifarnika samo jedna kolona sadrzi "id", nema stranih kljuceva
                retVal[0] = i;
                break;
        }
        if (retVal[0] == 1)
            retVal[1] = 2;
        else
            retVal[1] = 1;
        return  retVal;
    }

    private static String toJSONCoder(List<Coder> c) {
        try {
            return mapper.writeValueAsString(c);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String toJSONItemStatus(List<ItemStatus> c) {
        try {
            return mapper.writeValueAsString(c);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }




    private static LocalDate getDate(ResultSet rset, String columnName)  {

        try {
            java.sql.Date date = rset.getDate(columnName);
            if (date == null)
                return null;
            return date.toLocalDate();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
