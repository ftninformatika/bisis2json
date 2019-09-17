package bisis.apps.cobiss2bisis;


import bisis.model.records.Record;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

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

        for (File f: directoryListing) {
            try {
                InputStream is = Files.newInputStream(f.toPath());
                String jsonTxt = IOUtils.toString(is);
                JSONObject jsonObject = new JSONObject(jsonTxt);
                is.close();
                Cobiss2BisisRecordGenerator generator = new Cobiss2BisisRecordGenerator();
                Record r = generator.generateRecord(jsonObject);
                System.out.println(r);
            } catch (IOException e) {
                System.out.println("Can't read file: " + f.getAbsolutePath());
                e.printStackTrace();
            }
        }

    }
}
