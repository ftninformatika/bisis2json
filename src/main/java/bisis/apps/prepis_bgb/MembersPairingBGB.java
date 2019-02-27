package bisis.apps.prepis_bgb;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.apache.commons.cli.*;

import java.sql.Connection;
import java.sql.DriverManager;

public class MembersPairingBGB {

    public static MongoClient mongoClient = null;
    public static DB mongoDatabase = null;
    public static Connection mysqlConn = null;
    public static boolean margeMembersMode = true;
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
        boolean printMergedMembersMode = false;

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
                throw new Exception("Please enter branch prefix (-bp), -h for help.");
            if (cmd.hasOption("a"))
                mysqlAddress = cmd.getOptionValue("a");
            if (cmd.hasOption("p"))
                mysqlPort = cmd.getOptionValue("p");
            if (cmd.hasOption("u"))
                mysqlUsername = cmd.getOptionValue("u");
            if (cmd.hasOption("n"))
                mysqlDbName = cmd.getOptionValue("n");
            else
                throw new Exception("MySQL db name is mandatory parameter (-n), -h for help.");
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
            if (cmd.hasOption("m") && cmd.getOptionValue("m").equals("off"))
                margeMembersMode = false;
            if (cmd.hasOption("pm"))
                printMergedMembersMode = true;

            mysqlConn = DriverManager.getConnection("jdbc:mysql://" + mysqlAddress
                    + ":" + mysqlPort + "/" + mysqlDbName + "?useSSL=false&serverTimezone=CET", mysqlUsername, mysqlPassword);
            if (mongoUsername.equals("") && mongoPassword.equals(""))
                mongoClient = new MongoClient( mongoAddres , Integer.parseInt(mongoPort) );
            else
                mongoClient = new MongoClient( new MongoClientURI("mongodb://" + mongoUsername + ":" + mongoPassword + "@" + mongoAddres + ":" + mongoPort + "/" + mongoName));
            mongoDatabase = mongoClient.getDB(mongoName);

            MembersMerger membersMerger = new MembersMerger();
            membersMerger.fixCircLocationsMySQL();
            membersMerger.merge(margeMembersMode, printMergedMembersMode);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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

        options.addOption("m", "merge-mode", true, "If ON, same members will be merged. Default meta-data from central member.(default true, pass off if don't want it) ");
        options.addOption("pm", "print-merged", false, "If ON, .txt file with merged members will be generated.");
        options.addOption("bp", "branch-prefix", true, "Prefix of branch you want to migrate. (mandatory)");
    }

    public static void printHelp(Options options){
        System.out.println("\nTool for migrating members from local to central library (bgb).\nBefore this necessarily unify \'location\' table in MySQL for lendings to be properly copyied!");
        System.out.println("\nFor merge members mode (-m)");
        System.out.println("\nParameters:");
        for (Object o: options.getOptions())
            System.out.println("-" + ((Option) o).getOpt().toString() + "(--" + ((Option) o).getLongOpt().toString() + ") - " + ((Option) o).getDescription().toString());

    }
}
