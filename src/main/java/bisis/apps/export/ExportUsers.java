package bisis.apps.export;

import bisis.model.circ.*;
import bisis.apps.prepis_bgb.MemberCodersPairingMap;
import bisis.utils.DaoUtils;
import bisis.utils.DateUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.cli.*;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
public class ExportUsers {
  
  public static void main(Connection conn, String[] args) {
    //MongoClient mc = new MongoClient("localhost",27017);
    Options options = new Options();

    options.addOption("l", "library", true,
            "Library code (gbns, gbsa, tfzr...)");
    options.addOption("o", "output", true,
            "Output file");
    CommandLineParser parser = new GnuParser();

    String library = "";
    String outputFile = "";
    try {
      CommandLine cmd = parser.parse(options, args);

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
      export(conn, out, library);
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
    PreparedStatement userCategoryPS = conn.prepareStatement("SELECT * from user_categs where id=?");
    PreparedStatement corporateMemberPS = conn.prepareStatement("SELECT * from `groups` where sys_id=?");
    PreparedStatement duplicatesPS = conn.prepareStatement("SELECT * from duplicate where id=?");
    PreparedStatement picturebooksPS = conn.prepareStatement("SELECT * from picturebooks where id=?");
    PreparedStatement languagePS = conn.prepareStatement("SELECT * from languages where id=?");
    PreparedStatement eduLvlPS = conn.prepareStatement("SELECT * from edu_lvl where id=?");

    String organizationsMapJson = new Scanner(new File("export" + library.toUpperCase() + "/circ_coders_json_output/organization_id-id.json")).useDelimiter("\\Z").next();
    Map<Integer, String> orgMap = mapper.readValue(organizationsMapJson, new TypeReference<Map<Integer, String>>(){});

    ResultSet rset = stmt.executeQuery("SELECT sys_id, organization, languages, edu_lvl, mmbr_type, user_categ, `groups`, user_id, first_name, last_name, parent_name, address, city, zip, phone, email, jmbg, doc_id, doc_no, doc_city, country, gender, age, sec_address, sec_zip, sec_city, sec_phone, note, interests, warning_ind, occupation, title, index_no, class_no, pass, block_reason FROM users");
    while (rset.next()) {
      if (++userCount % 1000 == 0)
        System.out.println("members exported: " + userCount);
      Member member = new Member();

      //Organization
      organizationPS.setInt(1, rset.getInt("organization"));
      if (!rset.wasNull()) {
        ResultSet rOrg = organizationPS.executeQuery();
        if (rOrg.next()) {

          Organization org = new Organization();
          org.set_id(orgMap.get(rOrg.getInt("id")));
          org.setDescription(rOrg.getString("name"));
          org.setAddress(rOrg.getString("address"));
          org.setCity(rOrg.getString("city"));
          org.setZip(rOrg.getString("zip"));
          member.setOrganization(org);
        }
        rOrg.close();
      }

      //MembershipType
      membershipTypePS.setInt(1, rset.getInt("mmbr_type"));
      if(!rset.wasNull()) {
        ResultSet rMmbrt = membershipTypePS.executeQuery();

        // bgb slucaj, kada postoji mapiranje sifarnika
        if (Mysql2MongoBisisMigrationTool.library.equals("bgb")) {
          if(rMmbrt.next()) {
            String name = rMmbrt.getString("name");
            MembershipType mmbType = MemberCodersPairingMap.getMmbrTypeByName(name);
            member.setMembershipType(mmbType);
          }
        }
        else {
          MembershipType mmbrt = new MembershipType();
          if (rMmbrt.next()) {
            mmbrt.setDescription(rMmbrt.getString("name"));
            mmbrt.setPeriod(DaoUtils.getInteger(rMmbrt, "period"));
            mmbrt.setLibrary(library);
            member.setMembershipType(mmbrt);
          }
        }
        rMmbrt.close();
      }

      //UserCategory
      userCategoryPS.setInt(1, rset.getInt("user_categ"));
      if(!rset.wasNull()) {
        ResultSet rUc = userCategoryPS.executeQuery();

        // specijalni slucaj bgb, kada postoji mapiranje sifarnika
        if (Mysql2MongoBisisMigrationTool.library.equals("bgb")) {
          if (rUc.next()) {
            String ucDesc = rUc.getString("name");
            UserCategory uc = MemberCodersPairingMap.getUserCategMappedByDesc(ucDesc);
            member.setUserCategory(uc);
          }
        }
        else {
          if (rUc.next()) {
            UserCategory uC = new UserCategory();
            uC.setLibrary(library);
            uC.setDescription(rUc.getString("name"));
            uC.setTitlesNo(rUc.getInt("titles_no"));
            uC.setPeriod(rUc.getInt("period"));
            if (ExportCoders.hasColumn(rUc, "max_period"))
              uC.setMaxPeriod(rUc.getInt("max_period"));
            else
              uC.setMaxPeriod(5000);
            member.setUserCategory(uC);
          }
        }
        rUc.close();
      }
      //CorporateMember
        corporateMemberPS.setInt(1, rset.getInt("groups"));
        if (!rset.wasNull()) {
          ResultSet rCpm = corporateMemberPS.executeQuery();
          if (rCpm.next()) {
            CorporateMember cm = new CorporateMember();
            cm.setUserId(rCpm.getString("user_id"));
            cm.setInstName(rCpm.getString("inst_name"));
            cm.setSignDate(DateUtils.getInstant(rCpm, "sign_date"));
            cm.setAddress(rCpm.getString("address"));
            cm.setCity(rCpm.getString("city"));
            cm.setZip(DaoUtils.getInteger(rCpm,"zip"));
            cm.setPhone(rCpm.getString("phone"));
            cm.setEmail(rCpm.getString("email"));
            cm.setFax(rCpm.getString("fax"));
            cm.setSecAddress(rCpm.getString("sec_address"));
            cm.setSecCity(rCpm.getString("sec_city"));
            cm.setSecZip(DaoUtils.getInteger(rCpm,"sec_zip"));
            cm.setSecPhone(rCpm.getString("sec_phone"));
            cm.setContFirstName(rCpm.getString("cont_fname"));
            cm.setContLastName(rCpm.getString("cont_lname"));
            cm.setContEmail(rCpm.getString("cont_email"));
            member.setCorporateMember(cm);
          }
          rCpm.close();
        }

      languagePS.setInt(1, rset.getInt("languages"));
      if (!rset.wasNull()) {
        ResultSet rL = languagePS.executeQuery();
        if (rL.next()) {
          member.setLanguage(rL.getString("name"));
        }
        rL.close();
      }

      eduLvlPS.setInt(1,rset.getInt("edu_lvl"));
      if (!rset.wasNull()) {
        ResultSet rEd = eduLvlPS.executeQuery();
        if (rEd.next()) {
          member.setEducationLevel(rEd.getString("name"));
        }
        rEd.close();
      }

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
      member.setDocId(DaoUtils.getInteger(rset,"doc_id"));
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
      member.setWarningInd(DaoUtils.getInteger(rset,"warning_ind"));
      member.setOccupation(rset.getString("occupation"));
      member.setTitle(rset.getString("title"));
      member.setIndexNo(rset.getString("index_no"));
      member.setClassNo(DaoUtils.getInteger(rset,"class_no"));
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
        signing.setSignDate(DateUtils.getInstant(r2, "sign_date"));


        signing.setLocation(locationsMap.get(DaoUtils.getInteger(r2, "location")));
        signing.setUntilDate(DateUtils.getInstant(r2, "until_date"));
        signing.setCost(r2.getDouble("cost"));
        signing.setReceipt(r2.getString("receipt_id"));
        signing.setLibrarian(r2.getString("librarian") + "@" + library);
        member.getSignings().add(signing);
      }
      r2.close();

      duplicatesPS.setInt(1, rset.getInt("sys_id"));
      if (!rset.wasNull()) {
        ResultSet rDu = duplicatesPS.executeQuery();
        List<Duplicate> dups = new ArrayList<>();
        while (rDu.next()) {
          Duplicate d = new Duplicate();
          d.setDupDate(DateUtils.getInstant(rDu, "dup_date"));
          d.setDupNo(rDu.getInt("dup_no"));
          dups.add(d);
        }
        member.setDuplicates(dups);
      }

      picturebooksPS.setInt(1, rset.getInt("sys_id"));
      if(!rset.wasNull()) {
        ResultSet rPb = picturebooksPS.executeQuery();
        List<PictureBook> pictureBooks = new ArrayList<>();
        while (rPb.next()) {
          PictureBook p = new PictureBook();
          p.setLendDate(DateUtils.getInstant(rPb, "sdate"));
          p.setLendNo(DaoUtils.getInteger(rPb,"lend_no"));
          p.setReturnNo(DaoUtils.getInteger(rPb,"return_no"));
          p.setStatus(DaoUtils.getInteger(rPb,"state"));
          pictureBooks.add(p);
        }
        member.setPicturebooks(pictureBooks);
      }

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
