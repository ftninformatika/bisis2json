package bisis.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

/**
 * Created by Petar on 8/30/2017.
 */
public class ExportClientConfig {

    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args){
        Options options = new Options();
        options.addOption("c", "cliconf", true,
                "Path to client-config.ini file");
        options.addOption("l", "library", true,
                "Library name(code): gbns, bgb, gbsa...");
        options.addOption("r", "repconf", true,
                "Path to reports.ini file");
        options.addOption("o", "output", true,
                "Output file");

        CommandLineParser parser = new GnuParser();
        String input = "";
        String inputr = "";
        String output = "";
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("c"))
                input = cmd.getOptionValue("c");
            else
                throw new Exception("Specify path to client-config.ini file");
            if (cmd.hasOption("r"))
                inputr = cmd.getOptionValue("r");
            else
                throw new Exception("Specify path to reports.ini file");
            if (cmd.hasOption("o"))
                output = cmd.getOptionValue("o");
            else
                throw new Exception("Specify output file");
        } catch (Exception ex) {
            System.err.println("Invalid parameter(s), reason: " + ex.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("bisis2json-export-client-config", options);
            return;
        }
        try {

            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF8")));
            export(input, inputr, out, output);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void export(String input, String inputr, PrintWriter out, String outputPath) throws Exception {
        FileInputStream fis = new FileInputStream(input);

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        StringBuffer outString = new StringBuffer();
        outString.append("{\n");
        String header = "";

        String line = null;
        while ((line = br.readLine()) != null) {

            if (line.startsWith(";"))  //komentar
                continue;

            if (isHeader(line)) {
                header = makeHeader(line);
                continue;
            }

            if (isValueLine(line))
                outString.append(getPropName(line,header) + getPropValue(line) + "\n");

        }
        int lastComma = outString.lastIndexOf(",");
        outString.replace(lastComma, lastComma+1, "\n}");

        if (isJSONValid(outString.toString())) {

            //collecting reports.ini configuration
            List<Object> reportsConfList = ExportReportsConfig.export(new String[]{"-f", inputr, "-l", "gbns"});
            JSONObject jo = new JSONObject(outString.toString());
            jo.put("reports", reportsConfList);
            out.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jo.toMap()));
            out.close();
            System.out.println("Successfully parsed client-config.ini to " + outputPath + "\n");
        }
        else
            throw new Exception("Resulted JSON is not formatted properely, something went wrong!");

        br.close();

    }

    private static String getPropValue(String line) {
          if (line.split("=").length <= 1)
              return "\"\",";
          return "\"" + line.split("=")[1].trim() + "\",";
    }

    private static String getPropName(String line, String header) {
          return "\"" + header + WordUtils.capitalize(line.split("=")[0].trim()) + "\": ";
    }

    private static String makeHeader(String line){
        String retVal = "";
        retVal = line.substring(1,line.lastIndexOf(']'));
        String[] arr = retVal.split("-");
        if (arr.length > 1)
            for (int i = 1; i<arr.length; i++)
                arr[i] = WordUtils.capitalize(arr[i]);
        retVal = String.join("",arr);
        return retVal;
    }

    private static boolean isHeader(String line){
        if(line.startsWith("[") && line.endsWith("]"))
            return true;
        return false;
    }

    private static boolean isValueLine(String line){
        if (!isHeader(line) && !line.startsWith(";") && StringUtils.countMatches(line, "=") == 1)
            return true;
        return false;
    }

    private static boolean isJSONValid(String jsonInString ) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
