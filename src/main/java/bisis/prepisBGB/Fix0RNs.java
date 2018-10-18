package bisis.prepisBGB;

import bisis.coders.Counter;
import bisis.jongo_records.JoRecord;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

/**
 *  Izvrsiti ako se radi prepis iz ogranaka, extract- uje RN iz zapisa u kolonu MySQL,
 *  neophodno radi izvrsavanja import skripti napisanih u python- u
 */
public class Fix0RNs {

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Please enter arguments: mongodb host{1} port{2} and library prefix{3}");
            System.exit(0);
        }

        MongoClient mongoClient = new MongoClient(args[0], Integer.parseInt(args[1]));
        DB db = mongoClient.getDB("bisis");
        Jongo jongo = new Jongo(db);
        MongoCollection recordsCollection = jongo.getCollection(args[2] + "_records");

        MongoCursor<JoRecord> _0RnRecords = recordsCollection.find("{ rn:0 }").as(JoRecord.class);
        MongoCollection codersCounters = jongo.getCollection("coders.counters");
        Counter counterRn = codersCounters.findOne("{'library':'bgb', 'counterName':'RN'}").as(Counter.class);
        int lastRn = counterRn.getCounterValue();

        if (!_0RnRecords.hasNext()) {
            System.out.println("No records with RN = 0, for library: " + args[2]);
            System.exit(0);
        }

        System.out.println("RN counter is: " + lastRn);
        {
            while (_0RnRecords.hasNext()) {
                JoRecord record = _0RnRecords.next();
                // promeni u metapodacima i u poljima
                record.setRN(lastRn);
                recordsCollection.save(record);
                System.out.println(record.get_id() + " had RN 0, updated to: " + lastRn);
                lastRn++;
            }
        }

        counterRn.setCounterValue(lastRn);
        // sacuvaj RN brojac
        codersCounters.update("{'library':'bgb', 'counterName':'RN'}").with(counterRn);
        System.out.println("RN counter set to: " + lastRn);
    }
}
