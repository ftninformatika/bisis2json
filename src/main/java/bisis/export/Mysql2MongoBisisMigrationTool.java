package bisis.export;

import bisis.utils.FileUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.cli.*;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Petar on 11/23/2017.
 */
public class Mysql2MongoBisisMigrationTool {

    private static final Logger LOGGER = Logger.getLogger( Mysql2MongoBisisMigrationTool.class.getName() );
    public static MongoClient mongo = null;
    public static MongoDatabase mdb = null;

    public static void main(String[] args){
        Options options = new Options();
        initOptions(options);

        String library = "";
        String mysqlAddress = "localhost";
        String mysqlPort = "3306";
        String mysqlDbName = "bisis";
        String mysqlUsername = "bisis";
        String mysqlPassword = "bisis";
        String pathToInnis = "";
        String mongoAddres = "localhost";
        String mongoPort = "27017";
        String mongoName = "bisis";
        String mongoUsername = "";
        String mongoPassword = "";

        CommandLineParser parser = new GnuParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")){
                printHelp(options);
                return;
            }
            if (cmd.hasOption("l"))
                library = cmd.getOptionValue("l");
            else
                throw new Exception("Please specify library name, for help  input parameter -h (or --help)");
            if (cmd.hasOption("a"))
                mysqlAddress = cmd.getOptionValue("a");
            if (cmd.hasOption("p"))
                mysqlPort = cmd.getOptionValue("p");
            if (cmd.hasOption("n"))
                mysqlDbName = cmd.getOptionValue("n");
            if (cmd.hasOption("u"))
                mysqlUsername = cmd.getOptionValue("u");
            if (cmd.hasOption("w"))
                mysqlPassword = cmd.getOptionValue("w");
            if (cmd.hasOption("f"))
                pathToInnis = cmd.getOptionValue("f");
            else if (!cmd.hasOption("d") && cmd.hasOption("e")) //if not drop mode selected and export is selected
                throw new Exception("Please specify path to folder containing reports.ini and client-config.ini, for help  input parameter -h (or --help)");
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

            if (!cmd.hasOption("h") && !cmd.hasOption("i") && !cmd.hasOption("e") && !cmd.hasOption("d")){
                System.out.println("Please select one of the tool mods: -i for import, -e for export, -d for drop data, -h help.");
                System.exit(0);
            }

            if (mongoUsername.equals("") && mongoPassword.equals(""))
                mongo = new MongoClient( mongoAddres , Integer.parseInt(mongoPort) );
            else
                mongo = new MongoClient( new MongoClientURI("mongodb://" + mongoUsername + ":" + mongoPassword + "@" + mongoAddres + ":" + mongoPort + "/" + mongoName));
            mdb = mongo.getDatabase(mongoName);
            MongoUtil iu = new MongoUtil(mongoAddres, mongoPort, library, mongoName, mongoUsername, mongoPassword, mongo);

            //Drop all data mode
            if (cmd.hasOption("d")){
                iu.dropLibraryData();
                System.exit(0);
            }


