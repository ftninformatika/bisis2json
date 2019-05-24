package bisis.apps.winisis2bisis;

import bisis.apps.prepis_bgb.MembersMerger;
import bisis.model.jongo_circ.JoLending;
import bisis.model.jongo_circ.JoMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.json.JSONArray;

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

        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        try {
            byte[] jsonDataMembers = Files.readAllBytes(Paths.get("./gbnsMembersJava.json"));
            byte[] jsonDataLendings = Files.readAllBytes(Paths.get("./gbnsLendingsJava.json"));

            JSONArray jsonArray = new JSONArray(new String(jsonDataMembers));

//            List<JoMember> members = new ArrayList<>();
//
//            for (Object jo: jsonArray) {
//                JoMember jm = objectMapper.convertValue(jo, JoMember.class);
//                members.add(jm);
//            }

            List<JoMember> members = objectMapper.readValue(jsonDataMembers, objectMapper.getTypeFactory().constructCollectionType(List.class, JoMember.class));
            List<JoLending> lendings = objectMapper.readValue(jsonDataLendings, objectMapper.getTypeFactory().constructCollectionType(List.class, JoLending.class));

            mongoDatabase = mongoClient.getDB("bisis");
            MembersMerger membersMerger = new MembersMerger();
            membersMerger.mergeWinIsis2Bisis(mongoDatabase, members,lendings, true, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
