package bisis.export;

import bisis.coders.Coder;
import bisis.jongo_records.JoPrimerak;
import bisis.jongo_records.JoRecord;
import bisis.records.ItemAvailability;
import bisis.textsrv.DBStorage;
import bisis.utils.CSVUtils;
import bisis.utils.RecordUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.io.*;
import java.sql.*;
import java.util.*;

public class MigrateBGB {

    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("Please enter mysqldb name{1} and branch prefix{2} and generate(g)/insert(i) switch{3}");
            return;
        }

        String dbName = args[0];
        String branchPrefix = args[1];
        String _switch = args[2];

        try {

            PrintWriter out = new PrintWriter(new File("without_rn.csv"));
            PrintWriter outValidRecordsMap = new PrintWriter(new File("valid_records_map.csv"));
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
            MongoCollection centralItemAvailabilities = jongo.getCollection("bgb_itemAvailability");
            MongoCollection locationCollection = jongo.getCollection("coders.location");
            String locationDescription = locationCollection.findOne("{'library':'bgb', 'coder_id':#}", branchPrefix).as(Coder.class).getDescription();

            Scanner scanner = new Scanner(new File(MigrateBGB.class.getResource("/bisis/records-map/records_map.csv").getPath()));
            DBStorage storage = new DBStorage();

            // mapa lokalniRn - centralaRn
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
                JoRecord localRec = new JoRecord(storage.get(conn, id));

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

                    JoRecord centralRec = centralRecs.findOne("{rn:" + centralRN + "}").as(JoRecord.class);

                    if (centralRec == null) {
                        withoutRn.append(localRec.getRN() + "," + localRec.getRecordID());
                        withoutRn.append("\n");
                        System.out.println("Centralni zapis je null! recId: " + localRec.getRecordID());
                        continue;

                    }
                    else {

                        // upis u validnu mapu
                        if(!_switch.equals("i")) {
                            outValidRecordsMap.write("date_generated," + localRec.getRN() + "," + centralRN + "\n");
                        }

                        // switch za prepis
                        if(!localRec.isInvetarPrazan() && _switch.equals("i")) {
                            for (JoPrimerak p: localRec.getPrimerci()) {
                                if (!centralRec.containsPrimerak(p)) {
                                    centralRec.getPrimerci().add(p);
                                    centralRecs.save(centralRec);
                                    ItemAvailability ia = RecordUtils.makeItemAvailabilyForRec(centralRec, p.getInvBroj(), locationDescription);
                                    centralItemAvailabilities.save(ia);
                                }
                                else
                                    System.out.println("Nasao dupli inv za: " + p.getInvBroj());
                            }
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
            outValidRecordsMap.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
