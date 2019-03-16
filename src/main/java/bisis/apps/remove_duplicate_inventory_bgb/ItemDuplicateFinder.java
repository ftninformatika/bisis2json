package bisis.apps.remove_duplicate_inventory_bgb;

import bisis.utils.LimitedOutputStream;
import bisis.utils.ProgressBar;
import com.mongodb.client.*;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ItemDuplicateFinder {

    public static final String DEV_MONGO_DB_URI = "mongodb://bisis:bisis@192.168.200.1:27017,192.168.200.3:27017,192.168.200.46:27017/?replicaSet=bisis5";
    public static final String PROD_MONGO_DB_URI = "mongodb://bisis1:27017,bisis2:27017,bisis3:27017/?replicaSet=bisis5";
    public static final String TUNNEL_PROD_DB_URI = "mongodb://localhost:27018";
    public static final String DB_NAME = "bisis";

    // TODO - refactor to be generic and applicable to other libraries
    public static void main(String[] args) throws IOException {
        MongoClient mongoClient = MongoClients.create(TUNNEL_PROD_DB_URI);
        MongoDatabase db = mongoClient.getDatabase(DB_NAME);
        MongoCollection recordsCollection = db.getCollection("bgb_records");
        MongoCollection itemAvailibilitiesCollection = db.getCollection("bgb_itemAvailability");
        MongoCollection otherIACollection = db.getCollection("bgb_ItemAvailability");
        MongoCollection locationCollection = db.getCollection("coders.location");

        MongoCollection newICollection = db.getCollection("bgb_itemAvailability");
        IndexOptions indexOptions = new IndexOptions().unique(true);
        newICollection.createIndex(Indexes.descending("rn"));
        newICollection.createIndex(Indexes.descending("recordID"));
        newICollection.createIndex(Indexes.ascending("ctlgNo"), indexOptions);

        Map<String, String> locations = getLocationMap(locationCollection);


        createItemAvailabilityCollection(recordsCollection, newICollection, locations);
//        writeItemsToFile(recordsCollection, Paths.get("./primerci_bgb.csv"));
//        checkDuplicatedCollection(otherIACollection, itemAvailibilitiesCollection);
    }

    private static Map<String, String> getLocationMap(MongoCollection locationCollection) {
        Map<String, String> retVal = new HashMap<>();
        Document filter = new Document();
        filter.append("library", "bgb");
        Iterator<Document> iterator = locationCollection.find(filter).iterator();
        while(iterator.hasNext()) {
            Document loc = iterator.next();
            if (retVal.containsKey(loc.get("coder_id")))
                System.out.println("Greska");
            else
                retVal.put(loc.getString("coder_id"), loc.getString("description"));
        }
        return retVal;
    }

    private static void writeItemsToFile(MongoCollection recCollection, Path outputFilePath) {
        final long SIZE_1GB = 1073741824L;
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new LimitedOutputStream(Files.newOutputStream(outputFilePath), SIZE_1GB),
                StandardCharsets.UTF_8))) {
            Document projection = new Document();
            projection.put("primerci", 1);
            projection.put("rn",1);
            projection.put("recordID", 1);
            projection.put("primerci.invBroj", 1);

            MongoCursor<Document> docRecs = recCollection.find().projection(projection).iterator();
            int total = Math.toIntExact(recCollection.count());
            int cnt = 0;
            ProgressBar progressBar = new ProgressBar();
            System.out.println("Starting writing items!");
            while (docRecs.hasNext()) {
                Document rDoc = docRecs.next();
                Integer rn  = rDoc.getInteger("rn");
                Integer recordID = rDoc.getInteger("recordID");
                if (rDoc.get("primerci") != null) {
                    List<Document> primerci = rDoc.getList("primerci", Document.class);
                    for (Document p: primerci) {
                        writer.write("\"" + p.get("invBroj") + "\"," + rn + "," + recordID + "\n");
                    }
                }
                cnt++;
                progressBar.update(cnt, total);
            }
            System.out.println("Total items processes: " + cnt);
        }
        catch (IOException e) {
            System.out.println("Invalid output file path!");
            e.printStackTrace();
        }
    }

//    private static void checkDuplicatedCollection(MongoCollection duplicated, MongoCollection original) {
//        Iterator<Document> iterator = duplicated.find().iterator();
//        List<Object> nonExistingList = new ArrayList<>();
//        while (iterator.hasNext()) {
//            Document doc = iterator.next();
//            Document findFilter = new Document();
//            findFilter.append("ctlgNo", doc.get("ctlgNo"));
//            findFilter.append("recordID", doc.get("recordID"));
//            findFilter.append("libDepartment", doc.get("libDepartment"));
//            Object d = original.find(findFilter).first();
//            if (d == null) {
//                nonExistingList.add(d);
//            }
//        }
//        System.out.println(nonExistingList.size());
//
//    }

    private static void createItemAvailabilityCollection(MongoCollection recCollection, MongoCollection newCollection, Map<String, String> locations) {
        Document projection = new Document();
        projection.put("primerci", 1);
        projection.put("rn",1);
        projection.put("recordID", 1);
        projection.put("primerci.invBroj", 1);

        MongoCursor<Document> docRecs = recCollection.find().projection(projection).iterator();
        int total = Math.toIntExact(recCollection.count());
        int cnt = 0;
        ProgressBar progressBar = new ProgressBar();
        System.out.println("Starting writing items!");
        while (docRecs.hasNext()) {
            Document rDoc = docRecs.next();
            Integer rn  = rDoc.getInteger("rn");
            Integer recordID = rDoc.getInteger("recordID");
            List<Document> newItems = new ArrayList<>();
            if (rDoc.get("primerci") != null) {
                List<Document> primerci = rDoc.getList("primerci", Document.class);
                for (Document p: primerci) {
                    String invBroj = p.getString("invBroj");
                    String libDepartment = locations.get("01");
                    try {
                        libDepartment = locations.get(invBroj.substring(0,2));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    Document item = new Document();
                    item.append("ctlgNo", invBroj);
                    item.append("rn", rn);
                    item.append("recordID", String.valueOf(recordID));
                    item.append("libDepartment", libDepartment);
                    item.append("borrowed", false);
                    newItems.add(item);
                }
            }
            if (newItems.size() > 0)
                newCollection.insertMany(newItems);
            cnt++;
            progressBar.update(cnt, total);
        }
        System.out.println("Total items processes: " + cnt);
    }


}
