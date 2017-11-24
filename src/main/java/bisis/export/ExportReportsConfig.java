package bisis.export;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


/**
 * Created by Petar on 11/22/2017.
 *
 * reports.ini fajlovi ne smeju imati // na pocetku!
 */
public class ExportReportsConfig {

    public static List<Object> export(String[] args) {
        Options options = new Options();
        options.addOption("f", "file", true,
                "Path to \'reports.ini\'");
        options.addOption("l", "library", true,
                "Library code (gbns)");
        CommandLineParser parser = new GnuParser();

        String file = "";
        String library = "";
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("f"))
                file = cmd.getOptionValue("f");
            else
                throw new Exception("Specify path to \'reports.ini\'");
            if (cmd.hasOption("l"))
                library = cmd.getOptionValue("l");
            else
                throw new Exception("Specify library code!");

            List<Object> retVal = parseReports(file, library);
            System.out.println("Sucessfully parsed reports.ini configuration for library: " + library);
            return retVal;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    private static List<Object> parseReports(String reportsIniPath, String library) throws IOException, BackingStoreException {
        Ini ini = new Ini(new File(reportsIniPath));
        Preferences prefs = new IniPreferences(ini);

        JSONArray parsedJson = new JSONArray();

        for (String nodeName: prefs.childrenNames()){
            if (nodeName.equals("global"))
                continue;

            if (nodeName.toLowerCase().contains("online"))
                continue;

            JSONObject report = new JSONObject();

            for (String propName: ((IniPreferences) prefs).node(nodeName).keys()){
                if(propName.equals("class")) {
                    String name =  ((IniPreferences) prefs).node(nodeName).get(propName, null);
                    name = name.substring(name.lastIndexOf(".") + 1);
                    String className = "com.ftninformatika.bisis." + library + "." + name;
                    report.put("className", className);
                    continue;
                }
                if(propName.equals("file")) {
                    report.put("reportName", ((IniPreferences) prefs).node(nodeName).get(propName, null));
                    continue;
                }

                if(propName.equals("menuitem")) {
                    report.put("menuitem", ((IniPreferences) prefs).node(nodeName).get(propName, null));
                    continue;
                }
                if(propName.equals("invnumpattern")) {
                    report.put("invnumpattern", ((IniPreferences) prefs).node(nodeName).get(propName, null));
                    continue;
                }
                if(propName.equals("type")) {
                    report.put("type", ((IniPreferences) prefs).node(nodeName).get(propName, null));
                    continue;
                }
                if(propName.equals("jasper")) {
                    String parsedJasper =  ((IniPreferences) prefs).node(nodeName).get(propName, null);
                    parsedJasper = parsedJasper.substring(parsedJasper.lastIndexOf("/"));
                    String jasper = '/' + "jaspers" + '/'  + library + parsedJasper;
                    report.put("jasper", jasper);
                    continue;
                }
            }
            parsedJson.put(report);
        }
        return parsedJson.toList();
    }

}
