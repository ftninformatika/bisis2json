package bisis.apps.prepis_bgb;

import bisis.model.jongo_records.JoPrimerak;
import bisis.model.jongo_records.JoRecord;
import bisis.model.records.Field;
import bisis.model.records.Primerak;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

public class FillInvSigUDKFrom675a {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please enter library prefix!");
            return;
        }

        String libPref = args[0];

        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB db = mongoClient.getDB("bisis");
        Jongo jongo = new Jongo(db);
        MongoCollection recordsCollection = jongo.getCollection(libPref + "_records");

        int cnt = 0;

        MongoCursor<JoRecord> recsCur = recordsCollection.find().as(JoRecord.class);
        int totalCnt = recsCur.count();

        while (recsCur.hasNext()) {
            boolean touched = false;
            JoRecord rec = recsCur.next();
            if (rec == null) {
                continue;
            }
            String sigUdk = getSigUdk(rec);
            if (rec.getPrimerci() != null && rec.getPrimerci().size() > 0) {
                for(JoPrimerak p: rec.getPrimerci()) {
                    if ((p.getSigUDK() == null || "".equals(p.getSigUDK())) && !isPrimerak01(p)) {
                        p.setSigUDK(sigUdk);
                        touched = true;
                    }
                }
            }
            if (cnt % 100 == 0) {
                System.out.println("Processed " + cnt + " of " + totalCnt);
            }
            if (touched) {
                recordsCollection.save(rec);
            }
            cnt++;
        }
    }

    public static String getSigUdk(JoRecord record) {
        for (Field f : record.getFields("675")) {
            if (f.getSubfield('a') != null && !f.getSubfield('a').getContent().equals(""))
                return f.getSubfield('a').getContent();
        }
        return "";
    }

    public static boolean isPrimerak01(JoPrimerak p) {
        if (p == null) {
            return false;
        }
        else if (p.getOdeljenje() != null && p.getOdeljenje().startsWith("01")){
            return true;
        } else if (p.getSigPodlokacija() != null && p.getSigPodlokacija().startsWith("01")){
            return true;
        } else return p.getInvBroj() != null && p.getInvBroj().startsWith("01");
    }
}
