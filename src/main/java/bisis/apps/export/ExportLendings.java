package bisis.apps.export;

import bisis.model.circ.Lending;
import bisis.model.circ.Warning;
import bisis.utils.DaoUtils;
import bisis.utils.DateUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Petar on 8/28/2017.
 */
public class ExportLendings {

    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(Connection conn, String[] args) {
        Options options = new Options();

        options.addOption("l", "library", true,
                "Library code (gbns, gbsa, tfzr...)");
        options.addOption("o", "output", true,
                "Output file");
        CommandLineParser parser = new GnuParser();

        String library = "";
        String outputFile = "";
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("l"))
                library = cmd.getOptionValue("l");
            else
                throw new Exception("Library code not specified.");
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
            export(conn, out, library);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void export(Connection conn, PrintWriter outputFile, String library) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery("SELECT * FROM lending");
        PreparedStatement userPS = conn.prepareStatement("SELECT user_id FROM users where sys_id = ?");
        PreparedStatement locPS = conn.prepareStatement("SELECT name FROM location where id = ?");
        PreparedStatement warningsPS = conn.prepareStatement("SELECT * FROM warnings WHERE lending_id = ?");

        ResultSet warningTypesRs = conn.createStatement().executeQuery("SELECT * from warning_types");
        Map<Integer, String> warningTypesMap = new HashMap<>();
        while(warningTypesRs.next()){
            warningTypesMap.put(DaoUtils.getInteger(warningTypesRs,"id"), warningTypesRs.getString("name"));
        }


        int lendingCount = 0;

        while(rset.next()){
            if (++lendingCount % 1000 == 0)
                System.out.println("lendings exported: " + lendingCount);

            Lending lending = new Lending();

            //warnings
            Integer leindgId = DaoUtils.getInteger(rset, "id");
            warningsPS.setInt(1, leindgId);
            ResultSet warningsResulsts = warningsPS.executeQuery();
            List<Warning> warningList = new ArrayList<>();
            while (warningsResulsts.next()){
                Warning w = new Warning();
                w.setWarningDate(DateUtils.getInstant(warningsResulsts, "wdate"));
                w.setWarningType(warningTypesMap.get(warningsResulsts.getInt("wtype")));
                w.setWarnNo(warningsResulsts.getString("warn_no"));
                w.setDeadline(DateUtils.getInstant(warningsResulsts, "deadline"));
                w.setNote(warningsResulsts.getString("note"));
                warningList.add(w);
            }
            lending.setWarnings(warningList);


            userPS.setInt(1, rset.getInt("sys_id"));
            ResultSet rUser = userPS.executeQuery();
            if(rUser.next())
                lending.setUserId(rUser.getString("user_id"));

            lending.setCtlgNo(rset.getString("ctlg_no"));
            lending.setLendDate(DateUtils.getInstant(rset, "lend_date"));
            lending.setResumeDate(DateUtils.getInstant(rset, "resume_date"));
            lending.setReturnDate(DateUtils.getInstant(rset, "return_date"));
            lending.setDeadline(DateUtils.getInstant(rset, "deadline"));
            lending.setLibrarianLend(rset.getString("librarian_lend") + "@" + library);
            lending.setLibrarianReturn(rset.getString("librarian_return") + "@" + library);
            lending.setLibrarianResume(rset.getString("librarian_resume") + "@" + library);


            locPS.setInt(1, rset.getInt("location"));
            if (!rset.wasNull()) {
                ResultSet locRs = locPS.executeQuery();
                if (locRs.next())
                    lending.setLocation(locRs.getString("name"));
            }

            outputFile.write(toJSON(lending));
        }
        warningsPS.close();
        userPS.close();
        stmt.close();

        System.out.println("Total lendings exported: " + lendingCount);

    }

    private static String toJSON(Lending lending) {
        try {
            return mapper.writeValueAsString(lending);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
