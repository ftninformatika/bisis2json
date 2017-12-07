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
    public static void main(Connection conn, String[] args) {
        Options options = new Options();

        options.addOption("o", "output", true,
                "Output file");
        CommandLineParser parser = new GnuParser();

        String outputFile = "";
        try {
            CommandLine cmd = parser.parse(options, args);

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
            export(conn, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void export(Connection conn, PrintWriter outputFile) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery("SELECT * FROM lending");
        PreparedStatement userPS = conn.prepareStatement("SELECT user_id FROM users where sys_id = ?");
        PreparedStatement locPS = conn.prepareStatement("SELECT name FROM location where id = ?");
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
            locPS.setInt(1, rset.getInt("location"));
            ResultSet locRs = locPS.executeQuery();
            if(locRs.next())
                lending.setLocation(locRs.getString("name"));


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
            //e.printStackTrace();
            System.out.println("Tried parsing date 00-00-0000, parsed null ");
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
