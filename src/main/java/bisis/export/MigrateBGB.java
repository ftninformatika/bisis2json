package bisis.export;

import bisis.records.Record;
import bisis.textsrv.DBStorage;
import bisis.utils.CSVUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.io.*;
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

            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("without_rn.csv"), "UTF8")));
            StringBuffer withoutRn = new StringBuffer();
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false&serverTimezone=CET"
                    , "bisis", "bisis");
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("select record_id from Records");
            //broj zapisa u lokalu
            List<Integer> localRecIds = new ArrayList<>();
            while(rs.next())
                localRecIds.add(rs.getInt("record_id"));
            stm.close();

            DB db = new MongoClient().getDB("bisis");
            Jongo jongo = new Jongo(db);
            MongoCollection centralRecs = jongo.getCollection("bgb_records");

            Scanner scanner = new Scanner(new File(MigrateBGB.class.getResource("/bisis/records-map/records_map.csv").getPath()));
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
            scanner.close();

            Map<Integer, String> withoutReferenceMap = new HashMap<>();
            int cnt = 0;
            for (Integer id: localRecIds) {
                cnt++;
                Record localRec = storage.get(conn, id);

                if (localRec != null) {

                    if(localRec.getRN() == 0) {
                        withoutRn.append(localRec.getRN() + "," + localRec.getRecordID());
                        withoutRn.append("\n");
                        System.out.println("Nema rn: " + localRec.getRecordID());
                        continue;
                    }

                    Integer centralRN = localCentralMap.get(localRec.getRN());

                    if (centralRN == null) {
                        withoutRn.append(localRec.getRN() + "," + localRec.getRecordID());
                        withoutRn.append("\n");
                        System.out.println("Nema referencu na zapis! recId: " + localRec.getRecordID());
                        continue;
                    }

                    Record centralRec = centralRecs.findOne("{rn:" + centralRN + "}").as(Record.class);

                    if (centralRec == null) {
                        withoutRn.append(localRec.getRN() + "," + localRec.getRecordID());
                        withoutRn.append("\n");
                        System.out.println("Centralni zapis je null! recId: " + localRec.getRecordID());
                        continue;

                    }
                    else {

                        if(!localRec.isInvetarPrazan()) {
                            centralRec.addInventar(localRec);

                            centralRecs.save(centralRec);
                        }
                        else {
//                            withoutRn.append(localRec.getRN() + "," + localRec.getRecordID());
//                            withoutRn.append("\n");
                            System.out.println("Prazan inventar za zapis! recId: " + localRec.getRecordID());
                        }
                        if (cnt % 1000 == 0)
                            System.out.println("Processed: " + cnt + " records");
                    }
                }
                else {
                    withoutRn.append(localRec.getRN() + "," + localRec.getRecordID());
                    withoutRn.append("\n");
                    System.out.println("Lokalni zapis je null!");
                }

            }
            System.out.println(cnt);
            out.write(withoutRn.toString());
            out.close();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }




}
