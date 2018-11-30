package bisis.prepis_bgb;

import bisis.jongo_records.JoRecord;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;

public class RnPairing {

//    public static void main(String[] args) {
//        if(args.length != 2) {
//            System.out.println("Please enter mysqldb name{1} and branch prefix{2}");
//            return;
//        }
//
//        String dbName = args[0];
//        String branchPrefix = args[1];
//
//        try {
//            StringBuffer withoutRn = new StringBuffer();
//            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false&serverTimezone=CET"
//                    , "bisis", "bisis");
//
//
//            Scanner scanner = new Scanner(new File(MigrateBGB.class.getResource("/bisis/without_rn.csv").getPath()));
//            DBStorage storage = new DBStorage();
//            DB db = new MongoClient().getDB("bisis");
//            Jongo jongo = new Jongo(db);
//            MongoCollection centralRecs = jongo.getCollection("bgb_records");
//            MongoCollection centralItemAvailabilities = jongo.getCollection("bgb_itemAvailability");
//            MongoCollection locationCollection = jongo.getCollection("coders.location");
//            String locationDescription = locationCollection.findOne("{'library':'bgb', 'coder_id':#}", branchPrefix).as(Coder.class).getDescription();
//
//            MongoCollection codersCounters = jongo.getCollection("coders.counters");
//            Counter counterRn = codersCounters.findOne("{'library':'bgb', 'counterName':'RN'}").as(Counter.class);
//            Counter counterRecordid = codersCounters.findOne("{'library':'bgb', 'counterName':'recordid'}").as(Counter.class);
//            int rnCnt = counterRn.getCounterValue();
//            int recIdCnt= counterRecordid.getCounterValue();
//
//
//            List<Integer> withoutRNs = new ArrayList<>();
//            while (scanner.hasNext()) {
//                List<String> line = CSVUtils.parseLine(scanner.nextLine());
//                withoutRNs.add(Integer.valueOf(line.get(1)));
//            }
//            int found = 0;
//            int nFound = 0;
//            int picturebooks = 0;
//            int total = withoutRNs.size();
//
//
//            PrintWriter pwFound = new PrintWriter(new File(dbName +"_upareni.csv"));
//            PrintWriter pwNotFound = new PrintWriter(new File(dbName+"_neupareni.csv"));
//            PrintWriter pwPictureBooks = new PrintWriter(new File(dbName+"_pictureBooks.csv"));
//            StringBuffer sbFound = new StringBuffer();
//            StringBuffer sbNotFound = new StringBuffer();
//            StringBuffer sbPictureBooks = new StringBuffer();
//
//            for (Integer recId: withoutRNs){
//
//                JoRecord r = new JoRecord(storage.get(conn, recId));
//                String query = getPairingQuery(r);
//                JoRecord fromCentral = null;
//                System.out.print("\nPronasao: " + found + "\nPronasao slikovnica: " + picturebooks + "\nNije pronasao: " + nFound + "\nOd: " + total + " \n");
//
//                try {
//                    fromCentral = getRecFromCursor(centralRecs.find(query).as(JoRecord.class));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    continue;
//                }
//
//                //ako nije nasao po ostalim poljima, probaj po isbn
//                if (fromCentral == null) {
//                    try {
//                        String isbnQuery = getIsbnOnlyQuery(r);
//                        if (!isbnQuery.equals("")) {
//                            fromCentral = getRecFromCursor(centralRecs.find(isbnQuery).as(JoRecord.class));
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        continue;
//                    }
//                }
//
//
//                if (fromCentral != null) {
//                    sbFound.append("datum," + r.getRN() + "," + fromCentral.getRN());
//                    sbFound.append("\n");
//                    found++;
//
//                }
//                else {
//
//                    // slikovnice posebno, jer se prepisuju cele sa sve zapisom
//                    if (r.isPictureBookBGB()) {
//                        picturebooks++;
//                        sbPictureBooks.append(r.getRN() + "," + r.getRecordID());
//                        sbPictureBooks.append("\n");
//                        continue;
//                    }
//
//
//                    nFound++;
//                    sbNotFound.append(r.getRN() + "," +r.getRecordID());
//                    sbNotFound.append("\n");
//                }
//               }
//
//            pwFound.write(sbFound.toString());
//            pwNotFound.write(sbNotFound.toString());
//            pwPictureBooks.write(sbPictureBooks.toString());
//            pwFound.close();
//            pwNotFound.close();
//            pwPictureBooks.close();
//
//            counterRn.setCounterValue(rnCnt);
//            counterRecordid.setCounterValue(recIdCnt);
//            codersCounters.update("{counterName: 'RN', library: 'bgb'}").with(counterRn);
//            codersCounters.update("{counterName: 'recordid', library: 'bgb'}").with(counterRecordid);
//           // System.out.println("\nBROJACI PODESENI ZA RN I RECORD_ID NA: " + rnCnt + " i " + recIdCnt);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    public static final String EMPTY_QUERY = "{$and: [] }";

    public static JoRecord getRecFromCursor(MongoCursor<JoRecord> cursor) {
        if (cursor == null)
            return null;
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

                if(cont == null || cont.contains("'") || cont.contains("\'"))
                    continue;

                retVal += "{ 'fields':{$elemMatch: {'name': '" + f +
                        "', 'subfields': {$elemMatch: {'name': '"+ sf +
                        "', content: '" + cont + "'}}}}},";
            }
        }
        // skloni poslednji ,
        retVal = retVal.substring(0, retVal.length() - 1);
        retVal += "] }";
        return retVal.equals(EMPTY_QUERY) ? "" : retVal;
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
