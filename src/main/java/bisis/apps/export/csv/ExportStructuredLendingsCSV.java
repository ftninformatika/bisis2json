package bisis.apps.export.csv;

import bisis.model.config.LibraryConfiguration;
import bisis.model.jongo_circ.JoLending;
import bisis.model.jongo_records.JoPrimerak;
import bisis.model.jongo_records.JoRecord;
import bisis.model.records.Field;
import bisis.model.records.Subfield;
import bisis.utils.LatCyrUtils;
import bisis.utils.ProgressBar;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author badf00d21  11.4.19.
 */
public class ExportStructuredLendingsCSV {

    public static final String CSV_HEADER = "rn\tmongoid\tlibrary\tauthor_full\tauthor_name\tauthor_surname\ttitle\tlanguage\tpublisher\tpublication_year\tudc\tctlg_no\tuser_id\tlend_date\tlocation\n";
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat(DATE_PATTERN);
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("bisis");
            Jongo jongo = new Jongo(db);
            MongoCursor<LibraryConfiguration> curConfigs = jongo.getCollection("configs").find().as(LibraryConfiguration.class);
            for (LibraryConfiguration lc: curConfigs) {
                PrintWriter exportFile = new PrintWriter(new File("lendings_structured_" + lc.getLibraryName() + ".csv"));
                PrintWriter errFile = new PrintWriter(new File("errors_" + lc.getLibraryName() + ".txt"));
                exportFile.write(CSV_HEADER);
                System.out.println("Starting generating csv for: " + lc.getLibraryName());
                MongoCollection collLendings = jongo.getCollection(lc.getLibraryName() + "_lendings");
                int count = 0;
                ProgressBar progressBar = new ProgressBar();
                MongoCursor<JoRecord> curRecords = jongo.getCollection(lc.getLibraryName() + "_records").find().as(JoRecord.class);
                int lendingCount = curRecords.count();
                while(curRecords.hasNext()) {
                    JoRecord rec = curRecords.next();
                    if (rec == null || rec.getPrimerci().size() == 0)
                        continue;
                    for (JoPrimerak p: rec.getPrimerci()) {
                        if (p.getInvBroj() == null || p.getInvBroj().equals(""))
                            continue;
                        MongoCursor<JoLending> currLending = collLendings.find("{'ctlgNo':#}", p.getInvBroj()).as(JoLending.class);
                        if (currLending == null || currLending.count() == 0)
                            continue;
                        while (currLending.hasNext()) {
                            JoLending lending = currLending.next();
                            CsvItem row = new CsvItem();
                            row.setRn(String.valueOf(rec.getRN()));
                            row.setMongoid(rec.get_id());
                            row.setLibrary(lc.getLibraryName());
                            row.setAuthor_full(null == rec.getSubfieldContent("200f") ? "" : format(rec.getSubfieldContent("200f")));
                            row.setAuthor_name(getAuthorName(rec));
                            row.setAuthor_surname(getAuthorSurname(rec));
                            row.setTitle(null == rec.getSubfieldContent("200a") ? "" : format(rec.getSubfieldContent("200a")));
                            row.setLanguage(null == rec.getSubfieldContent("101a") ? "" : format(rec.getSubfieldContent("101a")));
                            row.setPublisher(null == rec.getSubfieldContent("210c") ? "" : format(rec.getSubfieldContent("210c")));
                            row.setPublication_year(null == rec.getSubfieldContent("210d") ? "" : format(rec.getSubfieldContent("210d")));
                            row.setUdc(getAllUDC(rec));
                            row.setCtlg_no(format(lending.getCtlgNo()));
                            row.setUser_id(format(lending.getUserId()));
                            row.setLend_date(null == lending.getLendDate() ? "" : format(df.format(lending.getLendDate())));
                            row.setLocation(format(lending.getLocation()));
                            exportFile.write(row.toString());
                            if (count % 100 == 0)
                                progressBar.update(count, lendingCount);
                        }
                    }
                    count++;
                }
                exportFile.close();
                errFile.close();
                System.out.println("Finished: " + lc.getLibraryName());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String getAuthorName(JoRecord r) {
        String retVal = "";
        Field _700 = r.getField("700");
        if (_700 == null)
            return retVal;
        if (_700.getInd2() == '1' && _700.getSubfieldContent('b') != null)
            retVal = _700.getSubfieldContent('b');
        else if (_700.getInd2() == '0' && _700.getSubfieldContent('a') != null)
            retVal = _700.getSubfieldContent('a');
        return format(retVal);
    }

    private static String getAuthorSurname(JoRecord r) {
        String retVal = "";
        Field _700 = r.getField("700");
        if (_700 == null)
            return retVal;
        if (_700.getInd2() == '1' && _700.getSubfieldContent('a') != null)
            retVal = _700.getSubfieldContent('a');
        else if (_700.getInd2() == '0' && _700.getSubfieldContent('b') != null)
            retVal = _700.getSubfieldContent('b');
        return format(retVal);
    }

    private static String getAllUDC(JoRecord r) {
        String retVal = "";
        List<Subfield> _675aList = r.getSubfields("675a");
        if (_675aList == null || _675aList.size() == 0)
            return retVal;
        StringBuffer sbUdc = new StringBuffer();
        for (int i = 0; i < _675aList.size(); i++) {
            sbUdc.append(format(_675aList.get(i).getContent()));
            if (i < (_675aList.size() - 1))
                sbUdc.append("; ");
        }
        retVal = sbUdc.toString();
        return retVal;
    }

    private static String format(String cell) {
        if (cell == null)
            return "";
        return LatCyrUtils.toLatinUnaccented(cell.replace("\t"," ")
                .replace("\n", " ")).replace("\"","").toLowerCase();
    }

}
