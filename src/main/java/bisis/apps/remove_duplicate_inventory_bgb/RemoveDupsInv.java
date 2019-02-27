package bisis.apps.remove_duplicate_inventory_bgb;

import bisis.model.jongo_records.JoRecord;
import bisis.model.records.ItemAvailability;
import bisis.utils.CSVUtils;
import com.mongodb.client.*;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RemoveDupsInv {

    public static final String DEV_MONGO_DB_URI = "mongodb://bisis:bisis@192.168.200.1:27017,192.168.200.3:27017,192.168.200.46:27017/?replicaSet=bisis5";
    public static final String PROD_MONGO_DB_URI = "mongodb://bisis1:27017,bisis2:27017,bisis3:27017/?replicaSet=bisis5";
    public static final String DB_NAME = "bisis";

    public static void main(String[] args) throws IOException {
        MongoClient mongoClient = MongoClients.create(DEV_MONGO_DB_URI);
        MongoDatabase db = mongoClient.getDatabase(DB_NAME);
        MongoCollection recordsCollection = db.getCollection("bgb_records");
        MongoCollection itemAvailibilitiesCollection = db.getCollection("bgb__itemAvailability");

        Scanner scanner = new Scanner(RemoveDupsInv.class.getResourceAsStream("/duplicateItems.csv"));
        while(scanner.hasNext()) {
            List<String> line = CSVUtils.parseLine(scanner.nextLine());
            String invNum = line.get(0);
            if (invNum != null) {
                List<ItemAvailability> dupItems = new ArrayList<>();
                itemAvailibilitiesCollection.find(new Document("ctlgNo", invNum)).into(dupItems);
                List<JoRecord> dupRecs = new ArrayList<>();
                recordsCollection.find(new Document("primerci.invBroj", invNum)).into(dupRecs);
                System.out.println("a");
            }
        }
    }
}
