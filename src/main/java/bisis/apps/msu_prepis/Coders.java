package bisis.apps.msu_prepis;

import bisis.model.coders.Coder;
import bisis.model.coders.Counter;
import bisis.model.records.Primerak;
import bisis.model.records.Record;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bisis.apps.export.ExportCoders.toJSONCoder;

public class Coders {

    public static final String CODER_TYPE_ACCESSION_REG = "coders.accessionReg";
    public static final String CODER_TYPE_LOCATION = "coders.location";
    public static final String CODER_TYPE_PROCESS_TYPE = "coders.process_types";
    public static final String CODER_TYPE_STATUS = "coders.status";
    public static final String CODER_TYPE_INTERNAL_MARK = "coders.internalMark";
    public static final String CODER_TYPE_COUNTER = "coders.counters";

    public static List<Coder> make(String coderName, String library, List<Record> records) {
        switch (coderName) {
            case CODER_TYPE_ACCESSION_REG:
                return makeAccessionReg(library);
            case CODER_TYPE_LOCATION:
                return makeLocation(library);
            case CODER_TYPE_PROCESS_TYPE:
                // manually copied
                return new ArrayList<>();
            case CODER_TYPE_STATUS:
                return makeStatus(library);
            case CODER_TYPE_INTERNAL_MARK:
                return makeInternalMark(library);
            case CODER_TYPE_COUNTER:
                return makeCounters(library, records);
            default: throw new IllegalArgumentException("Unknown coder name: " + coderName);
        }
    }

    public static List<Coder> makeAccessionReg(String library) {
        Map<String, String> map = new HashMap<>();
        map.put("00", "Monografske publikacije");
        map.put("01", "Izložbeni katalozi");
        map.put("02", "Serijske publikacije");

        return getCoders(library, map);
    }

    public static List<Coder> makeLocation(String library) {
        Map<String, String> map = new HashMap<>();
        map.put("00", "Biblioteka");

        return getCoders(library, map);
    }

    public static List<Coder> makeStatus(String library) {
        Map<String, String> map = new HashMap<>();
        map.put("A", "Aktivno");
        map.put("7", "?");
        map.put("8", "?");

        return getCoders(library, map);
    }

    public static List<Coder> makeInternalMark(String library) {
        Map<String, String> map = new HashMap<>();
        map.put("D", "Domaća");
        map.put("S", "Strana");

        return getCoders(library, map);
    }

    public static List<Coder> makeCounters(String library, List<Record> records) {
        int maxRn = records.stream()
                .mapToInt(Record::getRN)
                .max()
                .orElse(0);
        int maxPrimerakId = records.stream()
                .flatMap(r -> r.getPrimerci().stream())
                .mapToInt(Primerak::getPrimerakID)
                .max()
                .orElse(0);

        Map<String, Integer> map = new HashMap<>();
        map.put("RN", maxRn);
        map.put("recordid", maxRn);
        map.put("primerakid", maxPrimerakId);
        map.put("sveskaid", 0);
        map.put("godinaid", 0);

        List<Coder> coders = new ArrayList<>();
        for (String id : map.keySet()) {
            Integer value = map.get(id);
            Counter c = new Counter();
            c.setCoder_id(id);
            c.setLibrary(library);
            c.setCounterValue(value);
            coders.add(c);
        }
        return coders;
    }

    private static List<Coder> getCoders(String library, Map<String, String> map) {
        List<Coder> coders = new ArrayList<>();
        for (String id : map.keySet()) {
            String description = map.get(id);
            Coder c = new Coder();
            c.setCoder_id(id);
            c.setDescription(description);
            c.setLibrary(library);
            coders.add(c);
        }
        return coders;
    }

    public static void write(List<Coder> coders, String path) throws IOException {
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(path)), StandardCharsets.UTF_8)));
        pw.write(toJSONCoder(coders));
        pw.close();
    }
}
