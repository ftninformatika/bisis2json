package bisis.apps.misc;

import bisis.apps.export.csv.ExportCSVGBNS;
import bisis.model.jongo_records.JoRecord;
import bisis.model.records.Godina;
import bisis.model.records.Primerak;
import bisis.model.records.Record;
import bisis.model.records.Sveska;

/**
 * @author badf00d21  21.5.19.
 */
public class RecordUtilsData {

    public static String getMainContent(Record r) {
        if (r == null) {
            System.err.println("Null record");
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(getAutor(r) + ", ");
        sb.append(getNaslov(r) + ", ");
        sb.append(getPlace(r) + ", ");
        sb.append(getGodina(r));
        return sb.toString();
    }

    public static Object findInvUnit(String inv, Record r) {
        Object retVal = null;
        if (r == null) {
            System.err.println("Record null for inv: " + inv);
            return null;
        }
        if (r.getPrimerak(inv) != null) return r.getPrimerak(inv);
        if (r.getGodina(inv) != null) return r.getGodina(inv);
        for (Godina g: r.getGodine()) {
           if (g.getSveska(inv) != null) return g.getSveska(inv);
        }
        return null;
    }

    public static String getCena(Object o) {
        if (o instanceof Sveska) {
            return ((Sveska) o).getCena() != null ? ((Sveska) o).getCena().toString() : "";
        }
        if (o instanceof Godina) {
            return ((Godina) o).getCena() != null ? ((Godina) o).getCena().toString() : "";
        }
        if (o instanceof Primerak) {
            return ((Primerak) o).getCena() != null ? ((Primerak) o).getCena().toString() : "";
        }
        return "";
    }

    public static String getNapomena(Object o) {
        if (o instanceof Sveska) {
            return "";
        }
        if (o instanceof Godina) {
            return formatCVal(((Godina) o).getNapomene() != null ? ((Godina) o).getNapomene() : "");
        }
        if (o instanceof Primerak) {
            return formatCVal(((Primerak) o).getNapomene() != null ? ((Primerak) o).getNapomene() : "");
        }
        return "";
    }

    public static String getSignatura(Object o) {
        String retVal = "";
        if (o instanceof Primerak) {
            retVal += ((Primerak) o).getSigUDK() != null ? ((Primerak) o).getSigUDK() : "";
            retVal += ((Primerak) o).getSigPodlokacija() != null ? ((Primerak) o).getSigPodlokacija() : "";
            retVal += ((Primerak) o).getSigNumerusCurens() != null ? ((Primerak) o).getSigNumerusCurens() : "";
            retVal += ((Primerak) o).getSigFormat() != null ? ((Primerak) o).getSigFormat() : "";
            retVal += ((Primerak) o).getSigIntOznaka() != null ? ((Primerak) o).getSigIntOznaka() : "";
            retVal += ((Primerak) o).getSigDublet() != null ? ((Primerak) o).getSigDublet() : "";
        } else if (o instanceof Sveska) {
            retVal += ((Sveska) o).getSignatura() != null ? ((Sveska) o).getSignatura() : "";
        } else if (o instanceof Godina) {
            retVal += ((Godina) o).getSigUDK() != null ? ((Godina) o).getSigUDK() : "";
            retVal += ((Godina) o).getSigPodlokacija() != null ? ((Godina) o).getSigPodlokacija() : "";
            retVal += ((Godina) o).getSigNumerusCurens() != null ? ((Godina) o).getSigNumerusCurens() : "";
            retVal += ((Godina) o).getSigFormat() != null ? ((Godina) o).getSigFormat() : "";
            retVal += ((Godina) o).getSigIntOznaka() != null ? ((Godina) o).getSigIntOznaka() : "";
            retVal += ((Godina) o).getSigDublet() != null ? ((Godina) o).getSigDublet() : "";
        }
        return retVal;
    }

    private static String getAutor(Record r) {
        return formatCVal(ExportCSVGBNS.getAutori(new JoRecord(r)));
    }

    private static String getNaslov(Record r) {
        return formatCVal(ExportCSVGBNS.getNaslov(new JoRecord(r)));
    }

    private static String getPlace(Record r) {
        return formatCVal(r.getSubfieldContent("210a") == null ? r.getSubfieldContent("210a") : "");
    }

    private static String getGodina(Record r) {
        return formatCVal(ExportCSVGBNS.getGodina(new JoRecord(r)));
    }

    private static String formatCVal(String s) {
        return s.replace("\t", "")
                .replace("\n","").replace("\"", "")
                .replace("\'", "").replace(",", "").trim();
    }
}
