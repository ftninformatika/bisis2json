package bisis.export;

import bisis.jongo_records.JoRecord;
import bisis.prefixes.PrefixConverter;
import bisis.records.Godina;
import bisis.records.Primerak;
import bisis.records.Record;
import bisis.records.Sveska;
import bisis.utils.LatCyrUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class ExportCSVGBNS {

    public static void main(String[] args) {

        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            DB db = mongoClient.getDB("bisis");
            Jongo jongo = new Jongo(db);
            MongoCollection recCollection = jongo.getCollection("gbns_records");
            PrintWriter exportFile = new PrintWriter(new File("csvGBNS.csv"));

            MongoCursor<JoRecord> allRecs = recCollection.find().as(JoRecord.class);

            //Header tabele
            exportFile.write("Naslov,Autori,Branch,Izdavač,Jezik,Udk\n");

            while (allRecs.hasNext()) {
                JoRecord rec = allRecs.next();
                exportFile.write(getNaslov(rec).replace(",", "") + "," + getAutori(rec).replace(",", "") + ","
                        + getBranches(rec).replace(",", "") + "," + getIzdavac(rec).replace(",", "") +
                        "," + getJezik(rec).replace(",", "") + "," + getUdk(rec).replace(",","")+"\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static String getNaslov(JoRecord rec) {
        String retVal = "";
        if (rec.getSubfieldContent("200a") != null)
            retVal += LatCyrUtils.toLatin(rec.getSubfieldContent("200a")).replace(',', ' ');
        if (rec.getSubfieldContent("200i") != null)
            retVal += "; " + LatCyrUtils.toLatin(rec.getSubfieldContent("200a")).replace(',', ' ');

        return retVal;
    }

    public static String getAutori(JoRecord rec) {
        String retVal = "Nepoznati autori";
        Map<String, List<String>> prefixMap = PrefixConverter.toMap(new Record(rec), "");
        if (prefixMap.get("AU") != null && prefixMap.get("AU").size() > 0) {
            retVal = "";
            for (String d: prefixMap.get("AU")) {
                if (!retVal.toLowerCase().contains(d.toLowerCase()) && d.trim() != "" && !conatinsAuthor(retVal, d))
                    retVal += LatCyrUtils.toLatin(d).replace(",","") + "; ";
            }
            retVal = retVal.substring(0, retVal.length() - 2);
            if (retVal.startsWith(" ; "))
                retVal = retVal.substring(3, retVal.length());
            retVal.replace("]", "").replace("[", "");
        }

        return retVal;
    }

    private static boolean conatinsAuthor(String fullAuthor, String newAuthor) {
        if (newAuthor.split(" ").length > 0) {
            if (fullAuthor.contains(newAuthor.split(" ")[0]))
                return true;
            else
                return false;
        }
        else
            return false;
    }

    //vraca broj primeraka odnsotno svezaka za tekuci record
    public static String getBranches(JoRecord rec){
        Record record = new Record(rec);
        StringBuffer retVal = new StringBuffer();
        String raspodela = getRaspodelaNSCirc(record);
        retVal.append(raspodela);
        if (raspodela.trim().equals(""))
            return "";
        retVal.append(". Uk: ");
        if(record.getPrimerci() != null && record.getPrimerci().size() > 0) {
            int cnt = 0;
            for(Primerak p: record.getPrimerci()) {
                if(p.getStatus() != null && (p.getStatus().equals("A") || p.getStatus().equals("5")))
                    cnt++;
            }
            retVal.append(cnt);
        }

        else if(record.getGodine() != null && record.getGodine().size() > 0){
            int cnt = 0;
            for(Godina g: record.getGodine()) {
                if(g.getSveske() != null && g.getSveske().size() > 0)
                    for(Sveska s: g.getSveske()) {
                        if(s.getStatus() != null && (s.getStatus().equals("A") || s.getStatus().equals("5")))
                            cnt++;
                    }
            }
            retVal.append(cnt);
        }

        return retVal.toString();
    }

    public static String getIzdavac(JoRecord rec) {
        String retVal = "Nepoznat izdavač";
        if(rec.getSubfieldContent("210c") != null)
            retVal = LatCyrUtils.toLatin(rec.getSubfieldContent("210c")).replace(',',';');

        return retVal;
    }

    public static String getJezik(JoRecord rec) {
        String retVal = "Nepoznat jezik";
        if (rec.getSubfieldContent("101a") != null)
            retVal = LatCyrUtils.toLatin(rec.getSubfieldContent("101a").toLowerCase());
        return retVal;
    }

    public static String getUdk(JoRecord rec) {
        String retVal = "Nepoznat UDK";
        Map<String, List<String>> prefixMap = PrefixConverter.toMap(new Record(rec), "");
        if (prefixMap.get("DC") != null && prefixMap.get("DC").size() > 0) {
            retVal = "";
            for (String d: prefixMap.get("DC")) {
                retVal += LatCyrUtils.toLatin(d).replace(",","") + "; ";
            }
            retVal = retVal.substring(0, retVal.length() - 2);
        }


        return retVal;
    }

    public static String getGodina(JoRecord rec) {
        String retVal = "Nepoznata godina";
        if (rec.getSubfieldContent("210d") != null) {
            retVal = rec.getSubfieldContent("210d").replace('[',' ').replace(']', ' ').replace(',', ' ');
        }
        return retVal;
    }

    public static String getRaspodelaNSCirc(Record record){
        int brojOgranaka = 50;
        try{
            // niz koji na i-tom mestu ima broj primeraka u i-tom ogranku
            int [] brojPrimerakaUOgranku = new int[brojOgranaka];
            for(int i=0;i<brojPrimerakaUOgranku.length;i++)
                brojPrimerakaUOgranku[i]=0;
            // matrica koja na mestu (i,j) sadrzi broj primeraka koji
            // su iz ogranka i prebaceni u ogranak j
            int[][]preusmereni = new int[brojOgranaka][brojOgranaka];

            boolean firstOdeljenje = true;
            for(int i=0;i<brojOgranaka;i++)
                for(int j=0;j<brojOgranaka;j++)
                    preusmereni[i][j]=0;


            int odeljenje = 0;
            int odeljenjeUInvBroju = 0;
            for(Primerak p:record.getPrimerci()){
                if(p.getStatus()!=null && !p.getStatus().equals("9")){
                    if(p.getOdeljenje()!=null && !p.getOdeljenje().equals("")){
                        try{
                            odeljenje = Integer.parseInt(p.getOdeljenje());
                            brojPrimerakaUOgranku[odeljenje]++;
                        }catch(NumberFormatException e){

                        }
                        if(!p.getInvBroj().substring(0,2).equals(p.getOdeljenje())){
                            try{
                                odeljenjeUInvBroju = Integer.parseInt(p.getInvBroj().substring(0, 2));
                                preusmereni[odeljenjeUInvBroju][odeljenje]++;
                            }catch(NumberFormatException e){
                            }
                        }
                    }
                }
            }

            // ispis u string
            StringBuffer retVal = new StringBuffer();
            for(int ogranak=0;ogranak<brojPrimerakaUOgranku.length;ogranak++){
                if(brojPrimerakaUOgranku[ogranak]!=0 || imaPreusmerenih(getOgranakStrFromInt(ogranak), record)){
                    if(!firstOdeljenje)
                        retVal.append("; ");
                    retVal.append(""+getOgranakStrFromInt(ogranak)+""+":"+brojPrimerakaUOgranku[ogranak]);
                    firstOdeljenje = false;
                    if(imaPreusmerenih(getOgranakStrFromInt(ogranak), record)){
                        retVal.append("(");
                        boolean first = true;
                        for(int j=0;j<brojOgranaka;j++){
                            if(preusmereni[ogranak][j]!=0){
                                if(!first)
                                    retVal.append(" ;");
                                retVal.append(""+getOgranakStrFromInt(j)+""+":"+preusmereni[ogranak][j]);
                                first = false;

                            }
                        }
                        retVal.append(")");
                    }
                }
            }
            return retVal.toString();
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }

    private static boolean imaPreusmerenih(String ogranak, Record record){
        for(Primerak p:record.getPrimerci())
            if(p.getInvBroj().startsWith(ogranak)
                    && !p.getInvBroj().substring(0,2).equals(p.getOdeljenje()))
                return true;
        return false;
    }

    private static String getOgranakStrFromInt(int ogranak){
        if(ogranak<10)
            return "0"+String.valueOf(ogranak);
        else
            return String.valueOf(ogranak);
    }
}