            if (cmd.hasOption("e")) {
                //create directory where exported json files will live
                String exportDir = "export" + library.toUpperCase();
                FileUtils.createDir(exportDir);

                // main args for exports
                String[] exportRecArgs = new String[]{"-a", mysqlAddress, "-p", mysqlPort, "-d", mysqlDbName, "-u", mysqlUsername, "-w", mysqlPassword, "-f", "json", "-o", exportDir + "/exportedRecords.json"};
                String[] exportCodersArgs = new String[]{"-a", mysqlAddress, "-p", mysqlPort, "-d", mysqlDbName, "-u", mysqlUsername, "-w", mysqlPassword, "-l", library, "-o", exportDir};
                String[] exportLendingsArgs = new String[]{"-a", mysqlAddress, "-p", mysqlPort, "-d", mysqlDbName, "-u", mysqlUsername, "-w", mysqlPassword, "-o", exportDir + "/exportedLendings.json"};
                String[] exportUsersArgs = new String[]{"-a", mysqlAddress, "-p", mysqlPort, "-d", mysqlDbName, "-u", mysqlUsername, "-w", mysqlPassword, "-o", exportDir + "/exportedMembers.json", "-l", library};
                String[] exportItemAvailibilityArgs = new String[]{"-a", mysqlAddress, "-p", mysqlPort, "-d", mysqlDbName, "-u", mysqlUsername, "-w", mysqlPassword, "-o", exportDir + "/exportedItemAvailabilities.json"};
                String[] exportClientConfigArgs = new String[]{"-c", pathToInnis + "/client-config.ini", "-o", exportDir + "/config.json", "-r", pathToInnis + "/reports.ini", "-l", library};

                //exports
//                ExportRecords.main(exportRecArgs);
//                ExportCoders.main(exportCodersArgs);
//                ExportLendings.main(exportLendingsArgs);
//                ExportUsers.main(exportUsersArgs);
//                ExportItemAvailability.main(exportItemAvailibilityArgs);
//                ExportClientConfig.main(exportClientConfigArgs);

                if(cmd.hasOption("z")) //zip if selected
                    ZipUtil.pack(new File("export" + library.toUpperCase()), new File("export" + library.toUpperCase() + ".zip"));
            }
            //import all in MongoDB
          if (cmd.hasOption("i")) {

              //Check if mongoimport is installed
              if(!MongoUtil.isMongoImportInstalled()){
                  System.out.println("Please install mongoimport on your machine and put it in PATH variables!");
                  System.exit(0);
              }

              iu.importAll();

            //index required fields
              iu.indexField(library + "_itemAvailability", "recordID", true, false);
              iu.indexField(library + "_lendings", "ctlgNo", true, false);
              iu.indexField(library + "_lendings", "lendDate", false, false);
              iu.indexField(library + "_lendings", "returnDate", false, false);
              iu.indexField(library + "_lendings", "resumeDate", false, false);
              iu.indexField(library + "_lendings", "deadline", false, false);
              iu.indexField(library + "_members", "userId", true, true);
              iu.indexField(library + "_members", "signings.firstName", true, false);
              iu.indexField(library + "_members", "signings.lastName", true, false);
              iu.indexField(library + "_members", "signings.signDate", false, false);
          }



        } catch (ParseException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(0);
        }

    }


    private static void initOptions(Options options){
        options.addOption("l", "library", true, "Library name (prefix): gbns, tfzr... MANDATORY!");
        options.addOption("f", "pathtoinnis", true, "Path to folder that conatins reports.ini and client-config.ini MADNDATORY if not dropall!");

        options.addOption("a", "mysqladress", true, "MySQL server address (default: localhost)");
        options.addOption("p", "mysqlport", true,"MySQL server port (default: 3306)");
        options.addOption("n","mysqdblname", true, "MySQL database name (default: bisis)");
        options.addOption("u","mysqlusername", true, "MySQL server username (default: bisis)");
        options.addOption("w","mysqlpassword", true, "MySQL server password (default: bisis)");

        options.addOption("ma","mongoaddress", true, "MongoDB server address (default: localhost");
        options.addOption("mp", "mongoport", true, "MongoDB server port (default: 27017)");
        options.addOption("mn", "mongodbname", true, "MongoDB name (default: bisis)");
        options.addOption("mu", "mongousername", true, "MongoDB server username (default: --empty--)");
        options.addOption("mw", "mongopassword", true, "MongoDB server password (default: --empty--)");

        options.addOption("h", "help", false, "Help");
        options.addOption("d", "dropall", false, "Drop all data on MongoDB server for desired library. PRIORITY PARAM if selected!");
        options.addOption("i", "import", false, "If import is selected");
        options.addOption("e", "export", false, "If export is selected");
        options.addOption("z", "archive", false, "Archive after export.");

    }

    public static void printHelp(Options options){
        System.out.println("\n\nTool for migrating bisis from MySQL to Mongo.");
        System.out.println("*requirement: installed mongoimport on machine and put in PATH variables\nParameters:");
        for (Object o: options.getOptions())
            System.out.println("* Short param: -" + ((Option) o).getOpt().toString() + "; Long param: --" + ((Option) o).getLongOpt().toString() + "; Description: " + ((Option) o).getDescription().toString());

    }

}
