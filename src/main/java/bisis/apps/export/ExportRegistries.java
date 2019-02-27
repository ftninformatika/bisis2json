package bisis.apps.export;

import bisis.model.coders.GenericRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExportRegistries {

    private static ObjectMapper mapper = new ObjectMapper();
    private static final String FILE_NAME =
            "export" + Mysql2MongoBisisMigrationTool.library.toUpperCase() +
                    File.separator + "registries.json";

    public static final int AUTORI       = 1;
    public static final int ODREDNICE    = 2;
    public static final int PODODREDNICE = 3;
    public static final int ZBIRKE       = 4;
    public static final int UDK          = 5;
    public static final int KOLEKTIVNI   = 6;

    static {
        //mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }


    public static void export(Connection conn) {


        Statement stmt = null;
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream( FILE_NAME), "UTF8")));

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from registar_autori");


            while(rs.next()) {
                GenericRegistry g = new GenericRegistry();
                g.setCode(AUTORI);
                g.setField1(rs.getString("autor"));
                g.setField2(rs.getString("original"));
                pw.write(toJSONRegistry(g));
            }

            rs = stmt.executeQuery("SELECT * from registar_odr");
            while(rs.next()) {
                GenericRegistry g = new GenericRegistry();
                g.setCode(ODREDNICE);
                g.setField1(rs.getString("pojam"));
                pw.write(toJSONRegistry(g));
            }

            rs = stmt.executeQuery("SELECT * from registar_pododr");
            while(rs.next()) {
                GenericRegistry g = new GenericRegistry();
                g.setCode(PODODREDNICE);
                g.setField1(rs.getString("pojam"));
                pw.write(toJSONRegistry(g));
            }

            rs = stmt.executeQuery("SELECT * from registar_zbirke");
            while(rs.next()) {
                GenericRegistry g = new GenericRegistry();
                g.setCode(ZBIRKE);
                g.setField1(rs.getString("naziv"));
                pw.write(toJSONRegistry(g));
            }

            rs = stmt.executeQuery("SELECT * from registar_udk");
            while(rs.next()) {
                GenericRegistry g = new GenericRegistry();
                g.setCode(UDK);
                g.setField1(rs.getString("grupa"));
                g.setField2(rs.getString("opis"));
                pw.write(toJSONRegistry(g));
            }

            rs = stmt.executeQuery("SELECT * from registar_kolektivni");
            while(rs.next()) {
                GenericRegistry g = new GenericRegistry();
                g.setCode(KOLEKTIVNI);
                g.setField1(rs.getString("kolektivac"));
                pw.write(toJSONRegistry(g));
            }

            stmt.close();
            pw.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private static String toJSONRegistry(GenericRegistry c) {
        try {
            return mapper.writeValueAsString(c);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

}
