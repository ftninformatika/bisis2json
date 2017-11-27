package bisis.export;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

import bisis.circ.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.bson.Document;

public class ExportUsers {
  
  public static void main(String[] args) {
    //MongoClient mc = new MongoClient("localhost",27017);
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
    options.addOption("l", "library", true,
            "Library code (gbns, gbsa, tfzr...)");
    options.addOption("o", "output", true,
            "Output file");
    CommandLineParser parser = new GnuParser();
    String address = "localhost";
    String port = "3306";
    String database = "bisis";
    String username = "bisis";
    String password = "bisis";
    String library = "";
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
      if (cmd.hasOption("l"))
        library = cmd.getOptionValue("l");
      else
        throw new Exception("Library code not specified.");
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
      export(conn, out, library);
      conn.close();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }  

  public static void export(Connection conn, PrintWriter outputFile, String library) throws Exception {
    int userCount = 0;
    List<Member> members = new ArrayList<>();
    Statement stmt = conn.createStatement();
    //PreparedStatement lendingPS = conn.prepareStatement("SELECT id, ctlg_no, lend_date, location, return_date, resume_date, deadline, librarian_lend, librarian_return, librarian_resume FROM lending WHERE sys_id=?");
    PreparedStatement signingPS = conn.prepareStatement("SELECT id, sign_date, location, until_date, cost, receipt_id, librarian FROM signing WHERE sys_id=?");
    PreparedStatement organizationPS = conn.prepareStatement("SELECT id, address, name, city, zip from organization where id=?");
    PreparedStatement membershipTypePS = conn.prepareStatement("SELECT name, period from mmbr_types where id=?");
    PreparedStatement userCategoryPS = conn.prepareStatement("SELECT name, titles_no, period, max_period from user_categs where id=?");
    PreparedStatement corporateMemberPS = conn.prepareStatement("SELECT * from groups where user_id=?");
    PreparedStatement duplicatesPS = conn.prepareStatement("SELECT * from duplicate where id=?");
    PreparedStatement picturebooksPS = conn.prepareStatement("SELECT * from picturebooks where id=?");
    PreparedStatement languagePS = conn.prepareStatement("SELECT * from languages where id=?");
    PreparedStatement eduLvlPS = conn.prepareStatement("SELECT * from edu_lvl where id=?");


    ResultSet rset = stmt.executeQuery("SELECT sys_id, organization, languages, edu_lvl, mmbr_type, user_categ, groups, user_id, first_name, last_name, parent_name, address, city, zip, phone, email, jmbg, doc_id, doc_no, doc_city, country, gender, age, sec_address, sec_zip, sec_city, sec_phone, note, interests, warning_ind, occupation, title, index_no, class_no, pass, block_reason FROM users");
    while (rset.next()) {
      if (++userCount % 1000 == 0)
        System.out.println("members exported: " + userCount);
      Member member = new Member();

      //Organization
      organizationPS.setInt(1, rset.getInt("organization"));
      ResultSet rOrg = organizationPS.executeQuery();
      String organizationsMapJson = new Scanner(new File("export" + library.toUpperCase() + "/circ_coders_json_output/organization_id-id.json")).useDelimiter("\\Z").next();
      Map<Integer, String> orgMap = mapper.readValue(organizationsMapJson, new TypeReference<Map<Integer, String>>(){});
      if(rOrg.next()) {

        Organization org = new Organization();
        org.set_id(orgMap.get(rOrg.getInt("id")));
        org.setName(rOrg.getString("name"));
        org.setAddress(rOrg.getString("address"));
        org.setCity(rOrg.getString("city"));
        org.setZip(rOrg.getString("zip"));
        member.setOrganization(org);
      }
      rOrg.close();

      //MembershipType
      membershipTypePS.setInt(1, rset.getInt("mmbr_type"));
      ResultSet rMmbrt = membershipTypePS.executeQuery();
      MembershipType mmbrt = new MembershipType();
      if(rMmbrt.next()){
        mmbrt.setDescription(rMmbrt.getString("name"));
        mmbrt.setPeriod(rMmbrt.getInt("period"));
        mmbrt.setLibrary(library);
        member.setMembershipType(mmbrt);
      }
      rMmbrt.close();

      //UserCategory
      userCategoryPS.setInt(1, rset.getInt("user_categ"));
      ResultSet rUc = userCategoryPS.executeQuery();
      if(rUc.next()){
        UserCategory uC = new UserCategory();
        uC.setLibrary(library);
        uC.setDescription(rUc.getString("name"));
        uC.setTitlesNo(rUc.getInt("titles_no"));
        uC.setPeriod(rUc.getInt("period"));
        uC.setMaxPeriod(rUc.getInt("max_period"));
        member.setUserCategory(uC);
      }
      rUc.close();

      //CorporateMember
      corporateMemberPS.setInt(1, rset.getInt("groups"));
      ResultSet rCpm = corporateMemberPS.executeQuery();
      if(rCpm.next()){
        CorporateMember cm = new CorporateMember();
        cm.setUserId(rCpm.getString("user_id"));
        cm.setInstName(rCpm.getString("inst_name"));
        cm.setSignDate(getDate(rCpm,"sign_date"));
        cm.setAddress(rCpm.getString("address"));
        cm.setCity(rCpm.getString("city"));
        cm.setZip(rCpm.getInt("zip"));
        cm.setPhone(rCpm.getString("phone"));
        cm.setEmail(rCpm.getString("email"));
        cm.setFax(rCpm.getString("fax"));
        cm.setSecAddress(rCpm.getString("sec_address"));
        cm.setSecCity(rCpm.getString("sec_city"));
        cm.setSecZip(rCpm.getInt("sec_zip"));
        cm.setSecPhone(rCpm.getString("sec_phone"));
        cm.setContFirstName(rCpm.getString("cont_fname"));
        cm.setContLastName(rCpm.getString("cont_lname"));
        cm.setContEmail(rCpm.getString("cont_email"));
        member.setCorporateMember(cm);
      }
      rCpm.close();


      languagePS.setInt(1, rset.getInt("languages"));
      ResultSet rL = languagePS.executeQuery();
      if(rL.next()){
        member.setLanguage(rL.getString("name"));
      }
      rL.close();

      eduLvlPS.setInt(1,rset.getInt("edu_lvl"));
      ResultSet rEd = eduLvlPS.executeQuery();
      if(rEd.next()){
        member.setEducationLevel(rEd.getString("name"));
      }
      rEd.close();

      member.setUserId(rset.getString("user_id"));
      member.setFirstName(rset.getString("first_name"));
      member.setLastName(rset.getString("last_name"));
      member.setParentName(rset.getString("parent_name"));
      member.setAddress(rset.getString("address"));
      member.setCity(rset.getString("city"));
      member.setZip(rset.getString("zip"));
      member.setPhone(rset.getString("phone"));
      member.setEmail(rset.getString("email"));
      member.setJmbg(rset.getString("jmbg"));
      member.setDocId(rset.getInt("doc_id"));
      member.setDocNo(rset.getString("doc_no"));
      member.setDocCity(rset.getString("doc_city"));
      member.setCountry(rset.getString("country"));
      member.setGender(rset.getString("gender"));
      member.setAge(rset.getString("age"));
      member.setSecAddress(rset.getString("sec_address"));
      member.setSecZip(rset.getString("sec_zip"));
      member.setSecCity(rset.getString("sec_city"));
      member.setSecPhone(rset.getString("sec_phone"));
      member.setNote(rset.getString("note"));
      member.setInterests(rset.getString("interests"));
      member.setWarningInd(rset.getInt("warning_ind"));
      member.setOccupation(rset.getString("occupation"));
      member.setTitle(rset.getString("title"));
      member.setIndexNo(rset.getString("index_no"));
      member.setClassNo(rset.getInt("class_no"));
      member.setPin(rset.getString("pass"));
      member.setBlockReason(rset.getString("block_reason"));


      signingPS.setInt(1, rset.getInt("sys_id"));
      ResultSet r2 = signingPS.executeQuery();

      Statement stmt2 = conn.createStatement();
      ResultSet locationRs = stmt2.executeQuery("SELECT * from location");
      Map<Integer, String> locationsMap = new HashMap<>();

      while (locationRs.next()){
        locationsMap.put(locationRs.getInt("id") , locationRs.getString("name"));
      }
      stmt2.close();

      while (r2.next()) {
        Signing signing = new Signing();
        signing.setSignDate(getDate(r2, "sign_date"));

        signing.setLocation(locationsMap.get(r2.getInt("location")));
        signing.setUntilDate(getDate(r2, "until_date"));
        signing.setCost(r2.getDouble("cost"));
        signing.setReceipt(r2.getString("receipt_id"));
        signing.setLibrarian(r2.getString("librarian"));
        member.getSignings().add(signing);
      }
      r2.close();

      duplicatesPS.setInt(1, rset.getInt("sys_id"));
      ResultSet rDu = duplicatesPS.executeQuery();
      List<Duplicate> dups = new ArrayList<>();
      while(rDu.next()){
        Duplicate d = new Duplicate();
        d.setDupDate(getDate(rDu, "dup_date"));
        d.setDupNo(rDu.getInt("dup_no"));
        dups.add(d);
      }
      member.setDuplicates(dups);

      picturebooksPS.setInt(1, rset.getInt("sys_id"));
      ResultSet rPb = picturebooksPS.executeQuery();
      List<PictureBook> pictureBooks = new ArrayList<>();
      while (rPb.next()){
        PictureBook p = new PictureBook();
        p.setLendDate(getDate(rPb,"sdate"));
        p.setLendNo(rPb.getInt("lend_no"));
        p.setReturnNo(rPb.getInt("return_no"));
        p.setStatus(rPb.getInt("state"));
        pictureBooks.add(p);
      }
      member.setPicturebooks(pictureBooks);
      members.add(member);
    }
    rset.close();
    stmt.close();

    //lendingPS.close();
    signingPS.close();
    organizationPS.close();
    membershipTypePS.close();
    userCategoryPS.close();
    corporateMemberPS.close();
    duplicatesPS.close();
    picturebooksPS.close();
    languagePS.close();
    eduLvlPS.close();
    
    for (Member member : members) {
      outputFile.write(toJSON(member));
    }
    
    System.out.println("Total members exported: " + userCount);
    
  }

//  private static void insertInMongo(String lib, String jsonMemberString, Integer userId){
//    MongoCollection<Document> coll = Mysql2MongoBisisMigrationTool.mdb.getCollection(lib + "_members");
//    coll.insertOne(Document.parse(jsonMemberString));
//
//  }

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

  private static String toJSON(Member member) {
    try {
      return mapper.writeValueAsString(member);
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
