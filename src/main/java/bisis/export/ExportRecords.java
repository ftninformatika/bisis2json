package bisis.export;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import bisis.prefixes.PrefixConverter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import bisis.records.Record;
import bisis.records.serializers.JSONSerializer;
import bisis.records.serializers.LooseXMLSerializer;
import bisis.textsrv.DBStorage;

public class ExportRecords {
  public static void main(Connection conn, String[] args) {
    Options options = new Options();
    options.addOption("f", "format", true,
        "Export format: xml or json (default: xml)");
    options.addOption("o", "output", true,
        "Output file");
    CommandLineParser parser = new GnuParser();
    String format = "json";
    String outputFile = "";

    try {
      CommandLine cmd = parser.parse(options, args);
      if (cmd.hasOption("f")) {
        format = cmd.getOptionValue("f").toLowerCase();
        if (!"xml".equals(format) && !"json".equals(format))
          throw new Exception("Invalid format specified.");
      }
      if (cmd.hasOption("o"))
        outputFile = cmd.getOptionValue("o");
      else
        throw new Exception("Output file not specified.");
    } catch (Exception ex) {
      System.err.println("Invalid parameter(s), reason: " + ex.getMessage());
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("bisis2json-export-records", options);
      return;
    }
    try {
      PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF8")));
      PrintWriter outElastic = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile.substring(0,outputFile.lastIndexOf(".")) + "Elastic.json"), "UTF8")));
      if ("xml".equals(format))
        out.println("<?xml version=\"1.0\"?>\n<records>");
      List<Integer> docIDs = new ArrayList<Integer>();
      Statement stmt = conn.createStatement();
      ResultSet rset = stmt.executeQuery("SELECT record_id FROM Records");
      while (rset.next())
        docIDs.add(rset.getInt(1));
      rset.close();
      stmt.close();
      System.out.println("Found " + docIDs.size() + " records in the database");
      System.out.println("Exporting " + format.toUpperCase() + " to " + outputFile);
      DBStorage storage = new DBStorage();
      int i = 0;
      for (int id: docIDs) {
        Record rec = storage.get(conn, id);
        if (rec == null){
          System.out.println("Problem parsing record with ID: " + id );
          continue;
        }

        rec.pack();
        if ("xml".equals(format))
          out.println(LooseXMLSerializer.toLooseXML(rec));
        else
          out.println(JSONSerializer.toJSON(rec) );
        outElastic.write(JSONSerializer.toElasticJson(PrefixConverter.toMap(rec, null)));
        if (i % 1000 == 0)
          System.out.println(Integer.toString(i) + " records exported");
        i++;
      }
      if ("xml".equals(format))
        out.println("</records>");
      System.out.println("Total " + Integer.toString(i) + " records exported.");
      out.close();
      outElastic.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
