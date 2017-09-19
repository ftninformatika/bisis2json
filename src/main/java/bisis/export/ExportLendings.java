package bisis.export;

import bisis.circ.Lending;
import bisis.circ.Member;
import bisis.records.Record;
import bisis.records.serializers.JSONSerializer;
import bisis.records.serializers.LooseXMLSerializer;
import bisis.textsrv.DBStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petar on 8/28/2017.
 */
public class ExportLendings {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("a", "address", true,
                "MySQL server address (default: localhost)");
        options.addOption("p", "port", true, "MySQL server port (default: 3306)");
        options.addOption("d", "database", true,
                "MySQL database name (default: bisis)");
        options.addOption("u", "username", true,
                "MySQL server username (default: bisis)");
        options.addOption("w", "password", true,
                "MySQL server password (default: bisis)");
        options.addOption("o", "output", true,
                "Output file");
        CommandLineParser parser = new GnuParser();
        String address = "localhost";
        String port = "3306";
        String database = "bisis";
        String username = "bisis";
        String password = "bisis";
        String outputFile = "";
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
            if (cmd.hasOption("o"))
                outputFile = cmd.getOptionValue("o");
            else
                throw new Exception("Output file not specified.");
        } catch (Exception ex) {
            System.err.println("Invalid parameter(s), reason: " + ex.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("bisis2json-export-users", options);
            return;
        }
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF8")));
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + address
                    + ":" + port + "/" + database + "?useSSL=false&serverTimezone=CET", username, password);
            export(conn, out);
            conn.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void export(Connection conn, PrintWriter outputFile) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery("SELECT * FROM lending");
        PreparedStatement userPS = conn.prepareStatement("SELECT user_id FROM users where sys_id = ?");
        int lendingCount = 0;

        while(rset.next()){
            if (++lendingCount % 1000 == 0)
                System.out.println("lendings exported: " + lendingCount);

            Lending lending = new Lending();

            userPS.setInt(1, rset.getInt("sys_id"));
            ResultSet rUser = userPS.executeQuery();
            if(rUser.next())
                lending.setUserId(rUser.getString("user_id"));

            lending.setCtlgNo(rset.getString("ctlg_no"));
            lending.setLendDate(getDate(rset, "lend_date"));
            lending.setResumeDate(getDate(rset,"resume_date"));
            lending.setReturnDate(getDate(rset, "return_date"));
            lending.setDeadline(getDate(rset, "deadline"));
            lending.setLibrarianLend(rset.getString("librarian_lend"));
            lending.setLibrarianReturn(rset.getString("librarian_return"));
            lending.setLibrarianResume(rset.getString("librarian_resume"));

            //za lokaciju da li ubacivati description ili coder_id????
            if(rset.getInt("location") != 0 && rset.getInt("location") < 10)
                lending.setLocation("0"+rset.getInt("location"));
            else if(rset.getInt("location") != 0 && rset.getInt("location") >= 10 && rset.getInt("location") <= 99)
                lending.setLocation(Integer.toString(rset.getInt("location")));

            outputFile.write(toJSON(lending));
        }
        userPS.close();
        stmt.close();

        System.out.println("Total lendings exported: " + lendingCount);

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

    private static String toJSON(Lending lending) {
        try {
            return mapper.writeValueAsString(lending);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static ObjectMapper mapper = new ObjectMapper();
}