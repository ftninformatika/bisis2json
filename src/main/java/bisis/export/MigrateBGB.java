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

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
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
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("select count(*) as total from Records");
            //broj zapisa u lokalu
            Integer totalLocalRecs = 0;
            while(rs.next())
             totalLocalRecs = rs.getInt("total");
            stm.close();
            DB db = new MongoClient().getDB("bisis");
            Jongo jongo = new Jongo(db);
            MongoCollection centralRecs = jongo.getCollection("bgb_records");

            Scanner scanner = new Scanner(new File(MigrateBGB.class.getResource("/bisis/records-map/record_map.csv").getPath()));
            DBStorage storage = new DBStorage();

            //mapa lokalniRn - centralaRn
            Map<Integer, Integer> localCentralMap = new HashMap<>();
            while (scanner.hasNext()) {
                try {
                    List<String> line = CSVUtils.parseLine(scanner.nextLine());
                    localCentralMap.put(Integer.valueOf(line.get(1)), Integer.valueOf(line.get(2)));
                } catch (Exception e) {
                    continue;
                }
            }

            Map<Integer, String> withoutReferenceMap = new HashMap<>();

            for (int i = 1; i < totalLocalRecs; i++) {
                Record localRec = storage.get(conn, i);


                Integer centralRN = null;

                try {
                    centralRN = localCentralMap.get(localRec.getRN());
                } catch (Exception e) {
                    //withoutReferenceMap.put(localRec.getRN(), localRec.getSubfieldContent("010a"));
                    System.out.println("Ne moze da pokupi rn iz lokala za recordId: " + i);
                    continue;
                }

                //ako nema referencu izvuci njegov rn i njegov isbn
                if (centralRN == null) {
                    //pokusaj preko isbn da ga izvuces i postavis centralRN
                    if (localRec.getSubfieldContent("010a") != null && !localRec.getSubfieldContent("010a").trim().equals("")) {
                        Record isbnRec = centralRecs.findOne("{\"fields.subfields.content\":  \"" + localRec.getSubfieldContent("010a") + "\"}").as(Record.class);
                        centralRN = isbnRec.getRN();
                    } else {
                        withoutReferenceMap.put(localRec.getRN(), localRec.getSubfieldContent("010a"));
                        continue;
                    }
                }

                Record centralRec = centralRecs.findOne("{rn:" + centralRN + "}").as(Record.class);

                if (localRec == null || centralRec == null) {
                    System.out.println("Lokalni/centralni zapis je null! recId: " + i);
                    continue;

                }

                if( centralRec != null && localRec != null) {
                    if (centralRec.getPrimerci() == null)
                        centralRec.setPrimerci(new ArrayList<Primerak>());

                    centralRec.getPrimerci().addAll(localRec.getPrimerci());

                    if (centralRec.getGodine() == null)
                        centralRec.setGodine(new ArrayList<Godina>());

                    centralRec.getGodine().addAll(localRec.getGodine());

                    centralRecs.save(centralRec);

                    if (i % 1000 == 0)
                        System.out.println("Processed: " + i + " records");
                }

            }


            FileUtils.writeTextFile("without_reference.txt",withoutReferenceMap.toString());
            scanner.close();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



    }




}
