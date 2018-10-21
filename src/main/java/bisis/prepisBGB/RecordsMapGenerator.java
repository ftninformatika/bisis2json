package bisis.prepisBGB;

import bisis.coders.Coder;
import bisis.jongo_records.JoRecord;
import bisis.textsrv.DBStorage;
import bisis.utils.CSVUtils;
import bisis.utils.ProgressBar;
import com.mongodb.DB;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @author Petar
 */

public class RecordsMapGenerator {

    public static final String VALID_RECORDS_MAP_FILE_NAME_CHUNK = "_valid_records_map.csv";
    public static final String PICTUREBOOKS_FILE_NAME_CHUNK = "_picturebooks.csv";
    public static final String UNPAIRED_RECORDS_FILE_NAME_CHUNK = "_unpaired.csv";
    public static final String ERR_RECORDS_FILE_NAME_CHUNK = "_errRecords.txt";

    public void generate(String recordsMapCsv) {
        try {
            generate(InventoryPairingBGB.mongoDatabase, InventoryPairingBGB.mysqlConn, InventoryPairingBGB.mysqlDbName, InventoryPairingBGB.branchPrefix, recordsMapCsv);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generate(DB mongoDatabase, Connection mysqlConn, String mysqlDbName, String branchPrefix, String recordsMapCsv) throws FileNotFoundException, SQLException {
        if (identifyFile(recordsMapCsv) && validateBranchPrefix(branchPrefix)){
            PrintWriter outValidRecordsMap = new PrintWriter(new File(mysqlDbName + VALID_RECORDS_MAP_FILE_NAME_CHUNK));
            PrintWriter outPicturebooks = new PrintWriter(new File(mysqlDbName + PICTUREBOOKS_FILE_NAME_CHUNK));
            PrintWriter outUnpairedRecords = new PrintWriter(new File(mysqlDbName + UNPAIRED_RECORDS_FILE_NAME_CHUNK));
            PrintWriter outErrRecords = new PrintWriter(new File(mysqlDbName + ERR_RECORDS_FILE_NAME_CHUNK));
            Statement stm = mysqlConn.createStatement();
            ResultSet rs = stm.executeQuery("select record_id from Records");
            List<Integer> localRecIds = new ArrayList<>();
            while(rs.next())
                localRecIds.add(rs.getInt("record_id"));
            stm.close();


            Jongo jongo = new Jongo(mongoDatabase);
            MongoCollection centralRecsCollection = jongo.getCollection("bgb_records");
            MongoCollection centralItemAvailabilitiesCollection = jongo.getCollection("bgb_itemAvailability");
            MongoCollection locationCollection = jongo.getCollection("coders.location");
            String locationDescription = locationCollection.findOne("{'library':'bgb', 'coder_id':#}", branchPrefix).as(Coder.class).getDescription();

            Scanner scanner = new Scanner(new File(recordsMapCsv));
            DBStorage storage = new DBStorage();

            // mapa lokalniRn - centralaRn
            Map<Integer, Integer> localCentralMap = new HashMap<>();
            while (scanner.hasNext()) {
                try {
                    List<String> line = CSVUtils.parseLine(scanner.nextLine());
                    localCentralMap.put(Integer.valueOf(line.get(1)), Integer.valueOf(line.get(2)));
                } catch (Exception e) {
                    continue;
                }
            }
            scanner.close();
            ProgressBar progressBar = new ProgressBar();
            System.out.println("Generating valid records map etc...");
            int cnt = 0;
            for (Integer record_id: localRecIds) {
                cnt++;
                JoRecord localRec = new JoRecord(storage.get(mysqlConn, record_id));
                if (localRec != null && localRec.getRN() != 0) {
                    Integer centralRn = localCentralMap.get(localRec.getRN());

                    // clean situation - write
                    if (centralRn != null && loadRecordFromCentral(centralRn, centralRecsCollection) != null) {
                        outValidRecordsMap.write("date_generated," + localRec.getRN() + "," + centralRn + "\n");
                    }
                    // can't find - try to
                    else {
                        String query = RnPairing.getPairingQuery(localRec);
                        // try to find with predefined fields
                        JoRecord centralRecord = null;

                        centralRecord = RnPairing.getRecFromCursor(centralRecsCollection.find(query).as(JoRecord.class));
                        // if can't try only with isbn
                        if (centralRecord == null) {
                            query = RnPairing.getIsbnOnlyQuery(localRec);
                            centralRecord = RnPairing.getRecFromCursor(centralRecsCollection.find(query).as(JoRecord.class));
                        }
                        // did all to pair it - write
                        if (centralRecord != null)
                            outValidRecordsMap.write("date_paired," + localRec.getRN() + "," + centralRecord.getRN() + "\n");
                        // maybe it's a picture book?
                        else {
                            if (localRec.isPictureBookBGB()) {
                                outPicturebooks.write(localRec.getRN() + "," + localRec.getRecordID() + "\n");
                            }
                            // if not it's impossible to pair it, or it's local made non picture book
                            else {
                                outUnpairedRecords.write(localRec.getRN() + "," + localRec.getRecordID() + "\n");
                            }
                        }

                    }

                }
                else {
                    outErrRecords.write("Greska za zapis sa record_id: " + record_id + "\n");
                }
                if (cnt % 100 == 0)
                    progressBar.update(cnt, localRecIds.size());
            }
            progressBar.update(localRecIds.size(), localRecIds.size());
            System.out.println("Finished!\n");
            outErrRecords.close();
            outPicturebooks.close();
            outUnpairedRecords.close();
            outValidRecordsMap.close();
        }
        else {
            System.out.println("Wrong path to records map file or wrong format branch prefix!");
            System.exit(0);
        }
    }

    private JoRecord loadRecordFromCentral(Integer centralRn, MongoCollection recordsCollection) {
        return recordsCollection.findOne("{rn:" + centralRn + "}").as(JoRecord.class);
    }

    private  boolean identifyFile(String recordsMapCsv) {
        File f = new File(recordsMapCsv);
        if(f.exists() && !f.isDirectory() && f.getPath().endsWith(".csv")) {
           return true;
        }
        return false;
    }

    private boolean validateBranchPrefix(String bPrefix) {
        boolean retVal = true;

        if (bPrefix.length() == 2) {
            for (char c: bPrefix.toCharArray())
                if (!Character.isDigit(c)) {
                    retVal = false;
                    break;
                }
        }
        else
            retVal = false;

        return retVal;
    }


}
