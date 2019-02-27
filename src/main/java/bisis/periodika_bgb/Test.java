package bisis.periodika_bgb;

import bisis.coders.Counter;
import bisis.jongo_records.JoRecord;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.*;

public class Test {

    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient("localhost", 27020);
        Jongo jongo = new Jongo(mongoClient.getDB("bisis"));
        MongoCollection recordsMongoCollection = jongo.getCollection("bgb_records");
        MongoCollection codersCountersMongoCollection = jongo.getCollection("coders.counters");

        MongoCursor<JoRecord> periodicRecords = recordsMongoCollection.find("{'godine.invBroj': {$regex: #}}", "014.*").as(JoRecord.class);
        Counter counterRn = codersCountersMongoCollection.findOne("{'library':'bgb', 'counterName':'RN'}").as(Counter.class);
        Counter counterRecordid = codersCountersMongoCollection.findOne("{'library':'bgb', 'counterName':'recordid'}").as(Counter.class);

        List<String> issnList = new ArrayList<>();
        while (periodicRecords.hasNext()) {
            System.out.println(periodicRecords.next());
            try {
                JoRecord zapis = periodicRecords.next();
                if (zapis.getSubfieldContent("011e") != null )
                    issnList.add(zapis.getSubfieldContent("011e"));

                else if (zapis.getSubfieldContent("011c") != null )
                    issnList.add(zapis.getSubfieldContent("011c"));
            }
            catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        issnList.sort(Comparator.naturalOrder());
        for (String s: issnList)
            System.out.println(s);

        System.out.println("Ukupno: " + issnList.size());
        System.out.println("Ukupno razlicitih: " + new HashSet<String>(issnList).size());
    }
}
