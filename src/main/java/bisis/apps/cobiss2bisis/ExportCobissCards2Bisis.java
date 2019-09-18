package bisis.apps.cobiss2bisis;


import bisis.model.records.Record;
import bisis.model.records.serializers.JSONSerializer;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Glavna klasa koja pokrece prepisivanje COBISS .json fajlova zapisa
 * u BISIS format zapisa
 * @author badf00d21  17.9.19.
 */
public class ExportCobissCards2Bisis {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Enter path to folder that contains .json cobiss files as argument!");
            System.exit(0);
        }

        String path = args[0];
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing == null || directoryListing.length == 0) {
            System.out.println("Invalid path!");
            System.exit(0);
        }
        List<Record> exportRecs = new ArrayList<>();
        for (File f: directoryListing) {
            try {
                InputStream is = Files.newInputStream(f.toPath());
                String jsonTxt = IOUtils.toString(is);
                JSONObject jsonObject = new JSONObject(jsonTxt);
                is.close();
                Cobiss2BisisRecordGenerator generator = new Cobiss2BisisRecordGenerator();
                Record r = generator.generateRecord(jsonObject);
                exportRecs.add(r);
                System.out.println(r);
            } catch (IOException e) {
                System.out.println("Can't read file: " + f.getAbsolutePath());
                e.printStackTrace();
            }
        }

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("exportRecordsBogatic.json"), "UTF8")));
            out.println("[");
            for (Record r: exportRecs)
                out.println(JSONSerializer.toJSON(r) + ",");
            out.println("]");
            out.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
