package bisis.export;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.Indexes;
import org.bson.RawBsonDocument;
import org.bson.conversions.Bson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Petar on 11/27/2017.
 */
public class ImportUtil {

    static String lib;
    static String host;
    static String port;
    static String dbname;
    static String uname;
    static String pass;
    static MongoClient mongoClient;

    public ImportUtil( String host, String port, String lib, String dbname, String uname, String pass, MongoClient mongoClient){
        this.lib = lib;
        this.host = host;
        this.port = port;
        this.dbname = dbname;
        this.uname = uname;
        this.pass = pass;
        this.mongoClient = mongoClient;
    }

   public static void importMembers() throws IOException, InterruptedException {

       String command = "";
       if (uname != null && !uname.equals("") && pass != null && !pass.equals(""))
           command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection " + lib + "_members" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\exportedMembers.json";
       else
           command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection " + lib + "_members" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\exportedMembers.json";

       Process p = Runtime.getRuntime().exec("cmd /c " + command);
       System.out.println("Importing memebers");
       BufferedReader stdInput = new BufferedReader(new
               InputStreamReader(p.getInputStream()));

       BufferedReader stdError = new BufferedReader(new
               InputStreamReader(p.getErrorStream()));

       //print stream
       String s = null;
       while ((s = stdInput.readLine()) != null) {
           System.out.println(s);
       }

       while ((s = stdError.readLine()) != null) {
           System.out.println(s);
       }
   }

   public static void importRecords() throws IOException, InterruptedException {
       String command = "";
       if (uname != null && !uname.equals("") && pass != null && !pass.equals(""))
           command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection " + lib + "_records" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\exportedRecords.json";
       else
           command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection " + lib + "_records" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\exportedRecords.json";

       Process p = Runtime.getRuntime().exec("cmd /c " + command);
       System.out.println("Importing records");
       BufferedReader stdInput = new BufferedReader(new
               InputStreamReader(p.getInputStream()));

       BufferedReader stdError = new BufferedReader(new
               InputStreamReader(p.getErrorStream()));

        //print stream
       String s = null;
       while ((s = stdInput.readLine()) != null) {
           System.out.println(s);
       }

       while ((s = stdError.readLine()) != null) {
           System.out.println(s);
       }
   }

    public static void importLendings() throws IOException, InterruptedException {
        String command = "";
        if (uname != null && !uname.equals("") && pass != null && !pass.equals(""))
            command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection " + lib + "_lendings" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\exportedLendings.json";
        else
            command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection " + lib + "_lendings" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\exportedLendings.json";
        System.out.println("Importing lendings");
        Process p = Runtime.getRuntime().exec("cmd /c " + command);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        //print stream
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }

    public static void importItemAvailibilities() throws IOException, InterruptedException {
        String command = "";
        if (uname != null && !uname.equals("") && pass != null && !pass.equals(""))
            command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection " + lib + "_itemAvailability" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\exportedItemAvailabilities.json";
        else
            command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection " + lib + "_itemAvailability" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\exportedItemAvailabilities.json";
        System.out.println("Importing item availabilities");
        Process p = Runtime.getRuntime().exec("cmd /c " + command);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        //print stream
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }

    public static void importConfig() throws IOException, InterruptedException {
        String command = "";
        if (uname != null && !uname.equals("") && pass != null && !pass.equals(""))
            command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection configs" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\config.json";
        else
            command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection configs" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\config.json";
        System.out.println("Importing config");
        Process p = Runtime.getRuntime().exec("cmd /c " + command);

    }

    public static void importCoders() throws IOException, InterruptedException {
        String command = "";
        Map<String, String> codersMap = initCodersMap();

        for (Map.Entry<String, String> entry: codersMap.entrySet()){

            if (uname != null && !uname.equals("") && pass != null && !pass.equals(""))
                command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection " + entry.getKey() + " --file " + entry.getValue();
            else
                command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection " + entry.getKey() + " --file " + entry.getValue();
            System.out.println("Importing coder: " + entry.getKey());
            Process p = Runtime.getRuntime().exec("cmd /c " + command);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            //print stream
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }

    }

    public static void importAll() throws IOException, InterruptedException {
        importCoders();
        importMembers();
        importRecords();
        importLendings();
        importItemAvailibilities();
        importConfig();
    }

    public static void indexField(String collName, String fieldName){
        System.out.println("Indexing collection: " + collName + ", field: " + fieldName);
        MongoDatabase mdb = mongoClient.getDatabase(dbname);
        mdb.getCollection(collName).createIndex(Indexes.ascending(fieldName));
    }

    private static Map<String, String> initCodersMap(){
        Map<String, String> codersMap = new HashMap<>();
        codersMap.put("coders.accessionReg", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\invknj.json --jsonArray");
        codersMap.put("coders.acquisition", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\nacin_nabavke.json --jsonArray");
        codersMap.put("coders.availability", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\dostupnost.json --jsonArray");
        codersMap.put("coders.binding", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\povez.json --jsonArray");
        codersMap.put("coders.location", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\location.json --jsonArray");
        codersMap.put("coders.sublocation", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\podlokacija.json --jsonArray");
        codersMap.put("coders.status", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\status_primerka.json --jsonArray");
        codersMap.put("coders.format", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\sigformat.json --jsonArray");
        codersMap.put("coders.internalMark", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\interna_oznaka.json --jsonArray");
        codersMap.put("coders.circ_config", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\circConfigs.json");
        codersMap.put("coders.circ_location", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\circLocations.json --jsonArray");
        codersMap.put("coders.corporate_member", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\corporateMember.json --jsonArray");
        codersMap.put("coders.language", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\languages.json --jsonArray");
        codersMap.put("coders.education", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\eduLvls.json --jsonArray");
        codersMap.put("coders.membership", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\memberships.json --jsonArray");
        codersMap.put("coders.membership_type", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\membershipTypes.json --jsonArray");
        //organization se importuje odmah po eksportu, zbog organization(_id) u member- u!!
        //codersMap.put("coders.organization", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\organizations.json --jsonArray");
        codersMap.put("coders.place", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\places.json --jsonArray");
        codersMap.put("coders.user_categ", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\userCategories.json --jsonArray");
        codersMap.put("coders.warning_type", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\warningTypes.json --jsonArray");

        return codersMap;
    }
}
