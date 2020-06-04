package bisis.apps.krupanj;

import bisis.model.jongo_records.JoRecord;
import bisis.model.records.Field;
import bisis.model.records.Subfield;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author badf00d21  6.3.20.
 */
public class KrupanjFix {

    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient("localhost", 27018);
        Jongo jongo = new Jongo(mongoClient.getDB("bisis"));
        MongoCollection recordsMongoCollection = jongo.getCollection("bpk_records");

        MongoCursor<JoRecord> analiticRecords = recordsMongoCollection.find().as(JoRecord.class);
        while (analiticRecords.hasNext()) {
            boolean saveInd = false;
            JoRecord rec = analiticRecords.next();
            System.out.println(rec);

            Map<String, List<Field>> _70xFeldMap = new HashMap<>();
            _70xFeldMap.put("700", rec.getFields("700"));
            _70xFeldMap.put("701", rec.getFields("701"));
            _70xFeldMap.put("702", rec.getFields("702"));
            for (String fKey: _70xFeldMap.keySet()) {
                List<Field> fields = _70xFeldMap.get(fKey);
                if (fields == null || fields.size() == 0)
                    continue;
                for (Field f: fields) {
                    if (f.getSubfieldContent('4') == null || f.getSubfieldContent('4').trim().equals(""))
                        continue;
                    String[] contentSplit = f.getSubfieldContent('4').split(",");
                    if (contentSplit.length <= 1)
                        continue;
                    for (int i = 0; i < contentSplit.length; i++) {
                        String con = contentSplit[i].trim();
                        if (i == 0) {
                            saveInd = true;
                            f.getSubfield('4').setContent(con);
                        } else {
                            saveInd = true;
                            Subfield sf = new Subfield();
                            sf.setName('4');
                            sf.setContent(con);
                            f.add(sf);
                            f.pack();
                            f.sort();
                        }
                    }
                }
            }

            List<Field> _105s = rec.getFields("105");
            for (Field f: _105s) {
                List<Subfield> sfs = f.getSubfields('f');
                if (sfs == null || sfs.size() == 0)
                    continue;
                for (Subfield sf: sfs) {
                    if (sf.getContent() == null)
                        continue;
                    String[] parts = sf.getContent().split("( |-)");
                    if (parts.length > 1) {
                        sf.setContent(parts[0]);
                        saveInd = true;
                    }
                }
            }

            if (saveInd)
                recordsMongoCollection.save(rec);
        }
    }
}
