package bisis.apps.prepis_bgb;

import bisis.model.coders.Counter;
import bisis.model.jongo_records.JoDupRec;
import bisis.model.jongo_records.JoRecord;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.jongo.Aggregate;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

public class FixDuplicateAndNullRNs {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Unesite prefiks biblioteke kao parametar!");
        }
        String libPref = args[0];
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB db = mongoClient.getDB("bisis");
        Jongo jongo = new Jongo(db);
        MongoCollection recordsCollection = jongo.getCollection(libPref + "_records");
        MongoCollection counterCoderCollection = jongo.getCollection("coders.counters");
        Counter rnCounter = counterCoderCollection.findOne("{ library:'"+ libPref +"', counterName: 'RN' }").as(Counter.class);
        int nextRn = rnCounter.getCounterValue();

        //-----------NULLs-------------------
        MongoCursor<JoRecord> recsWithNull = recordsCollection.find("{rn: null}").as(JoRecord.class);
        System.out.println("Started changing records with NULL RN values...");
        while (recsWithNull.hasNext()) {
            JoRecord record = recsWithNull.next();
            nextRn++;
            record.setRN(nextRn);
            recordsCollection.save(record);
        }

        //-----------DUPLICATES--------------
        Aggregate.ResultsIterator<JoDupRec> dupRecsIterator = recordsCollection.aggregate("{$group: {_id:'$rn', dups: { $addToSet: '$_id'}, count: {$sum:1}}}}")
                         .and("{$match: { count: {'$gt':1}}}").as(JoDupRec.class);

        System.out.println("Started processing records with duplicate RNs...");
        while(dupRecsIterator.hasNext()) {
            JoDupRec dupRec = dupRecsIterator.next();
            for(ObjectId _id: dupRec.getDups()) {
                nextRn++;
                JoRecord rec = recordsCollection.findOne(_id).as(JoRecord.class);
                rec.setRN(nextRn);
                recordsCollection.save(rec);
            }
        }
        nextRn++;
        rnCounter.setCounterValue(nextRn);
        counterCoderCollection.save(rnCounter);
        System.out.println("Done!");
    }

}
