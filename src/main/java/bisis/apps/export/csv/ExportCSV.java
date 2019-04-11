package bisis.apps.export.csv;

import bisis.model.records.Record;
import bisis.utils.textsrv.DBStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExportCSV {

    private static final char DELIMITER = '|';

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Unesite naziv mysql baze kao argument!");
            return;
        }

        String dbName = args[0];
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false&serverTimezone=CET"
                    , "bisis", "bisis");
            PrintWriter pw = new PrintWriter(new File("lokalno.csv"));

            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT record_id FROM Records");
            List<Integer> docIDs = new ArrayList<>();
            while (rset.next())
                docIDs.add(rset.getInt(1));
            rset.close();
            stmt.close();
            DBStorage storage = new DBStorage();

            for (int id: docIDs) {
                Record rec = storage.get(conn, id);
                //ovde logika
                StringBuffer sb = new StringBuffer();
                sb.append(rec.getRN());
                sb.append(DELIMITER);
                sb.append(rec.getSubfieldContent("200a"));
                sb.append(DELIMITER);
                sb.append(rec.getSubfieldContent("200h"));
                sb.append(DELIMITER);
                sb.append(rec.getSubfieldContent("010a"));
                sb.append(DELIMITER);
                sb.append(rec.getSubfieldContent("205a"));
                sb.append(DELIMITER);
                sb.append(rec.getSubfieldContent("210a"));
                sb.append(DELIMITER);
                sb.append(rec.getSubfieldContent("210c"));
                sb.append(DELIMITER);
                sb.append(rec.getSubfieldContent("210d"));
                sb.append(DELIMITER);
                sb.append(rec.getSubfieldContent("215a"));
                sb.append("\n");
                pw.write(sb.toString());

            }
            pw.close();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
