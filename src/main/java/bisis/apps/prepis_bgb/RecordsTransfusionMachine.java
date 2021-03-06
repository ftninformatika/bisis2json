package bisis.apps.prepis_bgb;

import bisis.model.coders.Coder;
import bisis.model.coders.Counter;
import bisis.model.jongo_records.JoGodina;
import bisis.model.jongo_records.JoPrimerak;
import bisis.model.jongo_records.JoRecord;
import bisis.model.records.ItemAvailability;
import bisis.utils.CSVUtils;
import bisis.utils.ProgressBar;
import bisis.utils.RecordUtils;
import bisis.utils.textsrv.DBStorage;
import com.mongodb.DB;
import com.mongodb.DuplicateKeyException;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @author Petar
 */
public class RecordsTransfusionMachine {


    public void transfuse(boolean copyPicturebooks, boolean copyUnpaired) {
        try {
            transfuse(InventoryPairingBGB.mysqlConn, InventoryPairingBGB.mongoDatabase,
                    InventoryPairingBGB.mysqlDbName, InventoryPairingBGB.branchPrefix, copyPicturebooks, copyUnpaired);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void transfuse(Connection mysqlConn, DB mongoDb, String mysqlDbName, String branchPrefix, boolean copyPicturebooks, boolean copyUnpaired) throws SQLException, FileNotFoundException {
        if (validateGeneratedFiles(mysqlDbName)) {
            Statement stm = mysqlConn.createStatement();
            ResultSet rs = stm.executeQuery("select record_id from Records");
            List<Integer> localRecIds = new ArrayList<>();
            while(rs.next())
                localRecIds.add(rs.getInt("record_id"));
            stm.close();

            Jongo jongo = new Jongo(mongoDb);
            MongoCollection centralRecsCollection = jongo.getCollection("bgb_records");
            MongoCollection centralItemAvailabilitiesCollection = jongo.getCollection("bgb_itemAvailability");
            MongoCollection locationCollection = jongo.getCollection("coders.location");
            String locationDescription = locationCollection.findOne("{'library':'bgb', 'coder_id':#}", branchPrefix).as(Coder.class).getDescription();
            MongoCollection codersCounters = jongo.getCollection("coders.counters");
            Counter counterRn = codersCounters.findOne("{'library':'bgb', 'counterName':'RN'}").as(Counter.class);
            Counter counterRecordid = codersCounters.findOne("{'library':'bgb', 'counterName':'recordid'}").as(Counter.class);
            int rnCnt = counterRn.getCounterValue() + 1;
            int recIdCnt= counterRecordid.getCounterValue() + 1;

            Scanner scannerMap = new Scanner(new File(mysqlDbName + RecordsMapGenerator.VALID_RECORDS_MAP_FILE_NAME_CHUNK));
            Scanner scannerPb = new Scanner(new File(mysqlDbName + RecordsMapGenerator.PICTUREBOOKS_FILE_NAME_CHUNK));
            Scanner scannerU = new Scanner(new File(mysqlDbName + RecordsMapGenerator.UNPAIRED_RECORDS_FILE_NAME_CHUNK));
            DBStorage storage = new DBStorage();

            // localRn - centralRn map
            List<Integer> duplicateLocalRns = new ArrayList<>();
            Map<Integer, Integer> localCentralMap = new HashMap<>();
            while (scannerMap.hasNext()) {
                try {
                    List<String> line = CSVUtils.parseLine(scannerMap.nextLine());
                    if (localCentralMap.get(Integer.valueOf(line.get(1))) != null)
                        duplicateLocalRns.add(Integer.valueOf(line.get(1)));
                    localCentralMap.put(Integer.valueOf(line.get(1)), Integer.valueOf(line.get(2)));
                } catch (Exception e) {
                    continue;
                }
            }
            scannerMap.close();

            // check if localRn's are unique
            if (duplicateLocalRns.size() > 0) {
                System.out.println("Records map file is not valid, it contains duplicates in localRn's.");
                System.out.println("Folowing RN's are duplicated:");
                for (int rn: duplicateLocalRns)
                    System.out.println(String.valueOf(rn));
                System.exit(0);
            }

            List<Integer> picturebooksList = new ArrayList<>();
            while (scannerPb.hasNext()) {
                try {
                    List<String> line = CSVUtils.parseLine(scannerPb.nextLine());
                    picturebooksList.add(Integer.valueOf(line.get(1)));
                } catch (Exception e) {
                    continue;
                }
            }
            scannerPb.close();

            List<Integer> unpairedList = new ArrayList<>();
            while (scannerU.hasNext()) {
                try {
                    List<String> line = CSVUtils.parseLine(scannerU.nextLine());
                    unpairedList.add(Integer.valueOf(line.get(0)));
                } catch (Exception e) {
                    continue;
                }
            }
            scannerU.close();

            int cnt = 0;
            ProgressBar progressBar = new ProgressBar();
            System.out.println("Transfusion of inventory from " + branchPrefix + " to BGB started.");
            if (copyPicturebooks)
                System.out.println("Copy picturebooks mode ON!");
            if (copyUnpaired)
                System.out.println("Copy unpaired records mode ON!");
            for (Integer sys_id: localRecIds) {
                cnt++;
                JoRecord localRec = new JoRecord(storage.get(mysqlConn, sys_id));

                // map some coders that doesn't exist in central TODO - write something more elegant later
                for (JoPrimerak p: localRec.getPrimerci()) {
                    if (p.getSigIntOznaka() != null
                            && InventoryCodersPairingMap.internalMarkMap.get(p.getSigIntOznaka()) != null) {
                        p.setSigIntOznaka(InventoryCodersPairingMap.internalMarkMap.get(p.getSigIntOznaka()));
                    }
                }

                for (JoGodina g: localRec.getGodine()) {
                    if (g.getSigIntOznaka() != null  && InventoryCodersPairingMap.internalMarkMap.get(g.getSigIntOznaka()) != null) {
                        g.setSigIntOznaka(InventoryCodersPairingMap.internalMarkMap.get(g.getSigIntOznaka()));
                    }
                    // mapiranja ispod su za periodiku
                    if(!mysqlDbName.equals("periodika_bgb"))
                        continue;
                    if (g.getNacinNabavke() != null && InventoryCodersPairingMap.acqTypeMap.get(g.getNacinNabavke()) != null) {
                        g.setNacinNabavke(InventoryCodersPairingMap.acqTypeMap.get(g.getNacinNabavke()));
                    }
                }

                Integer centralRn = localCentralMap.get(localRec.getRN());
                if (centralRn != null && centralRecsCollection.findOne("{rn:" + centralRn + "}").as(JoRecord.class) != null) {
                    JoRecord centralRec = centralRecsCollection.findOne("{rn:" + centralRn + "}").as(JoRecord.class);
                    for (JoPrimerak p: localRec.getPrimerci()) {
                        if (!centralRec.containsPrimerak(p)) {
                            centralRec.getPrimerci().add(p);
                            centralRecsCollection.save(centralRec);
                            ItemAvailability ia = RecordUtils.makeItemAvailabilyForRec(centralRec, p.getInvBroj(), locationDescription);
                            centralItemAvailabilitiesCollection.save(ia);
                        }
                        else
                            System.out.println("Nasao dupli inv za: " + p.getInvBroj());
                    }
                }
                else {

                    if (copyPicturebooks && picturebooksList.contains(sys_id)) {
                        localRec.setRN(rnCnt);
                        localRec.setRecordID(recIdCnt);
                        rnCnt++;
                        recIdCnt++;
                        List<ItemAvailability> items = RecordUtils.makeItemAvailabilitesForRec(localRec, locationDescription);
                        centralRecsCollection.save(localRec);
                        for(ItemAvailability i: items)
                            centralItemAvailabilitiesCollection.save(i);
                    }
                    if (copyUnpaired && unpairedList.contains(sys_id)) {
                        localRec.setRN(rnCnt);
                        localRec.setRecordID(recIdCnt);
                        rnCnt++;
                        recIdCnt++;
                        try {
                            centralRecsCollection.save(localRec);
                        }
                        catch (DuplicateKeyException e) {
                            String errMsg = "Vec postoji zapis sa RN ili recordID: " + rnCnt + ", " + recIdCnt + ", record_id(mysql):" + sys_id + "\n";
                            System.out.println(errMsg);
                            // TODO - refactor this to write into LOGGER
//                            FileUtils.writeTextFile(mysqlDbName + "_zapisi_greske.txt", errMsg);

                        }
                        catch (Exception e) {
                            e.printStackTrace();
//                            FileUtils.writeTextFile(mysqlDbName + "_zapisi_greske.txt", e.getMessage() + "\n");
                        }
                        List<ItemAvailability> items = RecordUtils.makeItemAvailabilitesForRec(localRec, locationDescription);
                        centralRecsCollection.save(localRec);
                        for (ItemAvailability i : items) {
                            try {
                                centralItemAvailabilitiesCollection.save(i);
                            }
                            catch (DuplicateKeyException e) {
                                String errMsg = "Vec postoji inv broj za primerak: "+ i.getCtlgNo();
                                System.out.println(errMsg);
//                                FileUtils.writeTextFile(mysqlDbName + "_items_greske.txt", errMsg);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
//                                FileUtils.writeTextFile(mysqlDbName + "_postojeci_inv.txt", e.getMessage() + "\n");
                            }
                        }

                     }
                }
                if (cnt % 100 == 0)
                    progressBar.update(cnt, localRecIds.size());
            }
            progressBar.update(localRecIds.size(), localRecIds.size());
            counterRn.setCounterValue(rnCnt);
            counterRecordid.setCounterValue(recIdCnt);
            codersCounters.update("{counterName: 'RN', library: 'bgb'}").with(counterRn);
            codersCounters.update("{counterName: 'recordid', library: 'bgb'}").with(counterRecordid);
            System.out.println("\nCOUNTERS FOR RN & RECORD_ID SET TO: " + rnCnt + " & " + recIdCnt);
        }
    }

    private boolean validateGeneratedFiles(String mysqlDbName) {
        File recMapF = new File(mysqlDbName + RecordsMapGenerator.VALID_RECORDS_MAP_FILE_NAME_CHUNK);
        File picF = new File(mysqlDbName + RecordsMapGenerator.PICTUREBOOKS_FILE_NAME_CHUNK);
        File unpF = new File(mysqlDbName + RecordsMapGenerator.UNPAIRED_RECORDS_FILE_NAME_CHUNK);
        if (!recMapF.exists() || !picF.exists() || !unpF.exists()) {
            System.out.println("Following files needs to be generated and put in same directory with this to work insertion.");
            System.out.println(mysqlDbName + RecordsMapGenerator.VALID_RECORDS_MAP_FILE_NAME_CHUNK);
            System.out.println(mysqlDbName + RecordsMapGenerator.PICTUREBOOKS_FILE_NAME_CHUNK);
            System.out.println(mysqlDbName + RecordsMapGenerator.UNPAIRED_RECORDS_FILE_NAME_CHUNK);
            return false;
        }
        return true;
    }
}
