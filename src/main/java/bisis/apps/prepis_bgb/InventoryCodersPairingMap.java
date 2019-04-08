package bisis.apps.prepis_bgb;

import java.util.HashMap;
import java.util.Map;

public class InventoryCodersPairingMap {

    static Map<String, String> internalMarkMap = new HashMap<>();
    static Map<String, String> acqTypeMap  = new HashMap<>();
    static {
        // mapiranje internih oznaka iz opstinskih bibl BISIS3 -> BISIS5 (BGB), na interne oznake u
        internalMarkMap.put("depozit", "DEP");
        internalMarkMap.put("citaonica", "ČIT");
        internalMarkMap.put("d citaonica", "DČIT");
        internalMarkMap.put("magacin", "MAG");
        internalMarkMap.put("čitaonica", "ČIT");
        internalMarkMap.put("d čitaonica", "DČIT");
        internalMarkMap.put("20", "ČIT");
        internalMarkMap.put("kancelarija", "MAG");
        internalMarkMap.put("šuber", "DEP");

        // nacin nabavke mapiranje, periodika BGB
        acqTypeMap.put("a", "k");
        acqTypeMap.put("b", "b");
        acqTypeMap.put("c", "p");
        acqTypeMap.put("d", "n");
        acqTypeMap.put("e", "t");
        acqTypeMap.put("f", "i");
        acqTypeMap.put("k", "k");
        acqTypeMap.put("o", "o");
        acqTypeMap.put("p", "p");
        acqTypeMap.put("s", "i");

    }
}
