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
        languageMap.put("руски", "rus");
        languageMap.put("енглески", "eng");
        languageMap.put("немачки", "ger");
        languageMap.put("босански", "bos");
        languageMap.put("бугарски", "bul");
        languageMap.put("кинески", "chi");
        languageMap.put("латински", "lat");
        languageMap.put("шпански", "esp");
        languageMap.put("хрватски", "hrv");
    }

    static String mapLanguage2Coder(String val) {
        if (val == null || val.length() < 3) return null;
        val = val.trim();
        if (val.length() == 3) return LatCyrUtils.toLatin(val);
        return languageMap.get(val);
    }

}
