package bisis.apps.prepis_bgb;

import bisis.model.jongo_records.JoRecord;
import bisis.utils.textsrv.DBStorage;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * @author badf00d21  14.5.19.
 */
public class MiljkovicPrepis {

    public static void main(String[] args) {

        try {
            Scanner scanner = new Scanner(new File("miljkovic.csv"));
            PrintWriter printWriter = new PrintWriter(new File("miljkovic_out.csv"));
            String line = "";
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bisis_zvezdara_final?useSSL=false&serverTimezone=CET",
                    "bisis", "bisis");

            MongoClient mongoClient = new MongoClient("localhost", 27017);
            Jongo jongo = new Jongo(mongoClient.getDB("bisis"));
            MongoCollection collCentralRecords = jongo.getCollection("bgb_records");

            DBStorage storage = new DBStorage();
            int cnt = 0;
            while (scanner.hasNext()) {
                line = scanner.next();
                String[] chunks = line.split(",");
                if (chunks.length < 3)
                    continue;
                if (chunks[0].equals("") || chunks[0].length() != 11) {
                    System.err.println("Error in line: " + line);
                    continue;
                }
                String inv = chunks[0];
                if (!chunks[2].equals("")) {
                    printWriter.write(inv + ", " + chunks[2] + ", had\n");
                    continue;
                } else if (!chunks[1].equals("")) {
                    int localRn = Integer.parseInt(chunks[1]);
                    JoRecord localRec = null;
                    try {
                        localRec = new JoRecord(storage.getByRn(connection, localRn));
                    } catch (Exception e) {
                        printWriter.write(inv + ", local_null, nf\n");
                        continue;
                    }
                    String query = RnPairing.getPairingQuery(localRec);
                    JoRecord centralRecord = null;

                    try {
                        centralRecord = RnPairing.getRecFromCursor(collCentralRecords.find(query).as(JoRecord.class));
                    }
                    catch (Exception e) {
                        System.out.println("invalid query:\n" + query);
                    }

                    if (centralRecord == null) {
                        query = RnPairing.getIsbnOnlyQuery(localRec);
                        centralRecord = RnPairing.getRecFromCursor(collCentralRecords.find(query).as(JoRecord.class));
                    }
                    if (centralRecord != null) {
                        printWriter.write(inv + "," + centralRecord.getRN() + ", paired\n");
                    }
                    else {
                        printWriter.write(inv + ", not_found, nf\n");
                    }

                } else {
                    printWriter.write(inv + ", error, " + line + "\n");
                    continue;
                }
                cnt++;
                if (cnt % 100 == 0) System.out.println("Processed: " + cnt);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
