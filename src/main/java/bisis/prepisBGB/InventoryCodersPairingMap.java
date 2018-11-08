package bisis.prepisBGB;

import java.util.HashMap;
import java.util.Map;

public class InventoryCodersPairingMap {

    static Map<String, String> internalMarkMap = new HashMap<>();

    static {
        internalMarkMap.put("depozit", "DEP");
        internalMarkMap.put("citaonica", "ČIT");
        internalMarkMap.put("d citaonica", "DČIT");
        internalMarkMap.put("magacin", "MAG");
        internalMarkMap.put("čitaonica", "ČIT");
        internalMarkMap.put("d čitaonica", "DČIT");
        internalMarkMap.put("20", "ČIT");
    }
}
