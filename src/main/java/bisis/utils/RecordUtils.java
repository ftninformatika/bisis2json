package bisis.utils;

import bisis.model.jongo_records.JoGodina;
import bisis.model.jongo_records.JoPrimerak;
import bisis.model.jongo_records.JoRecord;
import bisis.model.records.ItemAvailability;

import java.util.ArrayList;
import java.util.List;

public class RecordUtils {

    public static List<ItemAvailability> makeItemAvailabilitesForRec(JoRecord record, String libDepartmentDesc) {
        List<ItemAvailability> retVal = new ArrayList<>();

        if (record.getPrimerci().size() > 0) {
            for (JoPrimerak p : record.getPrimerci()) {
                ItemAvailability ia = new ItemAvailability();
                ia.setLibDepartment(libDepartmentDesc);
                ia.setBorrowed(false);
                ia.setCtlgNo(p.getInvBroj());
                ia.setRecordID(String.valueOf(record.getRecordID()));
                retVal.add(ia);
            }
        }

        if (record.getGodine().size() > 0) {
            for (JoGodina g : record.getGodine()) {
                ItemAvailability ia = new ItemAvailability();
                ia.setLibDepartment(libDepartmentDesc);
                ia.setBorrowed(false);
                ia.setCtlgNo(g.getInvBroj());
                ia.setRecordID(String.valueOf(record.getRecordID()));
                retVal.add(ia);
            }
        }

        return retVal;
    }

    public static ItemAvailability makeItemAvailabilyForRec(JoRecord record, String invNum,  String libDepartmentDesc) {
        ItemAvailability retVal = null;

        if (record.getPrimerci() != null && record.getPrimerci().size() > 0) {
            retVal = new ItemAvailability();
            for (JoPrimerak p : record.getPrimerci()) {
                if (p.getInvBroj() != null && p.getInvBroj().equals(invNum)) {
                    ItemAvailability ia = new ItemAvailability();
                    ia.setLibDepartment(libDepartmentDesc);
                    ia.setBorrowed(false);
                    ia.setCtlgNo(p.getInvBroj());
                    ia.setRecordID(String.valueOf(record.getRecordID()));
                    retVal = ia;
                    break;
                }
            }
        }

        if (record.getGodine() != null && record.getGodine().size() > 0) {
            retVal = new ItemAvailability();
            for (JoGodina g : record.getGodine()) {
                if (g.getInvBroj() != null && g.getInvBroj().equals(invNum)) {
                    ItemAvailability ia = new ItemAvailability();
                    ia.setLibDepartment(libDepartmentDesc);
                    ia.setBorrowed(false);
                    ia.setCtlgNo(g.getInvBroj());
                    ia.setRecordID(String.valueOf(record.getRecordID()));
                    retVal = ia;
                    break;
                }
            }
        }

        return retVal;
    }
}
