package bisis.export;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Petar on 11/27/2017.
 * Helper class for Importing, Indexing, Dropping...data manipulation in mongodb
 */
public class MongoUtil {

    static String lib;
    static String host;
    static String port;
    static String dbname;
    static String uname;
    static String pass;
    static MongoClient mongoClient;
    static String os = System.getProperty("os.name");

    public MongoUtil(String host, String port, String lib, String dbname, String uname, String pass, MongoClient mongoClient){
        this.lib = lib;
        this.host = host;
        this.port = port;
        this.dbname = dbname;
        this.uname = uname;
        this.pass = pass;
        this.mongoClient = mongoClient;
    }

    /***
     *
     * @return true if mongoimport exists on machine and in PATH variables, false the opposite
     */
    public static boolean isMongoImportInstalled(){
        ProcessBuilder builder = new ProcessBuilder("mongoimport");
        builder.redirectErrorStream(true);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
//            System.out.println("Please install mongoimport on your machine and put it in the PATH variables!");
//            e.printStackTrace();
            return false;
        }
        InputStream is = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
               // System.out.println(line);
            }
        } catch (IOException e) {
//            System.out.println("Please install mongoimport on your machine and put it in the PATH variables!2");
//            e.printStackTrace();
            return false;
        }
        return true;
    }

    /***
     * Import memebers from json file to mongo using mongoimport
     * @throws IOException
     * @throws InterruptedException
     */
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

    /***
     * Import records from json file to mongo using mongoimport
     * @throws IOException
     * @throws InterruptedException
     */
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

    /***
     * Import lendings from json file to mongo using mongoimport
     * @throws IOException
     * @throws InterruptedException
     */
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

    /***
     * Import itemAvailibility from file to mongo using mongoimport
     * @throws IOException
     * @throws InterruptedException
     */
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

    /***
     * Import config from json file to mongo using mongoimport
     * @throws IOException
     * @throws InterruptedException
     */
    public static void importConfig() throws IOException, InterruptedException {
        String command = "";
        if (uname != null && !uname.equals("") && pass != null && !pass.equals(""))
            command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection configs" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\config.json";
        else
            command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection configs" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\config.json";
        System.out.println("Importing config");
        Process p = Runtime.getRuntime().exec("cmd /c " + command);

    }

    /***
     * Import coders from json file to mongo using mongoimport
     * @throws IOException
     * @throws InterruptedException
     */
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

    /***
     * Call all available imports
     * @throws IOException
     * @throws InterruptedException
     */
    public static void importAll() throws IOException, InterruptedException {
        importCoders();
        importMembers();
        importRecords();
        importLendings();
        importItemAvailibilities();
        importConfig();
    }

    /***
     *
     * @param collName - collection name
     * @param fieldName - field which will be indexed
     * @param ascending - is index ascending?
     * @param unique - is index unique
     */
    public static void indexField(String collName, String fieldName, boolean ascending, boolean unique){
        System.out.println("Indexing collection: " + collName + ", field: " + fieldName);
        MongoDatabase mdb = mongoClient.getDatabase(dbname);
        if (!unique) {
            if (ascending)
                mdb.getCollection(collName).createIndex(Indexes.ascending(fieldName));
            else
                mdb.getCollection(collName).createIndex(Indexes.descending(fieldName));
        }
        else {
                mdb.getCollection(collName).createIndex(new BasicDBObject(fieldName, 1), new IndexOptions().unique(true));
        }
    }

    /***
     *
     * @return Map, where key is coder collection name in mongoDb, value is path to .json from which data will be parsed
     */
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

    /***
     * Drop all data for selected library
     */
    public static void dropLibraryData(){
        System.out.println("Application mode DROP ALL DATA for library " + lib);
        MongoDatabase mdb = mongoClient.getDatabase(dbname);
        mdb.getCollection(lib + "_lendings").drop();
        mdb.getCollection(lib + "_records").drop();
        mdb.getCollection(lib + "_members").drop();
        mdb.getCollection(lib + "_itemAvailability").drop();

        mdb.getCollection("configs").deleteMany(new BasicDBObject("libraryName", lib));
        mdb.getCollection("coders.warning_type").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.user_categ").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.sublocation").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.status").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.organization").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.membership_type").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.membership").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.location").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.language").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.internalMark").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.format").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.education").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.corporate_member").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.circ_config").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.circ_location").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.binding").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.availability").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.acquisition").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.accessionReg").deleteMany(new BasicDBObject("library", lib));
        System.out.println("All data for library: " + lib + " has been dropped. \nExiting application.");
    }
}
