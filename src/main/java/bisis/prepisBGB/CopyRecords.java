package bisis.prepisBGB;

import bisis.coders.Coder;
import bisis.coders.Counter;
import bisis.jongo_records.JoRecord;
import bisis.records.ItemAvailability;
import bisis.textsrv.DBStorage;
import bisis.utils.CSVUtils;
import bisis.utils.RecordUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *  Namenjeno za prepis slikovnica, koje svaka opstinska ima posebno
 */

public class CopyRecords {

    public static void main(String[] args) {

        if(args.length != 2) {
            System.out.println("Please enter mysqldb name{1} and branch prefix{2}");
            return;
        }

        String dbName = args[0];
        String branchPrefix = args[1];


        StringBuffer withoutRn = new StringBuffer();
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false&serverTimezone=CET"
                    , "bisis", "bisis");
            Scanner scanner = new Scanner(new File(CopyRecords.class.getResource("/bisis/picturebooks.csv").getPath()));
            DBStorage storage = new DBStorage();
            DB db = new MongoClient().getDB("bisis");
            Jongo jongo = new Jongo(db);
            MongoCollection centralRecs = jongo.getCollection("bgb_records");
            MongoCollection centralItemAvailabilities = jongo.getCollection("bgb_itemAvailability");
            MongoCollection locationCollection = jongo.getCollection("coders.location");
            String locationDescription = locationCollection.findOne("{'library':'bgb', 'coder_id':#}", branchPrefix).as(Coder.class).getDescription();

            MongoCollection codersCounters = jongo.getCollection("coders.counters");
            Counter counterRn = codersCounters.findOne("{'library':'bgb', 'counterName':'RN'}").as(Counter.class);
            Counter counterRecordid = codersCounters.findOne("{'library':'bgb', 'counterName':'recordid'}").as(Counter.class);
            int rnCnt = counterRn.getCounterValue();
            int recIdCnt= counterRecordid.getCounterValue();

            List<Integer> picturebooksIds = new ArrayList<>();
            while (scanner.hasNext()) {
                List<String> line = CSVUtils.parseLine(scanner.nextLine());
                picturebooksIds.add(Integer.valueOf(line.get(1)));
            }

            for (int picturebookId: picturebooksIds) {
                JoRecord localRec = new JoRecord(storage.get(conn, picturebookId));

                if(localRec == null) {
                    System.out.println("Local record is null! " + picturebookId);
                }
                else {
                    localRec.setRN(rnCnt);
                    localRec.setRecordID(recIdCnt);
                    rnCnt++;
                    recIdCnt++;
                    List<ItemAvailability> items = RecordUtils.makeItemAvailabilitesForRec(localRec, locationDescription);
                    centralRecs.save(localRec);
                    for(ItemAvailability i: items)
                        centralItemAvailabilities.save(i);
                }

            }

            counterRn.setCounterValue(rnCnt);
            counterRecordid.setCounterValue(recIdCnt);
            codersCounters.update("{counterName: 'RN', library: 'bgb'}").with(counterRn);
            codersCounters.update("{counterName: 'recordid', library: 'bgb'}").with(counterRecordid);
            System.out.println("\nBROJACI PODESENI ZA RN I RECORD_ID NA: " + rnCnt + " i " + recIdCnt);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
