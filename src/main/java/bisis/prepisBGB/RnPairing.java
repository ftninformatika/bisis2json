package bisis.prepisBGB;

import bisis.coders.Counter;
import bisis.export.MigrateBGB;
import bisis.jongo_records.JoRecord;
import bisis.textsrv.DBStorage;
import bisis.utils.CSVUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RnPairing {

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Unesite naziv mysql baze kao argument!");
            return;
        }

        String dbName = args[0];

        try {
            StringBuffer withoutRn = new StringBuffer();
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false&serverTimezone=CET"
                    , "bisis", "bisis");


            Scanner scanner = new Scanner(new File(MigrateBGB.class.getResource("/bisis/without_rn.csv").getPath()));
            DBStorage storage = new DBStorage();
            DB db = new MongoClient().getDB("bisis");
            Jongo jongo = new Jongo(db);
            MongoCollection centralRecs = jongo.getCollection("bgb_records");

            MongoCollection codersCounters = jongo.getCollection("coders.counters");
            Counter counterRn = codersCounters.findOne("{'library':'bgb', 'counterName':'RN'}").as(Counter.class);
            Counter counterRecordid = codersCounters.findOne("{'library':'bgb', 'counterName':'recordid'}").as(Counter.class);
            int rnCnt = counterRn.getCounterValue();
            int recIdCnt= counterRecordid.getCounterValue();


            List<Integer> withoutRNs = new ArrayList<>();
            while (scanner.hasNext()) {
                List<String> line = CSVUtils.parseLine(scanner.nextLine());
                withoutRNs.add(Integer.valueOf(line.get(1)));
            }
            int found = 0;
            int nFound = 0;
            int picturebooks = 0;
            int total = withoutRNs.size();


            PrintWriter pwFound = new PrintWriter(new File(dbName +"_upareni.csv"));
            PrintWriter pwNotFound = new PrintWriter(new File(dbName+"_neupareni.csv"));
            StringBuffer sbFound = new StringBuffer();
            StringBuffer sbNotFound = new StringBuffer();

            for (Integer recId: withoutRNs){

                JoRecord r = new JoRecord(storage.get(conn, recId));
                String query = getPairingQuery(r);
                JoRecord fromCentral = null;
                try {
                    fromCentral = getRecFromCursor(centralRecs.find(query).as(JoRecord.class));
                } catch (Exception e) {
                    continue;
                }

                //ako nije nasao po ostalim poljima, probaj po isbn
                if (fromCentral == null) {
                    try {
                        String isbnQuery = getIsbnOnlyQuery(r);
                        if (!isbnQuery.equals(""))
                            fromCentral = getRecFromCursor(centralRecs.find(isbnQuery).as(JoRecord.class));
                    } catch (Exception e) {
                        continue;
                    }
                }

                System.out.print("\nPronasao: " + found + "\nPrepisao slikovnica: " + picturebooks + "\nNije pronasao: " + nFound + "\nOd: " + total + " \n");

                if (fromCentral != null) {
                    sbFound.append("datum," + r.getRN() + "," + fromCentral.getRN());
                    sbFound.append("\n");
                    found++;

                    fromCentral.addInventar(r);

                    centralRecs.save(fromCentral);
                }
                else {

                    // ako je slikovnica onda prepisujemo ceo zapis sa sve invetarom
                    // dodeljujemo rn i record_id sa brojaca
                    if (r.isPictureBookBGB()) {
                        r.setRN(rnCnt);
                        r.setRecordID(recIdCnt);
                        rnCnt ++;
                        recIdCnt ++;
                        picturebooks ++;
                        centralRecs.save(r);
                        continue;
                    }


                    nFound++;
                    sbNotFound.append(r.getRN()/* + "," +r.getRecordID()*/);
                    sbNotFound.append("\n");
                }
               }

            pwFound.write(sbFound.toString());
            pwNotFound.write(sbNotFound.toString());
            pwFound.close();
            pwNotFound.close();

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

    public static JoRecord getRecFromCursor(MongoCursor<JoRecord> cursor) {
        if (cursor.count() == 1)
            return cursor.next();
        else  if (cursor.count() == 0 ) {
//            System.out.println("Nije nasao zapis!");
            return null;
        } else {
//            System.out.println("Pronasao vise rezultata za zapis!");
            return null;
        }
    }

    public static String getPairingQuery(JoRecord r) {
        String retVal = "{$and: [ ";

        for (String subfield: getSubfieldsList()){
            if (r.getSubfieldContent(subfield) != null && !r.getSubfieldContent(subfield).equals("")) {
                String cont = r.getSubfieldContent(subfield);
                String f = subfield.substring(0,3);
                String sf = subfield.substring(3,4);

                if(cont.contains("'") || cont.contains("\'"))
                    continue;

                retVal += "{ 'fields':{$elemMatch: {'name': '" + f +
                        "', 'subfields': {$elemMatch: {'name': '"+ sf +
                        "', content: '" + cont + "'}}}}},";
            }
        }
        // skloni poslednji ,
        retVal = retVal.substring(0, retVal.length() - 1);
        retVal += "] }";
        return retVal;
    }

    public static String getIsbnOnlyQuery(JoRecord r) {
        String retVal = "";

        String subfield = "010a";
            if (r.getSubfieldContent(subfield) != null && !r.getSubfieldContent(subfield).equals("")) {
                String cont = r.getSubfieldContent(subfield);
                String f = subfield.substring(0,3);
                String sf = subfield.substring(3,4);

                retVal += "{ 'fields':{$elemMatch: {'name': '" + f +
                        "', 'subfields': {$elemMatch: {'name': '"+ sf +
                        "', content: '" + cont + "'}}}}}";
        }
        retVal += "";
        return retVal;
    }

    private static String getISBNGenericQuery(JoRecord r) {
        String isbn = r.getSubfieldContent("010a");
        if (isbn != null && !isbn.equals(""))
            return "{'fields.subfields.content':'" + isbn + "'}";
        return "{}";
    }

    private static String getISBNConreteQuery(JoRecord r) {
        String isbn = r.getSubfieldContent("010a");
        if (isbn != null && !isbn.equals(""))
            return "{'isbn':'" + isbn + "'}";
        return "{}";
    }


    private static List<String> getSubfieldsList() {
        List<String> retVal = new ArrayList<>();

        retVal.add("200a");
        retVal.add("200h");
        retVal.add("205a");
        retVal.add("210a");
        retVal.add("210c");
        retVal.add("210d");
        retVal.add("215a");
//        retVal.add("010a");

        return retVal;
    }
}
