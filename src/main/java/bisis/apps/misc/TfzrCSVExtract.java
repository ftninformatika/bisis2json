package bisis.apps.misc;

import bisis.model.records.Godina;
import bisis.model.records.Primerak;
import bisis.model.records.Record;
import bisis.model.records.Sveska;
import bisis.utils.textsrv.DBStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * @author badf00d21  21.5.19.
 */
public class TfzrCSVExtract {

    static Connection connection;
    static String ACTIVE_LENDINGS_QUERY = "select ctlg_no from lending where return_date is null";
    static String ALL_RECORDS_ID_QUERY = "select record_id from Records";

    public static void main(String[] args) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bisis_tfzr?useSSL=false&serverTimezone=CET",
                    "bisis", "bisis");
            writeLended();
            writeAllInventory();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void writeLended() throws SQLException, FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(new File("TFZR_ZADUZENE.csv"));
        printWriter.write(RowItem.TABLE_HEADER);

        Statement stmt = connection.createStatement();
        ResultSet rset = stmt.executeQuery(ACTIVE_LENDINGS_QUERY);
        int cnt = 0;
        while (rset.next()) {
            String inv = rset.getString(1);
            DBStorage db = new DBStorage();
            Record r = db.getByInvNum(inv, connection);
            cnt++;
            RowItem rowItem = new RowItem(cnt+"", inv, r);
            printWriter.write(rowItem.toString());
            if (cnt % 100 == 0) System.out.println("Processed LENDED: " + cnt);
        }
        printWriter.close();
    }

    private static void writeAllInventory() throws SQLException, FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(new File("TFZR_SVE.csv"));
        printWriter.write(RowItem.TABLE_HEADER);
        Statement stmt = connection.createStatement();
        ResultSet rset = stmt.executeQuery(ALL_RECORDS_ID_QUERY);
        int cnt = 1;
        DBStorage db = new DBStorage();
        while (rset.next()) {
            int recordId = rset.getInt(1);
            Record r = db.get(connection, recordId);
            if (r == null) {
                System.err.println("Error for record with ID: " + recordId);
                continue;
            }
            if (r.getPrimerci() != null && r.getPrimerci().size() > 0) {
                for (Primerak p: r.getPrimerci()) {
                    RowItem rowItem = new RowItem(cnt+"", p.getInvBroj(), r);
                    printWriter.write(rowItem.toString());
                    cnt++;
                }
            }
            if (r.getGodine() != null && r.getGodine().size() > 0) {
                for (Godina p: r.getGodine()) {
                    RowItem rowItem = new RowItem(cnt+"", p.getInvBroj(), r);
                    printWriter.write(rowItem.toString());
                    cnt++;
                    if (p.getSveske() != null && p.getSveske().size() > 0) {
                        for(Sveska s: p.getSveske()) {
                            RowItem rowItem1 = new RowItem(cnt + "", s.getInvBroj(), r);
                            printWriter.write(rowItem1.toString());
                            cnt++;
                        }
                    }
                }
            }
            if (cnt % 100 == 0) System.out.println("Processed ALL: " + cnt);
        }
        printWriter.close();
    }


}
