package bisis.export;

import bisis.records.Godina;
import bisis.records.Primerak;
import bisis.records.Record;
import bisis.textsrv.DBStorage;
import bisis.utils.CSVUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MigrateBGB {

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bisis_bgb_04?useSSL=false&serverTimezone=CET"
                    , "bisis", "bisis");
            DB db = new MongoClient().getDB("bisis");

            Jongo jongo = new Jongo(db);
            MongoCollection centralRecs = jongo.getCollection("bgb_records");

            Scanner scanner = new Scanner(new File(MigrateBGB.class.getResource("/bisis/records-map/record_map.csv").getPath()));
            DBStorage storage = new DBStorage();

            //listamo linije record_map.csv
            int i = 0;
            while (scanner.hasNext()) {
                List<String> line = CSVUtils.parseLine(scanner.nextLine());
                Integer localId = Integer.valueOf(line.get(1));
                String centralId = line.get(2);

                //pokupi zapis iz opstinske
                Record localRec = storage.get(conn, localId);
                //pokupi zapis iz centralne
                Record centralRec = centralRecs.findOne("{rn:" + centralId + "}").as(Record.class);

                //prespe primerke/godine i sacuva u centralnu
                if( centralRec != null && localRec != null) {
                    if (centralRec.getPrimerci() == null)
                        centralRec.setPrimerci(new ArrayList<Primerak>());

                    centralRec.getPrimerci().addAll(localRec.getPrimerci());

                    if (centralRec.getGodine() == null)
                        centralRec.setGodine(new ArrayList<Godina>());

                    centralRec.getGodine().addAll(localRec.getGodine());

                    centralRecs.save(centralRec);

                    i++;
                    if (i % 1000 == 0)
                        System.out.println("Processed: " + i + " records");
                }
            }
            scanner.close();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }




}
