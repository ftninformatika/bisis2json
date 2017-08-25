package bisis.export;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.core.exceptions.DataReadException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import bisis.circ.Lending;
import bisis.circ.Signing;
import bisis.circ.User;

public class ExportUsers {
  
  public static void main(String[] args) {
    Options options = new Options();
    options.addOption("a", "address", true,
        "MySQL server address (default: localhost)");
    options.addOption("p", "port", true, "MySQL server port (default: 3306)");
    options.addOption("d", "database", true,
        "MySQL database name (default: bisis)");
    options.addOption("u", "username", true,
        "MySQL server username (default: bisis)");
    options.addOption("w", "password", true,
        "MySQL server password (default: bisis)");
    options.addOption("o", "output", true,
        "Output file");
    CommandLineParser parser = new GnuParser();
    String address = "localhost";
    String port = "3306";
    String database = "bisis";
    String username = "bisis";
    String password = "bisis";
    String outputFile = "";
    try {
      CommandLine cmd = parser.parse(options, args);
      if (cmd.hasOption("a"))
        address = cmd.getOptionValue("a");
      if (cmd.hasOption("p"))
        port = cmd.getOptionValue("p");
      if (cmd.hasOption("d"))
        database = cmd.getOptionValue("d");
      if (cmd.hasOption("u"))
        username = cmd.getOptionValue("u");
      if (cmd.hasOption("w"))
        password = cmd.getOptionValue("w");
      if (cmd.hasOption("o"))
        outputFile = cmd.getOptionValue("o");
      else
        throw new Exception("Output file not specified.");
    } catch (Exception ex) {
      System.err.println("Invalid parameter(s), reason: " + ex.getMessage());
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("bisis2json-export-users", options);
      return;
    }
    try {
      PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF8")));
      Connection conn = DriverManager.getConnection("jdbc:mysql://" + address
          + ":" + port + "/" + database + "?useSSL=false&serverTimezone=CET", username, password);
      export(conn, out);
      conn.close();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }  

  public static void export(Connection conn, PrintWriter outputFile) throws Exception {
    int userCount = 0;
    List<User> users = new ArrayList<>();
    Statement stmt = conn.createStatement();
    PreparedStatement lendingPS = conn.prepareStatement("SELECT id, ctlg_no, lend_date, location, return_date, resume_date, deadline, librarian_lend, librarian_return, librarian_resume FROM lending WHERE sys_id=?");
    PreparedStatement signingPS = conn.prepareStatement("SELECT id, sign_date, location, until_date, cost, receipt_id, librarian FROM signing WHERE sys_id=?");
    PreparedStatement organizationPS = conn.prepareStatement("SELECT adress, name, city, zip from organization where id=?");
    PreparedStatement membershipTypePS = conn.prepareStatement("SELECT name, period from mmbr_types where id=?");
    PreparedStatement userCategoryPS = conn.prepareStatement("SELECT name, titles_no, period, max_period from user_categs where id=?");
    PreparedStatement corporateMemberPS = conn.prepareStatement("SELECT * from gruops where user_id=?");
    PreparedStatement duplicatesPS = conn.prepareStatement("SELECT * from duplicate where id=?");
    PreparedStatement picturebooksPS = conn.prepareStatement("SELECT * from picturebooks where id=?");

    ResultSet rset = stmt.executeQuery("SELECT sys_id, organization, languages, edu_lvl, mmbr_type, user_categ, groups, user_id, first_name, last_name, parent_name, address, city, zip, phone, email, jmbg, doc_id, doc_no, doc_city, country, gender, age, sec_address, sec_zip, sec_city, sec_phone, note, interests, warning_ind, occupation, title, index_no, class_no, pass, block_reason FROM users");
    while (rset.next()) {
      if (++userCount % 1000 == 0)
        System.out.println("users exported: " + userCount);
      User user = new User();
      //user.setSysId(rset.getInt("sys_id"));


      organizationPS.setInt(1, rset.getInt("organization"));

      rset.getInt("organization");
      //user.setOrganizationId(rset.getInt("organization"));
      //user.setLanguages(rset.getInt("languages"));
      //user.setEducationLevel(rset.getInt("edu_lvl"));
      //user.setMembershipType(rset.getInt("mmbr_type"));
      //user.setUserCategory(rset.getInt("user_categ"));
      //user.setGroups(rset.getInt("groups"));
      //user.setUserId(rset.getString("user_id"));
      //user.setFirstName(rset.getString("first_name"));
      //user.setLastName(rset.getString("last_name"));
      //user.setParentName(rset.getString("parent_name"));
      //user.setAddress(rset.getString("address"));
      //user.setCity(rset.getString("city"));
      //user.setZip(rset.getInt("zip"));
      /*user.setPhone(rset.getString("phone"));
      user.setEmail(rset.getString("email"));
      user.setJmbg(rset.getString("jmbg"));
      user.setDocId(rset.getInt("doc_id"));
      user.setDocNo(rset.getString("doc_no"));
      user.setDocCity(rset.getString("doc_city"));
      user.setCountry(rset.getString("country"));
      user.setGender(rset.getString("gender"));
      user.setAge(rset.getString("age"));
      user.setSecAddress(rset.getString("sec_address"));
      user.setSecZip(rset.getInt("sec_zip"));
      user.setSecCity(rset.getString("sec_city"));
      user.setSecPhone(rset.getString("sec_phone"));
      user.setNote(rset.getString("note"));
      user.setInterests(rset.getString("interests"));
      user.setWarningInd(rset.getInt("warning_ind"));
      user.setOccupation(rset.getString("occupation"));
      user.setTitle(rset.getString("title"));
      user.setIndexNo(rset.getString("index_no"));
      user.setClassNo(rset.getInt("class_no"));
      user.setPass(rset.getString("pass"));
      user.setBlockReason(rset.getString("block_reason"));

      lendingPS.setInt(1, user.getSysId());
      ResultSet r1 = lendingPS.executeQuery();
      while (r1.next()) {
        Lending lending = new Lending();
        lending.setId(r1.getInt("id"));
        lending.setCtlgNo(r1.getString("ctlg_no"));
        lending.setLendDate(getDate(r1, "lend_date"));
        lending.setLocation(r1.getInt("location"));
        lending.setReturnDate(getDate(r1, "return_date"));
        lending.setResumeDate(getDate(r1, "resume_date"));
        lending.setDeadline(getDate(r1, "deadline"));
        lending.setLibrarianLend(r1.getString("librarian_lend"));
        lending.setLibrarianReturn(r1.getString("librarian_return"));
        lending.setLibrarianResume(r1.getString("librarian_resume"));
        user.getLending().add(lending);
      }
      r1.close();

      signingPS.setInt(1, user.getSysId());
      ResultSet r2 = signingPS.executeQuery();
      while (r2.next()) {
        Signing signing = new Signing();
        signing.setId(r2.getInt("id"));
        signing.setSignDate(getDate(r2, "sign_date"));
        signing.setLocation(r2.getInt("location"));
        signing.setUntilDate(getDate(r2, "until_date"));
        signing.setCost(r2.getBigDecimal("cost"));
        signing.setReceiptId(r2.getString("receipt_id"));
        signing.setLibrarian(r2.getString("librarian"));
        user.getSigning().add(signing);
      }
      r2.close();
      
      users.add(user);*/
    }
    rset.close();
    stmt.close();
    lendingPS.close();
    signingPS.close();
    
    for (User user: users) {
      outputFile.write(toJSON(user));
    }
    
    System.out.println("Total users exported: " + userCount);
    
  }
  
  private static LocalDate getDate(ResultSet rset, String columnName)  {

    try {
      java.sql.Date date = rset.getDate(columnName);
      if (date == null)
        return null;
      return date.toLocalDate();
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

  private static String toJSON(User user) {
    try {
      return mapper.writeValueAsString(user);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "";
    }
  }
  
  private static ObjectMapper mapper = new ObjectMapper();
  
  static {
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
  }

}
