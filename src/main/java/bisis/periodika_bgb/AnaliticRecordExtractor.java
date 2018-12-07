package bisis.periodika_bgb;

import bisis.coders.Counter;
import bisis.jongo_records.JoRecord;
import bisis.records.Author;
import bisis.records.Field;
import bisis.records.Subfield;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AnaliticRecordExtractor {

    public static final String SERIAL_PUB_CRITERIA = "{'fields': {$elemMatch: {'name': '001', 'subfields': {$elemMatch: {'name': 'c', 'content':'s'}}}}}";
    public static final int ANALITIC_TYPE = 3;

    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        Jongo jongo = new Jongo(mongoClient.getDB("bisis"));
        MongoCollection recordsMongoCollection = jongo.getCollection("bgb_records");
        MongoCollection codersCountersMongoCollection = jongo.getCollection("coders.counters");

        MongoCursor<JoRecord> analiticRecords = recordsMongoCollection.find(SERIAL_PUB_CRITERIA).as(JoRecord.class);
        Counter counterRn = codersCountersMongoCollection.findOne("{'library':'bgb', 'counterName':'RN'}").as(Counter.class);
        Counter counterRecordid = codersCountersMongoCollection.findOne("{'library':'bgb', 'counterName':'recordid'}").as(Counter.class);
        int rnCnt = counterRn.getCounterValue() + 1;
        int recIdCnt= counterRecordid.getCounterValue() + 1;

        int cnt = 0;
        while (analiticRecords.hasNext()) {
            JoRecord aRec = analiticRecords.next();
            if (aRec.getField("423") != null) {
                List<JoRecord> analitics = extractRec2Analitics(aRec, rnCnt, recIdCnt);
                rnCnt++;
                recIdCnt++;
                for (JoRecord rec: analitics) {
                    cnt++;
                    recordsMongoCollection.save(rec);
                }
                System.out.println("Records extracted: "+ cnt);
                //TODO brisanje polja 423
            }
        }
        System.out.println(analiticRecords.count());
        System.out.println("Total analitic records extracted:" + cnt);
        counterRn.setCounterValue(rnCnt);
        counterRecordid.setCounterValue(recIdCnt);
        codersCountersMongoCollection.update("{counterName: 'RN', library: 'bgb'}").with(counterRn);
        codersCountersMongoCollection.update("{counterName: 'recordid', library: 'bgb'}").with(counterRecordid);
        System.out.println("\nCOUNTERS FOR RN & RECORD_ID SET TO: " + rnCnt + " & " + recIdCnt);
    }

    private static List<JoRecord> extractRec2Analitics(JoRecord record, int rn, int record_id) {
        List<JoRecord> retVal = new ArrayList<>();

        for(Field f423: record.getFields("423")){
            JoRecord r = new JoRecord();
            r.setPubType(ANALITIC_TYPE);
            r.setRecordID(record_id);
            r.setRN(rn);
            r.setCreator(new Author("prepis 2018", "bgb"));
            r.setCreationDate(new Date());

            List<Field> allFields = new ArrayList<>();
            allFields.addAll(get001Generics(rn));
            if (format011(record) != null) {
                allFields.add(format011(record));
            }

            Field f100 = new Field("100");
            f100.getSubfields().add(record.getSubfield("100c"));
            allFields.add(f100);

            Field f101 = new Field("101");
            if (record.getSubfieldContent("101c") != null) {
                f101.add(new Subfield('c', record.getSubfieldContent("101c")));
                Field f210 = new Field("210");
                Subfield sf210d = new Subfield('d', record.getSubfieldContent("101c"));
                f210.add(sf210d);
                allFields.add(f210);
            }

            if (record.getSubfieldContent("101a") != null) {
                f101.add(new Subfield('a', record.getSubfieldContent("101a")));
            }

            allFields.add(f101);

            Field f215 = new Field("215");
            if (record.getSubfieldContent("200h") != null) {
                Subfield sf215h = new Subfield('h', record.getSubfieldContent("200h"));
                f215.add(sf215h);
            }
            if (record.getSubfieldContent("200i") != null) {
                Subfield sf215k = new Subfield('k', record.getSubfieldContent("200i"));
                f215.add(sf215k);
            }
            if (f215.getSubfield('i') != null || f215.getSubfield('h') != null)
                allFields.add(f215);

            for (Subfield sf: f423.getSubfields()) {
                if (sf.getSecField() != null) {
                    boolean containsSf = false;
                    for (Field field: allFields) {
                        if (field.getName().equals(sf.getSecField().getName())){
                            containsSf = true;
                            for (Subfield _sf: sf.getSecField().getSubfields()) {
                                if (field.getSubfield(sf.getName()) == null)
                                    field.add(_sf);
                            }
                            break;
                        }
                    }
                    if (!containsSf)
                        allFields.add(sf.getSecField());
                }
            }

            Field f474 = new Field("474");
            f474.add(new Subfield('1', String.valueOf(record.getRN())));
            allFields.add(f474);

            Field f992 = new Field("992");
            f992.add(new Subfield('b', "prepis2018"));
            allFields.add(f992);

            allFields.sort(Comparator.comparing(Field::getName));

            r.setFields(allFields);
            //r.pack();
            retVal.add(r);

        }

        return retVal;
    }

    private static Field format011(JoRecord record) {
        Field f011 = new Field("011");
        String _011Content = "";

        if (record.getSubfieldContent("011a") == null)
            return null;

        f011.add(new Subfield('a', record.getSubfieldContent("011a")));
        _011Content = record.getSubfieldContent("011a");
        if (record.getSubfieldContent("200a") != null)
            _011Content += "(" + record.getSubfieldContent("200a");
        if (record.getSubfieldContent("200e") != null)
            _011Content += " : " + record.getSubfieldContent("200e");
        _011Content += ")";
        f011.add(new Subfield('b', _011Content));

        return f011;
    }

    private static List<Field> get001Generics(int rn) {
        List<Field> retVal = new ArrayList<>();

        Field f001 = new Field("001");
        f001.add(new Subfield('a', "c"));
        f001.add(new Subfield('b', "a"));
        f001.add(new Subfield('c', "a"));
        f001.add(new Subfield('d', "2"));
        f001.add(new Subfield('e', Integer.toString(rn)));
        retVal.add(f001);

        return retVal;
    }
}
