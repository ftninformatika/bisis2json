package bisis.utils;

import bisis.winisis2bisis.SubfieldDataPair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WinIsisUtils {

    public static final String SUBFIELD_DELIMITER = "\\^";

    public static final String FIELD_REGEX = "^!v\\d{3}!";

    public static String computeInvNumber(String shortInv) {
        String retVal = "";

        return retVal;
    }

    public static Date makeDate(String shortDate) {
        Date retVal = null;

        return retVal;
    }

    public static List<SubfieldDataPair> getSubfieldsList(String line) {
        List<SubfieldDataPair> retVal = new ArrayList<>();
        String field = "";

        if (line.equals("") || !line.substring(0,6).matches(FIELD_REGEX))
            return retVal;
        else {
            field = line.substring(2,5);
            // Polje bez potpolja
            if (line.split(SUBFIELD_DELIMITER).length == 1) {
                String data = line.substring(6,line.length());
                SubfieldDataPair sfPair = new SubfieldDataPair(field, data);
                retVal.add(sfPair);
            }
            // Polje sa potpoljima
            else {
                String[] chunks = line.split(SUBFIELD_DELIMITER);
                for(int i = 1; i < chunks.length; i++) {
                    String sf = chunks[i].substring(0,1);
                    String data = chunks[i].substring(1, chunks[i].length());
                    SubfieldDataPair sfPair = new SubfieldDataPair(field + sf, data);
                    retVal.add(sfPair);
                }

            }
        }

        return retVal;
    }

}
