package bisis.export;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.File;
import java.util.Set;

/**
 * Created by Petar on 11/24/2017.
 */
public class MongoImporter {

    private  MongoClient mongoClient = null;
    private  String library;
    private  String mongoDbName;
    private  MongoDatabase database;
    private static JsonFactory jsonFactory;

    public MongoImporter(MongoClient mongoClient, String library, String monggoDbName){
        this.mongoClient = mongoClient;
        this.library = library;
        this.mongoDbName = monggoDbName;
        this.database = mongoClient.getDatabase(monggoDbName);
    }

    public void importRecords() throws Exception {

        MongoCollection<Document> collection = database.getCollection(library + "_records");
        if (collection !=  null || collection.count() > 0){
            throw new Exception("Documents alredy exists in this collection!");
        }
        else{
            JsonParser jp = jsonFactory.createJsonParser(new File("export/"+library + "_records"));

        }
    }

    public boolean collectionExists(final String collectionName) {
        Set<String> collectionNames = (Set<String>) database.listCollectionNames();
        for (final String name : collectionNames) {
            if (name.equalsIgnoreCase(collectionName)) {
                return true;
            }
        }
        return false;
    }
}
