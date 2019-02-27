package bisis.utils;

import bisis.winisis2bisis.WField;
import bisis.winisis2bisis.WSubField;

import java.util.Date;

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

    public static WField makeWField(String line) {
        WField retVal = new WField();

        String field = "";

        if (line.equals("") || !line.substring(0,6).matches(FIELD_REGEX))
            return null;
        else {
            field = line.substring(2,5);
            retVal.setName(field);
            String[] chunks = line.split(SUBFIELD_DELIMITER);
            // Polje bez potpolja
            if (line.split(SUBFIELD_DELIMITER).length == 1) {
                String data = line.substring(6,line.length());
                retVal.setContent(data);
            }
            if (retVal.getContent() == null && !line.substring(6, line.length()).startsWith("^"))
                retVal.setContent(line.substring(6, line.indexOf('^')));
            // Polje sa potpoljima
            else {
                for(int i = 1; i < chunks.length; i++) {
                    String sf = chunks[i].substring(0,1);
                    String data = chunks[i].substring(1, chunks[i].length());
                    retVal.getSubfields().add(new WSubField(sf, data));
                }
            }
        }
        return retVal;
    }
}
