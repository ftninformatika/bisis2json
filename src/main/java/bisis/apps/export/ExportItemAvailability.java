package bisis.apps.export;

import bisis.model.records.ItemAvailability;
import bisis.model.records.Record;
import bisis.utils.DaoUtils;
import bisis.utils.textsrv.DBStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Petar on 10/19/2017.
 */
public class ExportItemAvailability {

    public static void main(Connection conn, String[] args){
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
        }
        catch (Exception ex) {
            System.err.println("Invalid parameter(s), reason: " + ex.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("bisis2json-export-item-availabilities", options);
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
        ResultSet rsetOdlj = stmt.executeQuery("SELECT * FROM Odeljenje");

        Map<String, String> libDepartments = new HashMap<>();
        while (rsetOdlj.next()){
            libDepartments.put(rsetOdlj.getString("odeljenje_id"), rsetOdlj.getString("odeljenje_naziv"));
        }


        ResultSet rset = stmt.executeQuery("SELECT * FROM Primerci");
        int primerciCount = 0;

        while(rset.next()){
            if (++primerciCount % 1000 == 0)
                System.out.println("Item availiabilities exported: " + primerciCount);

            ItemAvailability ia = new ItemAvailability();
            ia.setRecordID(rset.getString("record_id"));
            ia.setBorrowed(DaoUtils.getInteger(rset,"stanje") == 1);
            ia.setCtlgNo(rset.getString("inv_broj"));
            ia.setLibDepartment(libDepartments.get(rset.getString("odeljenje_id")));

            int recId = rset.getInt("record_id");
            DBStorage storage = new DBStorage();
            Record rec = storage.get(conn, recId);

            if (rec != null && rec.getRN() != 0) {
                ia.setRn(rec.getRN());
            } else {
                System.out.println("No RN for:" + recId);
            }

            outputFile.write(toJSON(ia));
        }

        ResultSet rsetSveske = stmt.executeQuery("SELECT * FROM Sveske");
        int sveskeCount = 0;

        while(rsetSveske.next()){
            if (++sveskeCount % 1000 == 0)
                System.out.println("Item availiabilities exported: " + sveskeCount);

            Integer godinaId = rsetSveske.getInt("godina_id");

            Integer recordId = getRecordIdFromGodinaId(conn, godinaId);

            DBStorage storage = new DBStorage();
            Record rec = storage.get(conn, recordId);

            ItemAvailability ia = new ItemAvailability();
            ia.setRecordID(recordId + "");
            ia.setBorrowed(DaoUtils.getInteger(rsetSveske,"stanje") == 1);
            ia.setCtlgNo(rsetSveske.getString("inv_br"));
            ia.setLibDepartment(libDepartments.get(getOdeljenjeFromGodinaId(conn, godinaId)));

            if (rec != null && rec.getRN() != 0) {
                ia.setRn(rec.getRN());
            } else {
                System.out.println("No RN for:" + recordId);
            }

            outputFile.write(toJSON(ia));
        }
        stmt.close();

        System.out.println("Total items exported: " + primerciCount);

    }

    private static Integer getRecordIdFromGodinaId(Connection conn, Integer godinaId) throws SQLException {
        Integer retVal = null;
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery("SELECT * FROM Godine where godina_id=" + godinaId);
        while (rset.next()) {
            retVal = rset.getInt("record_id");
        }
        stmt.close();
        return retVal;
    }

    private static String getOdeljenjeFromGodinaId(Connection conn, Integer godinaId) throws SQLException {
        String retVal = null;
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery("SELECT * FROM Godine where godina_id=" + godinaId);
        while (rset.next()) {
            retVal = rset.getString("odeljenje_id");
        }
        stmt.close();
        return retVal;
    }

    public static String toJSON(ItemAvailability lending) {
        try {
            return mapper.writeValueAsString(lending);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static ObjectMapper mapper = new ObjectMapper();


}
