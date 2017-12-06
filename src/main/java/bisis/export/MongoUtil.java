package bisis.export;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.sun.nio.zipfs.ZipPath;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Petar on 11/27/2017.
 * Helper class for Importing, Indexing, Dropping...data manipulation in mongodb
 */
public class MongoUtil {

    String lib;
    String host;
    String port;
    String dbname;
    String uname;
    String pass;
    MongoClient mongoClient;
    Map<String, String> coderMap;

    public MongoUtil(String host, String port, String lib, String dbname, String uname, String pass, MongoClient mongoClient){
        this.lib = lib;
        this.host = host;
        this.port = port;
        this.dbname = dbname;
        this.uname = uname;
        this.pass = pass;
        this.mongoClient = mongoClient;
        coderMap = initCodersMap();
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
                System.out.println(line);
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
   public  void importMembers() throws IOException, InterruptedException {

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
   public  void importRecords() throws IOException, InterruptedException {
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

   public void importLibrarians() throws IOException {
       String command = "";

       if (uname != null && !uname.equals("") && pass != null && !pass.equals(""))
           command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection librarians" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\librarians.json --jsonArray";
       else
           command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection  librarians" + " --file " + System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\librarians.json --jsonArray";

       Process p = Runtime.getRuntime().exec("cmd /c " + command);
       System.out.println("Importing librarians");
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
    public  void importLendings() throws IOException, InterruptedException {
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
    public  void importItemAvailibilities() throws IOException, InterruptedException {
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
    public  void importConfig() throws IOException, InterruptedException {
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
    public  void importCoders() throws IOException, InterruptedException {
        String command = "";

        for (Map.Entry<String, String> entry: coderMap.entrySet()){

            if (uname != null && !uname.equals("") && pass != null && !pass.equals(""))
                command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection " + entry.getKey() + " --file " + entry.getValue() + " --jsonArray";
            else
                command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection " + entry.getKey() + " --file " + entry.getValue() + " --jsonArray";
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
    public  void importAll() throws IOException, InterruptedException {

        //check if export files are generated, exit app if not
        exportExists();

        importCoders();
        importMembers();
        importRecords();
        importLendings();
        importItemAvailibilities();
        importConfig();
        importLibrarians();
    }

    /***
     *
     * @param collName - collection name
     * @param fieldName - field which will be indexed
     * @param ascending - is index ascending?
     * @param unique - is index unique
     */
    public  void indexField(String collName, String fieldName, boolean ascending, boolean unique){
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
    private  Map<String, String> initCodersMap(){
        Map<String, String> codersMap = new HashMap<>();
        codersMap.put("coders.accessionReg", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\Invknj.json");
        codersMap.put("coders.acquisition", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\Nacin_Nabavke.json");
        codersMap.put("coders.availability", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\Dostupnost.json");
        codersMap.put("coders.binding", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\Povez.json");
        codersMap.put("coders.location", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\location.json");
        codersMap.put("coders.sublocation", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\Podlokacija.json");
        codersMap.put("coders.process_types", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\processTypes.json");
        codersMap.put("coders.status", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\Status_Primerka.json ");
        codersMap.put("coders.format", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\SigFormat.json");
        codersMap.put("coders.internalMark", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\coders_json_output\\Interna_oznaka.json");
        codersMap.put("coders.circ_config", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\circConfigs.json");
        codersMap.put("coders.circ_location", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\circLocations.json");
        codersMap.put("coders.corporate_member", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\corporateMember.json");
        codersMap.put("coders.language", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\languages.json");
        codersMap.put("coders.education", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\eduLvls.json");
        codersMap.put("coders.membership", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\memberships.json");
        codersMap.put("coders.membership_type", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\membershipTypes.json");
        codersMap.put("coders.organization", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\organizations.json");
        codersMap.put("coders.place", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\places.json");
        codersMap.put("coders.user_categ", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\userCategories.json");
        codersMap.put("coders.warning_type", System.getProperty("user.dir") + "\\export" + lib.toUpperCase() + "\\circ_coders_json_output\\warningTypes.json");

        return codersMap;
    }

    /***
     * Drop all data for selected library
     */
    public  void dropLibraryData(){
        System.out.println("Application mode DROP ALL DATA for library " + lib);
        MongoDatabase mdb = mongoClient.getDatabase(dbname);
        mdb.getCollection(lib + "_lendings").drop();
        mdb.getCollection(lib + "_records").drop();
        mdb.getCollection(lib + "_members").drop();
        mdb.getCollection(lib + "_itemAvailability").drop();
        mdb.getCollection("librarians").deleteMany(new BasicDBObject("biblioteka", lib));

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
        mdb.getCollection("coders.process_types").deleteMany(new BasicDBObject("libName", lib));
        System.out.println("All data for library: " + lib + " has been dropped. \nExiting application.");
    }

    private  void exportExists(){

        File rec = new File("export" + lib.toUpperCase() + "\\exportedRecords.json");
        File mem = new File("export" + lib.toUpperCase() + "\\exportedMembers.json");
        File len = new File("export" + lib.toUpperCase() + "\\exportedLendings.json");
        File ia = new File("export" + lib.toUpperCase() + "\\exportedItemAvailabilities.json");
        File con = new File("export" + lib.toUpperCase() + "\\config.json");
        File lib = new File("export" + this.lib.toUpperCase() + "\\librarians.json");

        if (!rec.exists()){
            System.out.println("Records export file is missing, exiting application.");
            System.exit(0);
        }
        if (!mem.exists()){
            System.out.println("Members export file is missing, exiting application.");
            System.exit(0);
        }
        if (!len.exists()){
            System.out.println("Lendings export file is missing, exiting application.");
            System.exit(0);
        }
        if (!ia.exists()){
            System.out.println("ItemAvailability export file is missing, exiting application.");
            System.exit(0);
        }
        if (!con.exists()){
            System.out.println("Config export file is missing, exiting application.");
            System.exit(0);
        }
        if (!lib.exists()){
            System.out.println("Librarians export file is missing, exiting application.");
            System.exit(0);
        }

        for (Map.Entry<String, String> entry: coderMap.entrySet()){
            File f = new File(entry.getValue().split(" ")[0]);
            if (!f.exists()){
                System.out.println("Some export files are missing, exiting application.");
                System.exit(0);
            }
        }

    }
}
