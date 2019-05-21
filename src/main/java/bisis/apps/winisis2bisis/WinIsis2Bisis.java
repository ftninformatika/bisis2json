package bisis.apps.winisis2bisis;

import bisis.apps.prepis_bgb.MembersMerger;
import bisis.model.jongo_circ.JoLending;
import bisis.model.jongo_circ.JoMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class WinIsis2Bisis {

    private static ObjectMapper objectMapper = new ObjectMapper();
    public static MongoClient mongoClient = new MongoClient("localhost", 27017);
    public static DB mongoDatabase = null;

    public static void main(String[] args) {


        try {
            byte[] jsonDataMembers = Files.readAllBytes(Paths.get("./gbnsLendings.json"));
            byte[] jsonDataLendings = Files.readAllBytes(Paths.get("./gbnsMembers.json"));

//            JoMember[] parsedMembers = objectMapper.readValue(jsonDataMembers, JoMember[].class);
            List<JoMember> members = objectMapper.readValue(jsonDataMembers, objectMapper.getTypeFactory().constructCollectionType(List.class, JoMember.class));
            List<JoLending> lendings = objectMapper.readValue(jsonDataLendings, objectMapper.getTypeFactory().constructCollectionType(List.class, JoLending.class));

            mongoDatabase = mongoClient.getDB("bisis");
            MembersMerger membersMerger = new MembersMerger();
            membersMerger.mergeWinIsis2Bisis(mongoDatabase, null,null, true, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
