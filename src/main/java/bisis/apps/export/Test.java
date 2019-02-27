package bisis.apps.export;

import bisis.model.records.Record;
import bisis.utils.textsrv.DBStorage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Unesite naziv mysql baze kao argument!");
            return;
        }
        String dbName = args[0];

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false&serverTimezone=CET", "bisis", "bisis");
            List<Integer> docIDs = new ArrayList<Integer>();
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT record_id FROM Records");
            while (rset.next()) {
                docIDs.add(rset.getInt(1));
            }
            DBStorage storage = new DBStorage();
            int i = 0;
            for (int id: docIDs) {
                Record rec = storage.get(conn, id);
                if(id == 935)
                    break;
                if(rec.getSubfield("010a") == null || rec.getSubfield("010a").equals("")) {
                    i++;
                    System.out.println(rec.getRecordID());
                }
                System.out.println(rec.getSubfield("010a"));

            }
            System.out.println("Bez isbn: " + i);


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
