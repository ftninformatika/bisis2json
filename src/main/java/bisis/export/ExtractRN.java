package bisis.export;

import bisis.records.Record;
import bisis.textsrv.DBStorage;
import bisis.utils.ProgressBar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExtractRN {

    public static void main(String[] args) {

        if (args.length != 1){
            System.out.println("Enter db name as parameter!");
            System.exit(0);
        }

        String dbName = args[0];
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+ dbName +"?useSSL=false&serverTimezone=CET", "bisis", "bisis");
            List<Integer> docIDs = new ArrayList<Integer>();
            Statement stmt = conn.createStatement();
            int cnt = 0;
            ResultSet rset = stmt.executeQuery("SELECT record_id FROM Records");
            PreparedStatement psInsertRN = conn.prepareStatement("update Records set rn = ? where record_id = ?");

            conn.createStatement().execute("alter table Records add column `rn` int(11)");
            conn.createStatement().execute("create index records_rn_index on Records (rn)");
            conn.createStatement().execute("alter table Primerci modify column primerak_id int not null auto_increment");

            ProgressBar progressBar = new ProgressBar();
            System.out.println("Inserting RN in Records:");
            while (rset.next())
                docIDs.add(rset.getInt(1));
            DBStorage storage = new DBStorage();

            for (Integer id: docIDs) {
                Record r = storage.get(conn, id);
                psInsertRN.setInt(1, r.getRN());
                psInsertRN.setInt(2, r.getRecordID());
                psInsertRN.execute();
                cnt++;
                if (cnt % 100 == 0) {
                    progressBar.update(cnt, docIDs.size());
                }
            }

            progressBar.update(docIDs.size(), docIDs.size());
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
