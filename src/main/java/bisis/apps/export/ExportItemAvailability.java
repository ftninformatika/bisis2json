package bisis.apps.export;

import bisis.model.records.ItemAvailability;
import bisis.utils.DaoUtils;
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

            outputFile.write(toJSON(ia));
        }
        stmt.close();

        System.out.println("Total items exported: " + primerciCount);

    }

    private static String toJSON(ItemAvailability lending) {
        try {
            return mapper.writeValueAsString(lending);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static ObjectMapper mapper = new ObjectMapper();


}
