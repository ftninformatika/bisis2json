package bisis.apps.msu_prepis;

import bisis.apps.export.MongoUtil;
import bisis.apps.export.Mysql2MongoBisisMigrationTool;
import bisis.model.coders.Coder;
import bisis.model.prefixes.PrefixConverter;
import bisis.model.records.ItemAvailability;
import bisis.model.records.Primerak;
import bisis.model.records.Record;
import bisis.model.records.serializers.JSONSerializer;
import bisis.utils.DaoUtils;
import bisis.utils.textsrv.DBStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static bisis.apps.export.ExportItemAvailability.toJSON;

public class Import {

    private static final Logger LOGGER = Logger.getLogger( Mysql2MongoBisisMigrationTool.class.getName() );
    public static final String library = "msu";
    public static MongoClient mongo = null;
    public static MongoDatabase mdb = null;

    public static void main(String[] args) {
        String jsonInputPath = "";
        String exportDir = "export" + library.toUpperCase();
        String outputFile = exportDir + "/exportedRecords.json";
        String outputFileIA = exportDir + "/exportedItemAvailabilities.json";
        String mongoAddres = "localhost";
        String mongoPort = "27027";
        String mongoName = "bisis";
        String mongoUsername = "";
        String mongoPassword = "";

        Options options = new Options();
        initOptions(options);
        CommandLineParser parser = new GnuParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption("h")){
                printHelp(options);
                System.exit(0);
            }
            if (cmd.hasOption("f"))
                jsonInputPath = cmd.getOptionValue("f");
            else
                throw new Exception("Please specify the path to the input JSON file, for help  input parameter -h (or --help)");
            if (cmd.hasOption("ma"))
                mongoAddres = cmd.getOptionValue("ma");
            if (cmd.hasOption("mp"))
                mongoPort = cmd.getOptionValue("mp");
            if (cmd.hasOption("mn"))
                mongoName = cmd.getOptionValue("mn");
            if (cmd.hasOption("mu"))
                mongoUsername = cmd.getOptionValue("mu");
            if (cmd.hasOption("mw"))
                mongoPassword = cmd.getOptionValue("mw");

            List<Record> records = loadRecords(jsonInputPath);
//            for (Record record : records) {
//                // remove records without inv numbers (2 records)
//                record.getPrimerci().removeIf(p -> p.getInvBroj() == null);
//            }

            // create export dir if it does not exist
            File dir = new File(exportDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    System.out.println("Failed to create directory: " + exportDir);
                    System.exit(0);
                }
            }

            // write data to export dir
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputFile)), StandardCharsets.UTF_8)));
            PrintWriter outIA = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputFileIA)), StandardCharsets.UTF_8)));
            PrintWriter outElastic = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputFile.substring(0, outputFile.lastIndexOf(".")) + "Elastic.json")), StandardCharsets.UTF_8)));
            for (Record rec: records) {
                rec.pack();
                out.println(JSONSerializer.toJSON(rec) );
                outElastic.write(JSONSerializer.toElasticJson(PrefixConverter.toMap(rec, null)));
                for(Primerak p: rec.getPrimerci()) {
                    ItemAvailability ia = new ItemAvailability();
                    ia.setRecordID(String.valueOf(rec.getRecordID()));
                    ia.setBorrowed(false);
                    ia.setCtlgNo(p.getInvBroj());
                    ia.setLibDepartment(p.getOdeljenje());
                    ia.setRn(rec.getRN());
                    outIA.write(toJSON(ia));
                }
            }
            out.close();
            outElastic.close();
            outIA.close();

            // init mongo
            if (mongoUsername.isEmpty() && mongoPassword.isEmpty())
                mongo = new MongoClient( mongoAddres , Integer.parseInt(mongoPort) );
            else
                mongo = new MongoClient( new MongoClientURI("mongodb://" + mongoUsername + ":" + mongoPassword + "@" + mongoAddres + ":" + mongoPort + "/" + mongoName));
            mdb = mongo.getDatabase(mongoName);
            MongoUtil iu = new MongoUtil(mongoAddres, mongoPort, library, mongoName, mongoUsername, mongoPassword, mongo);

            // check if mongoimport is installed
            if(!MongoUtil.isMongoImportInstalled()){
                System.out.println("Please install mongoimport on your machine and put it in PATH variables!");
                System.exit(0);
            }

            // import records
