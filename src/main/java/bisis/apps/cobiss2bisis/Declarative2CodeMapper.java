package bisis.apps.cobiss2bisis;

import bisis.utils.LatCyrUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author badf00d21  18.9.19.
 */
public class Declarative2CodeMapper {

    static Map<String, String> languageMap = new HashMap<>();

    static {
        languageMap.put("српски језик", "srp");
        languageMap.put("srpski jezik", "srp");
        languageMap.put("српски", "srp");
        languageMap.put("италијански", "ita");
        languageMap.put("француски", "fra");
        languageMap.put("шпански", "esp");
    }

    static String mapLanguage2Coder(String val) {
        if (val == null || val.length() < 3) return null;
        val = val.trim();
        if (val.length() == 3) return LatCyrUtils.toLatin(val);
        return languageMap.get(val);
    }

}
