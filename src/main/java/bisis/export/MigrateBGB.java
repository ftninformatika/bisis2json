package bisis.export;

import bisis.records.Godina;
import bisis.records.Primerak;
import bisis.records.Record;
import bisis.textsrv.DBStorage;
import bisis.utils.CSVUtils;
import bisis.utils.FileUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class MigrateBGB {

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Unesite naziv mysql baze kao argument!");
            return;
        }

        String dbName = args[0];


        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false&serverTimezone=CET"
                    , "bisis", "bisis");
            DB db = new MongoClient().getDB("bisis");
            PrintWriter outPrimerci = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("primerciMap.json"), "UTF8")));
            PrintWriter outGodine = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("godineMap.json"), "UTF8")));


            Jongo jongo = new Jongo(db);
            MongoCollection centralRecs = jongo.getCollection("bgb_records");

            Scanner scanner = new Scanner(new File(MigrateBGB.class.getResource("/bisis/records-map/record_map.csv").getPath()));
            DBStorage storage = new DBStorage();

            //listamo linije record_map.csv
            int i = 0;
            List<String> errIDS = new ArrayList<>();
            while (scanner.hasNext()) {
                List<String> line = CSVUtils.parseLine(scanner.nextLine());
                Integer localId = Integer.valueOf(line.get(1));
                Integer centralId = Integer.valueOf(line.get(2));

                //pokupi zapis iz opstinske
                Record localRec = storage.get(conn, localId);

                if(localRec == null) {

                    System.out.println("Greska, ne postoji lokalni zapis sa brojem: " + localId);
                    errIDS.add(String.valueOf(localId));
                }

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

            FileUtils.writeTextFile("not_found_records.txt",errIDS.toString());
//            outPrimerci.println(JSONSerializer.toJSONPrimerciMap(mapPrimerci));
//            outGodine.println(JSONSerializer.toJSONGodineMap(mapGodine));
//
//            outGodine.close();
//            outPrimerci.close();
            scanner.close();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }




}