//            iu.dropLibraryData();
            iu.importRecords();
            // import item availability
            iu.importItemAvailibilities();

            //index required fields
//            iu.indexField(library + "_itemAvailability", "recordID", true, false);
//            iu.indexField(library + "_lendings", "ctlgNo", true, false);
//            iu.indexField(library + "_lendings", "lendDate", false, false);
//            iu.indexField(library + "_lendings", "returnDate", false, false);
//            iu.indexField(library + "_lendings", "resumeDate", false, false);
//            iu.indexField(library + "_lendings", "deadline", false, false);
//            iu.indexField(library + "_members", "userId", true, true);
//            iu.indexField(library + "_members", "firstName", true, false);
//            iu.indexField(library + "_members", "lastName", true, false);
//            iu.indexField(library + "_members", "signings.signDate", false, false);
//            iu.indexField(library + "_members", "corporateMember.instName", false, false);
//            iu.indexField(library + "_records", "primerci.invBroj", false, false);
//            iu.indexField(library + "_records", "godine.invBroj", false, false);
//            iu.indexField(library + "_records", "godine.sveske.invBroj", false, false);
//            iu.indexField(library + "_records", "rn", true, false);
//            iu.indexField(library + "_registries", "code", true, false);
//            iu.indexField(library + "_records", "fields.name", true, false);
//            iu.indexField(library + "_records", "fields.subfields.name", true, false);

            // make coders
            Map<String, String> codersMap = new HashMap<>();
            codersMap.put(Coders.CODER_TYPE_ACCESSION_REG, exportDir + "/invBooks.json");
            codersMap.put(Coders.CODER_TYPE_LOCATION, exportDir + "/locations.json");
            codersMap.put(Coders.CODER_TYPE_PROCESS_TYPE, exportDir + "/processTypes.json");
            codersMap.put(Coders.CODER_TYPE_STATUS, exportDir + "/statuses.json");
            codersMap.put(Coders.CODER_TYPE_INTERNAL_MARK, exportDir + "/internalMarks.json");
            codersMap.put(Coders.CODER_TYPE_COUNTER, exportDir + "/counters.json");
            codersMap.put(Coders.CODER_TYPE_SUBLOCATION, exportDir + "/sublocations.json");

            for (String name : codersMap.keySet()) {
                List<Coder> coders = Coders.make(name, library, records);
                String path = codersMap.get(name);
                // export coders
                if (!coders.isEmpty()) Coders.write(coders, path);
            }

            // import coders
            iu.importCoders(codersMap);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        }
    }

    public static List<Record> loadRecords(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
                new File(path),
                new TypeReference<List<Record>>() {}
        );
    }

    private static void initOptions(Options options){
        options.addOption("f", "file", true, "Path to the JSON file");

        options.addOption("ma","mongoaddress", true, "MongoDB server address (default: localhost");
        options.addOption("mp", "mongoport", true, "MongoDB server port (default: 27017)");
        options.addOption("mn", "mongodbname", true, "MongoDB name (default: bisis)");
        options.addOption("mu", "mongousername", true, "MongoDB server username (default: --empty--)");
        options.addOption("mw", "mongopassword", true, "MongoDB server password (default: --empty--)");

        options.addOption("h", "help", false, "Help");

    }

    public static void printHelp(Options options){
        System.out.println("\n\nTool for migrating MSU data to BISIS");
        System.out.println("*requirement: installed mongoimport on machine and put in PATH variables\nParameters:");
        for (Object o: options.getOptions())
            System.out.println("* Short param: -" + ((Option) o).getOpt().toString() + "; Long param: --" + ((Option) o).getLongOpt().toString() + "; Description: " + ((Option) o).getDescription().toString());
    }
}
