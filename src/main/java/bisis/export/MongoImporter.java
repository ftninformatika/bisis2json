package bisis.export;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

/**
 * Created by Petar on 11/24/2017.
 */
public class MongoImporter {

    private  MongoClient mongoClient = null;
    private  String library;
    private  String mongoDbName;
    private  MongoDatabase database;

    public MongoImporter(MongoClient mongoClient, String library, String monggoDbName){
        this.mongoClient = mongoClient;
        this.library = library;
        this.mongoDbName = monggoDbName;
        this.database = mongoClient.getDatabase(monggoDbName);
    }

    public void importRecords() throws Exception {

        InputStream is = new FileInputStream("export" + library.toUpperCase() + "/exportedRecords.json");
        JSONArray json = new JSONArray(IOUtils.toString(is, "UTF-8"));




        MongoCollection<Document> collection = database.getCollection(library + "_records");
        if (collection !=  null || collection.count() > 0){
            throw new Exception("Documents already exists in this collection!");
        }
        else{


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
