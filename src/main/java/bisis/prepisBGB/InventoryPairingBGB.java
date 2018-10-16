package bisis.prepisBGB;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.apache.commons.cli.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Petar
 *
 */
public class InventoryPairingBGB {

    private static final Logger LOGGER = Logger.getLogger(InventoryPairingBGB.class.getName());
    public static MongoClient mongoClient = null;
    public static DB mongoDatabase = null;
    public static Connection mysqlConn = null;
    public static boolean generateMode = false;
    public static boolean insertMode = false;
    public static String mysqlDbName = "";
    public static String branchPrefix = "";

    public static void main(String[] args) {
        Options options = new Options();
        initOptions(options);

        String mysqlAddress = "localhost";
        String mysqlPort = "3306";
        String mysqlUsername = "bisis";
        String mysqlPassword = "bisis";
        String mongoAddres = "localhost";
        String mongoPort = "27017";
        String mongoName = "bisis";
        String mongoUsername = "";
        String mongoPassword = "";
        String recordsMapPath = "";
        boolean insertPicturebooks = false;
        boolean insertUnpaired = false;

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("h")) {
                printHelp(options);
                System.exit(0);
            }
            if (cmd.hasOption("bp"))
                branchPrefix = cmd.getOptionValue("bp");
            else
                throw new Exception("Please enter branch prefix -bp, -h for help.");
            if (cmd.hasOption("a"))
                mysqlAddress = cmd.getOptionValue("a");
            if (cmd.hasOption("p"))
                mysqlPort = cmd.getOptionValue("p");
            if (cmd.hasOption("u"))
                mysqlUsername = cmd.getOptionValue("u");
            if (cmd.hasOption("n"))
                mysqlDbName = cmd.getOptionValue("n");
            else
                throw new Exception("MySQL db name is mandatory parameter, -h for help.");
            if (cmd.hasOption("w"))
                mysqlPassword = cmd.getOptionValue("w");
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
            if (cmd.hasOption("g")) {
                generateMode = true;
                if (!cmd.hasOption("rm"))
                    throw new Exception("You must eneter path to records map .csv file in generate mode (-rf) !");
                else
                    recordsMapPath = cmd.getOptionValue("rm");
            }
            if (cmd.hasOption("i"))
                insertMode = true;
            if (cmd.hasOption("ip"))
                insertPicturebooks = true;
            if (cmd.hasOption("iu"))
                insertUnpaired = true;

            mysqlConn = DriverManager.getConnection("jdbc:mysql://" + mysqlAddress
                    + ":" + mysqlPort + "/" + mysqlDbName + "?useSSL=false&serverTimezone=CET", mysqlUsername, mysqlPassword);
            if (mongoUsername.equals("") && mongoPassword.equals(""))
                mongoClient = new MongoClient( mongoAddres , Integer.parseInt(mongoPort) );
            else
                mongoClient = new MongoClient( new MongoClientURI("mongodb://" + mongoUsername + ":" + mongoPassword + "@" + mongoAddres + ":" + mongoPort + "/" + mongoName));
            mongoDatabase = mongoClient.getDB(mongoName);

            RecordsMapGenerator recordsMapGenerator = new RecordsMapGenerator();
            RecordsTransfusionMachine recordsTransfusionMachine = new RecordsTransfusionMachine();

            if (generateMode)
                recordsMapGenerator.generate(recordsMapPath);
            if (insertMode)
                recordsTransfusionMachine.transfuse(insertPicturebooks, insertUnpaired);



        } catch (ParseException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.toString(), e);
        } catch (Exception e) {
            //e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }

    }

    private static void initOptions(Options options) {
        options.addOption("a", "mysqladress", true, "MySQL server address (default: localhost)");
        options.addOption("p", "mysqlport", true,"MySQL server port (default: 3306)");
        options.addOption("n","mysqdblname", true, "MySQL database name (mandatory, can be different for every local library)");
        options.addOption("u","mysqlusername", true, "MySQL server username (default: bisis)");
        options.addOption("w","mysqlpassword", true, "MySQL server password (default: bisis)");

        options.addOption("ma","mongoaddress", true, "MongoDB server address (default: localhost)");
        options.addOption("mp", "mongoport", true, "MongoDB server port (default: 27017)");
        options.addOption("mn", "mongodbname", true, "MongoDB name (default: bisis)");
        options.addOption("mu", "mongousername", true, "MongoDB server username (default: --empty--)");
        options.addOption("mw", "mongopassword", true, "MongoDB server password (default: --empty--)");

        options.addOption("h", "help", false, "Help");

        options.addOption("rm", "records-map", true, "Path to records_map file (if -g => mandatory param)");
        options.addOption("bp", "branch-prefix", true, "Prefix of branch you want to migrate. (mandatory)");
        options.addOption("g", "generate", false, "Generate valid map with all possible paired records.");
        options.addOption("i", "insert", false, "Insert all paired records from generated map.");
        options.addOption("ip", "insert-picturebooks", false, "Also copy picture books");
        options.addOption("iu", "insert-unpaired", false, "Also copy all unpaired records");

    }

    public static void printHelp(Options options){
        System.out.println("\nTool for migrating and copying inventory from local libraries BGB to central.");
        System.out.println("\nFor insertion mode (-i) this jar must be in same directory with generated .csv files to work.");
        System.out.println("\nParameters:");
        for (Object o: options.getOptions())
            System.out.println("-" + ((Option) o).getOpt().toString() + "(--" + ((Option) o).getLongOpt().toString() + ") - " + ((Option) o).getDescription().toString());

    }
}
