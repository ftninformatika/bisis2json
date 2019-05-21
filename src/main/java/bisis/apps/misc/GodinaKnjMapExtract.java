package bisis.apps.misc;

import bisis.model.jongo_records.JoRecord;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author badf00d21  16.4.19.
 * for temp usage...
 */
public class GodinaKnjMapExtract {

//    public static void main(String[] args) {
//        MongoClient localClient = new MongoClient("localhost", 27021);
//        MongoClient prodTunnelClient = new MongoClient("localhost", 27018);
//        DB dbLocal = localClient.getDB("bisis");
//        Jongo jongoLocal = new Jongo(dbLocal);
//        DB dbProd = prodTunnelClient.getDB("bisis");
//        Jongo jongoProd = new Jongo(dbProd);
//
//        MongoCollection collLocalRec = jongoLocal.getCollection("bgb_records");
//        MongoCollection collProdRec = jongoProd.getCollection("bgb_records");
//
//        MongoCursor<JoRecord> currLocalRecs = collLocalRec.find("{pubType:2}").as(JoRecord.class);
//        int cnt = 0;
//        while (currLocalRecs.hasNext()) {
//            JoRecord localRec = currLocalRecs.next();
//            collProdRec.save(localRec);
//            cnt++;
//        }
//        System.out.println("Records processed: " + cnt);
//    }

    public static void main(String[] args) throws IOException {
                                                                    // test app port
        MongoClient mongoClient = new MongoClient("localhost", 27021);
        DB db = mongoClient.getDB("bisis");
        Jongo jongo = new Jongo(db);
        MongoCollection collRecords = jongo.getCollection("bgb_records");

        Scanner scanner = new Scanner(new File("godInvKnjMap.csv"));
        String line = "";
        Map<String, String> invKnjMap = new HashMap<>();
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            String invBr = null;
            String knj = null;
            try {
                String[] chunks = line.split("\\|");
                invBr = chunks[0];
                knj = chunks[1];
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (invBr == null || knj == null) {
                System.out.println("err parsing csv ");
                continue;
            }
            knj = knj.trim() + " ";
            invKnjMap.put(invBr, knj);
        }
        scanner.close();
        MongoCursor<JoRecord> currRec = collRecords.find("{pubType:2}").as(JoRecord.class);
        int cnt = 0;
        Set<String> checkedInvs = new HashSet<>();
        while (currRec.hasNext()) {
            JoRecord rec = currRec.next();
            for (String inv: invKnjMap.keySet()) {
                if (rec.getGodina(inv) != null) {
//                    System.out.println("menjaj");
                    rec.getGodina(inv).setBroj(invKnjMap.get(inv) + rec.getGodina(inv).getBroj());
                    System.out.println("stoj");
//                    collRecords.save(rec);
                    cnt++;
                    checkedInvs.add(inv);
                }
            }
        }
        Set<String> remaining = invKnjMap.keySet();
        remaining.removeAll(checkedInvs);
        for (String rem: remaining) {
            System.out.println(rem + ": " + invKnjMap.get(rem));
        }
        System.out.println("Modified godinas: " + cnt);
        System.out.println(checkedInvs);
    }
}
