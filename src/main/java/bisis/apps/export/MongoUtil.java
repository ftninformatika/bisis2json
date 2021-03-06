package bisis.apps.export;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import java.io.*;
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
    String runCmd;

    public MongoUtil(String host, String port, String lib, String dbname, String uname, String pass, MongoClient mongoClient){
        this.lib = lib;
        this.host = host;
        this.port = port;
        this.dbname = dbname;
        this.uname = uname;
        this.pass = pass;
        this.mongoClient = mongoClient;
        coderMap = initCodersMap();
        if (Mysql2MongoBisisMigrationTool.os.equals("Linux") || Mysql2MongoBisisMigrationTool.os.toLowerCase().startsWith("mac"))
            runCmd = "";
        else
            runCmd = "cmd /c ";
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
           command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection " + lib + "_members" + " --file " + System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"exportedMembers.json";
       else
           command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection " + lib + "_members" + " --file " + System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"exportedMembers.json";

       Process p = Runtime.getRuntime().exec(runCmd + command);
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
           command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection " + lib + "_records" + " --file " + System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"exportedRecords.json";
       else
           command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection " + lib + "_records" + " --file " + System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"exportedRecords.json";

       Process p = Runtime.getRuntime().exec(runCmd + command);
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
           command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection librarians" + " --file " + System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"librarians.json --jsonArray";
       else
           command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection  librarians" + " --file " + System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"librarians.json --jsonArray";

       Process p = Runtime.getRuntime().exec(runCmd + command);
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
            command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection " + lib + "_lendings" + " --file " + System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"exportedLendings.json";
        else
            command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection " + lib + "_lendings" + " --file " + System.getProperty("user.dir") +  File.separator +"export" + lib.toUpperCase() + File.separator +"exportedLendings.json";
        System.out.println("Importing lendings");
        Process p = Runtime.getRuntime().exec(runCmd + command);

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
            command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection " + lib + "_itemAvailability" + " --file " + System.getProperty("user.dir") +  File.separator +"export" + lib.toUpperCase() +   File.separator +"exportedItemAvailabilities.json";
        else
            command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection " + lib + "_itemAvailability" + " --file " + System.getProperty("user.dir") +   File.separator +"export" + lib.toUpperCase() +   File.separator +"exportedItemAvailabilities.json";
        System.out.println("Importing item availabilities");
        Process p = Runtime.getRuntime().exec(runCmd + command);

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
            command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection configs" + " --file " + System.getProperty("user.dir") +   File.separator +"export" + lib.toUpperCase() +  File.separator +"config.json";
        else
            command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection configs" + " --file " + System.getProperty("user.dir") +  File.separator +"export" + lib.toUpperCase()  + File.separator +"config.json";
        System.out.println("Importing config");
        Process p = Runtime.getRuntime().exec(runCmd + command);

    }

    /***
     * Import config from json file to mongo using mongoimport
     * @throws IOException
     * @throws InterruptedException
     */
    public  void importRegistries() throws IOException, InterruptedException {
        String command = "";
        if (uname != null && !uname.equals("") && pass != null && !pass.equals(""))
            command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass + " --collection " + Mysql2MongoBisisMigrationTool.library + "_registries" + " --file " + System.getProperty("user.dir") +   File.separator +"export" + lib.toUpperCase() +  File.separator +"registries.json";
        else
            command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection " + Mysql2MongoBisisMigrationTool.library + "_registries" + " --file " + System.getProperty("user.dir") +  File.separator +"export" + lib.toUpperCase()  + File.separator +"registries.json";
        System.out.println("Importing config");
        Process p = Runtime.getRuntime().exec(runCmd + command);

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
     * Import coders from json file to mongo using mongoimport
     * @throws IOException
     * @throws InterruptedException
     */
    public  void importCoders() throws IOException, InterruptedException {
        String command = "";

        for (Map.Entry<String, String> entry: coderMap.entrySet()){

            if (uname != null && !uname.equals("") && pass != null && !pass.equals(""))
                command = "mongoimport --host " + host +" --port " + port + " --db " + dbname + " --username " + uname + " --password " + pass +" --collection " + entry.getKey() + " --file " + entry.getValue() + " --jsonArray --maintainInsertionOrder";
            else
                command = "mongoimport --host " + host +" --port " + port + " --db "+ dbname + " --collection " + entry.getKey() + " --file " + entry.getValue() + " --jsonArray --maintainInsertionOrder";
            System.out.println("Importing coder: " + entry.getKey());

            Process p = Runtime.getRuntime().exec(runCmd + command);

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
        importRegistries();
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
        codersMap.put("coders.accessionReg", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"coders_json_output"+ File.separator +"Invknj.json");
        codersMap.put("coders.task", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"coders_json_output"+ File.separator +"Sifarnik_992b.json");
        codersMap.put("coders.acquisition", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"coders_json_output"+ File.separator +"Nacin_nabavke.json");
        codersMap.put("coders.availability", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"coders_json_output"+ File.separator +"Dostupnost.json");
        codersMap.put("coders.binding", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"coders_json_output"+ File.separator +"Povez.json");
        codersMap.put("coders.location", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"coders_json_output"+ File.separator +"Odeljenje.json");
        codersMap.put("coders.sublocation", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"coders_json_output"+ File.separator +"Podlokacija.json");
        codersMap.put("coders.process_types", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"coders_json_output"+ File.separator +"processTypes.json");
        codersMap.put("coders.status", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"coders_json_output"+ File.separator +"Status_Primerka.json ");
        codersMap.put("coders.format", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"coders_json_output"+ File.separator +"SigFormat.json");
        codersMap.put("coders.internalMark", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"coders_json_output"+ File.separator +"Interna_oznaka.json");
        codersMap.put("coders.circ_config", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"circConfigs.json");
        codersMap.put("coders.circ_location", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"circLocations.json");
        codersMap.put("coders.corporate_member", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"corporateMember.json");
        codersMap.put("coders.language", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"languages.json");
        codersMap.put("coders.education", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"eduLvls.json");
        codersMap.put("coders.membership", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"memberships.json");
        codersMap.put("coders.membership_type", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"membershipTypes.json");
        codersMap.put("coders.organization", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"organizations.json");
        codersMap.put("coders.place", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"places.json");
        codersMap.put("coders.user_categ", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"userCategories.json");
        codersMap.put("coders.warning_type", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"warningTypes.json");
        codersMap.put("coders.warning_counter", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"warningCounters.json");
        codersMap.put("coders.counters", System.getProperty("user.dir") + File.separator +"export" + lib.toUpperCase() + File.separator +"circ_coders_json_output"+ File.separator +"counters.json");

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
        mdb.getCollection(lib + "_reports").drop();
        mdb.getCollection(lib + "_registries").drop();
        mdb.getCollection("librarians").deleteMany(new BasicDBObject("biblioteka", lib));

        mdb.getCollection("configs").deleteMany(new BasicDBObject("libraryName", lib));
        mdb.getCollection("coders.warning_type").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.warning_counter").deleteMany(new BasicDBObject("library", lib));
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
        mdb.getCollection("coders.counters").deleteMany(new BasicDBObject("library", lib));
        mdb.getCollection("coders.task").deleteMany(new BasicDBObject("library", lib));
        System.out.println("All data for library: " + lib + " has been dropped. \nExiting application.");
    }

    private  void exportExists(){

        File rec = new File("export" + lib.toUpperCase() + File.separator + "exportedRecords.json");
        File mem = new File("export" + lib.toUpperCase() + File.separator + "exportedMembers.json");
        File len = new File("export" + lib.toUpperCase() + File.separator +"exportedLendings.json");
        File ia = new File("export" + lib.toUpperCase() + File.separator +"exportedItemAvailabilities.json");
        File con = new File("export" + lib.toUpperCase() + File.separator +"config.json");
        File lib = new File("export" + this.lib.toUpperCase() + File.separator +"librarians.json");

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
