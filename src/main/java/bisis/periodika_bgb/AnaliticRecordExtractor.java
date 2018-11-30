package bisis.periodika_bgb;

import bisis.jongo_records.JoRecord;
import bisis.records.Field;
import bisis.records.Subfield;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;

public class AnaliticRecordExtractor {

    public static final String ANALITIC_CRITERIA = "{'fields': {$elemMatch: {'name': '001', 'subfields': {$elemMatch: {'name': 'c', 'content':'s'}}}}}";
    public static final int ANALITIC_TYPE = 3;

    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        Jongo jongo = new Jongo(mongoClient.getDB("bisis"));
        MongoCollection recordsMongoCollection = jongo.getCollection("bgb_records");

        MongoCursor<JoRecord> analiticRecords = recordsMongoCollection.find(ANALITIC_CRITERIA).as(JoRecord.class);
        int cnt = 0;
        while (analiticRecords.hasNext()) {
            JoRecord aRec = analiticRecords.next();
            if (aRec.getField("423") != null) {
                List<JoRecord> analitics = extractRec2Analitics(aRec);
                System.out.println(analitics);
                cnt++;
            }
        }
        System.out.println(analiticRecords.count());
        System.out.println(cnt);
    }

    private static List<JoRecord> extractRec2Analitics(JoRecord record) {
        List<JoRecord> retVal = new ArrayList<>();

        for(Field f423: record.getFields("423")){
            JoRecord r = new JoRecord();
            r.setPubType(ANALITIC_TYPE);

            List<Field> allFields = new ArrayList<>();
            allFields.addAll(getGenericAnaliticFields());
            for (Subfield sf: f423.getSubfields()) {
                if (sf.getSecField() != null) {
                    allFields.add(sf.getSecField());
                }
            }
            r.setFields(allFields);
            r.pack();
            retVal.add(r);

        }


        return retVal;
    }

    private static List<Field> getGenericAnaliticFields() {
        List<Field> retVal = new ArrayList<>();

        Field f001 = new Field("001");
        f001.add(new Subfield('a', "c"));
        f001.add(new Subfield('b', "a"));
        f001.add(new Subfield('c', "a"));
        f001.add(new Subfield('d', "2"));
        retVal.add(f001);

        return retVal;
    }
}
