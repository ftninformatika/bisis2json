package bisis.apps.prepis_bgb;

import bisis.model.records.Record;
import bisis.utils.textsrv.DBStorage;
import bisis.utils.CSVUtils;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UnpairedRecordsCsvGenerator {

    public static final String DELIMITER = "|";

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Please enter path to unpaired RN's CSV file and mysqldb name as parameters!\n");
            System.exit(0);
        }
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("unpaired_" + args[1] + ".txt"), "UTF8")));
            StringBuffer writeOut = new StringBuffer();

            Scanner scanner = new Scanner(new File(args[0]));
            List<String> rnList = new ArrayList<>();

            while (scanner.hasNext()) {
                List<String> line = CSVUtils.parseLine(scanner.nextLine());
                rnList.add(line.get(0));
            }
            scanner.close();
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + args[1] + "?useSSL=false&serverTimezone=CET"
                    , "bisis", "bisis");
            DBStorage storage = new DBStorage();

            List<Integer> docIDs = new ArrayList<Integer>();
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT record_id FROM Records");
            while (rset.next())
                docIDs.add(rset.getInt(1));
            rset.close();
            stmt.close();

            //writeOut.append(makeHeader());

            for (int recId: docIDs) {
                Record record = storage.get(conn, recId);
                if (rnList.contains(record.getRN() + "")) {
                    writeOut.append(makeBlock(record));
                }
            }
            out.write(writeOut.toString());
            out.close();

        } catch (FileNotFoundException e) {
            System.out.println("Ivalid path!\n");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }

    private static String makeHeader() {
        StringBuffer sb = new StringBuffer();

        sb.append("RECORD_ID");
        sb.append(DELIMITER);
        sb.append("RN");
        sb.append(DELIMITER);
        for (String sf: getPrintFieldsList()) {
            sb.append(sf);
            sb.append(DELIMITER);
        }
        sb.append("\n");
        return sb.toString();
    }

    private static String makeBlock(Record rec) {
        StringBuffer sb = new StringBuffer();

        sb.append("RN - ");
        sb.append(rec.getRN());
        sb.append("\n");
        sb.append("RECORD_ID - ");
        sb.append(rec.getRecordID());
        sb.append("\n");
        for (String sf: getPrintFieldsList()) {
            sb.append(sf + " - ");
            sb.append(rec.getSubfieldContent(sf));
            sb.append("\n");
        }
        sb.append("------------------------------------------\n");
        return sb.toString();
    }

    private static String makeRow(Record rec) {
        StringBuffer sb = new StringBuffer();

        sb.append(rec.getRecordID());
        sb.append(DELIMITER);
        sb.append(rec.getRN());
        sb.append(DELIMITER);

        for (String sField: getPrintFieldsList()) {
            String text = rec.getSubfieldContent(sField) != null ? rec.getSubfieldContent(sField) : "null";
            text.replace('"','\"');
            sb.append(text);
            sb.append(DELIMITER);
        }
        sb.append("\n");
        return sb.toString();
    }

    private static List<String> getPrintFieldsList() {
        List<String> retVal = new ArrayList<>();

        retVal.add("010a");
        retVal.add("010b");
        retVal.add("200a");
        retVal.add("200f");
        retVal.add("200h");
        retVal.add("205a");
        retVal.add("210a");
        retVal.add("210c");
        retVal.add("210d");
        retVal.add("215a");
        retVal.add("215c");
        retVal.add("215d");
        retVal.add("700a");
        retVal.add("700b");
        retVal.add("701a");
        retVal.add("701b");

        return retVal;
    }



}
