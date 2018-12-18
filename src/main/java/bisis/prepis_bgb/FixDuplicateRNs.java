package bisis.prepis_bgb;

import bisis.coders.Counter;
import bisis.jongo_records.JoDupRec;
import bisis.jongo_records.JoRecord;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.jongo.Aggregate;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

public class FixDuplicateRNs {

    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient("localhost", 27018);
        DB db = mongoClient.getDB("bisis");
        Jongo jongo = new Jongo(db);
        MongoCollection recordsCollection = jongo.getCollection("bgb_records");
        MongoCollection counterCoderCollection = jongo.getCollection("coders.counters");

        Aggregate.ResultsIterator<JoDupRec> dupRecsIterator = recordsCollection.aggregate("{$group: {_id:'$rn', dups: { $addToSet: '$_id'}, count: {$sum:1}}}}")
                         .and("{$match: { count: {'$gt':1}}}").as(JoDupRec.class);

        Counter rnCounter = counterCoderCollection.findOne("{ library:'bgb', counterName: 'RN' }").as(Counter.class);
        int nextRn = rnCounter.getCounterValue();
        System.out.println("Started processing records...");
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
