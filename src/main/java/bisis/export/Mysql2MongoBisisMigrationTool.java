package bisis.export;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.apache.commons.cli.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Petar on 11/23/2017.
 */
public class Mysql2MongoBisisMigrationTool {

    private static final Logger LOGGER = Logger.getLogger( Mysql2MongoBisisMigrationTool.class.getName() );

    public static void main(String[] args){
        Options options = new Options();
        options.addOption("l", "library", true, "Library name (prefix): gbns, tfzr...");
        options.addOption("a", "mysqladress", true, "MySQL server address (default: localhost");
        options.addOption("p", "mysqlport", true,"MySQL server port (default: 3306");
        options.addOption("n","mysqdblname", true, "MySQL database name (default: bisis)");
        options.addOption("u","mysqlusername", true, "MySQL server username (default: bisis");
        options.addOption("w","mysqlpassword", true, "MySQL server password (default: bisis");
        options.addOption("f", "pathtoinnis", true, "Path to folder that conatins reports.ini and client-config.ini");
        options.addOption("ma","mongoaddress", true, "MongoDB server address (default: localhost");
        options.addOption("mp", "mongoport", true, "MongoDB server port (default: 27017)");
        options.addOption("mn", "mongodbname", true, "MongoDB name (default: bisis)");
        options.addOption("mu", "mongousername", true, "MongoDB server username (default: --empty--");
        options.addOption("m2", "mongopassword", true, "MongoDB server password (default: --empty--");

        String library = "";
        String mysqlAddress = "localhost";
        String mysqlPort = "3306";
        String mysqlDbName = "bisis";
        String mysqlUsername = "bisis";
        String mysqlPassword = "bisis";
        String pathToInnis = "";
        String mongoAddres = "localhost";
        String mongoPort = "3306";
        String mongoName = "bisis";
        String mongoUsername = "";
        String mongoPassword = "";

        CommandLineParser parser = new GnuParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("l"))
                library = cmd.getOptionValue("l");
            else
                throw new Exception("Please specify library name");
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
            else
                throw new Exception("Please specify path to folder containing reports.ini and client-config.ini");
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

            Connection mysqlConn = DriverManager.getConnection("jdbc:mysql://" + mysqlAddress
                    + ":" + mysqlPort + "/" + mysqlDbName + "?useSSL=false&serverTimezone=CET", mysqlUsername, mysqlPassword);
            MongoClient mongo = null;
            if (mongoUsername.equals("") && mongoPassword.equals(""))
                mongo = new MongoClient( mongoAddres , Integer.parseInt(mongoPort) );
            else
                mongo = new MongoClient( new MongoClientURI("mongodb://" + mongoUsername + ":" + mongoPassword + "@" + mongoAddres + ":" + mongoPort + "/data"));

            
        } catch (ParseException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.toString(), e);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }


        //MongoClient mongo = new MongoClient( "localhost" , 27017 );
    }
}
