package bisis.prepisBGB;

import bisis.circ.*;
import bisis.export.ExportCoders;
import bisis.jongo_circ.*;
import bisis.utils.DaoUtils;
import bisis.utils.DateUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberStorage {


    public MemberStorage(String library, Connection conn) {
        this.library = library;
        this.conn = conn;
    }

    private String library;
    private Connection conn;

    public static final String SELECT_SIGNINGS = "SELECT id, sign_date, location, until_date, cost, receipt_id, librarian FROM signing WHERE sys_id=?";
    public static final String SELECT_ORGANIZATION = "SELECT id, address, name, city, zip from organization where id=?";
    public static final String SELECT_MMBR_TYPES = "SELECT name, period from mmbr_types where id=?";
    public static final String SELECT_USR_CATEGS = "SELECT * from user_categs where id=?";
    public static final String SELECT_CORPORATE_MEMBERS = "SELECT * from groups where sys_id=?";
    public static final String SELECT_DUPLICATES = "SELECT * from duplicate where id=?";
    public static final String SELECT_PICTUREBOOKS = "SELECT * from picturebooks where id=?";
    public static final String SELECT_LANGUAGE = "SELECT * from languages where id=?";
    public static final String SELECT_EDU_LVL = "SELECT * from edu_lvl where id=?";

    public Member get(int sys_id) throws SQLException {
        Statement stmt = conn.createStatement();
        //PreparedStatement lendingPS = conn.prepareStatement("SELECT id, ctlg_no, lend_date, location, return_date, resume_date, deadline, librarian_lend, librarian_return, librarian_resume FROM lending WHERE sys_id=?");
        PreparedStatement signingPS = conn.prepareStatement(SELECT_SIGNINGS);
        PreparedStatement organizationPS = conn.prepareStatement(SELECT_ORGANIZATION);
        PreparedStatement membershipTypePS = conn.prepareStatement(SELECT_MMBR_TYPES);
        PreparedStatement userCategoryPS = conn.prepareStatement(SELECT_USR_CATEGS);
        PreparedStatement corporateMemberPS = conn.prepareStatement(SELECT_CORPORATE_MEMBERS);
        PreparedStatement duplicatesPS = conn.prepareStatement(SELECT_DUPLICATES);
        PreparedStatement picturebooksPS = conn.prepareStatement(SELECT_PICTUREBOOKS);
        PreparedStatement languagePS = conn.prepareStatement(SELECT_LANGUAGE);
        PreparedStatement eduLvlPS = conn.prepareStatement(SELECT_EDU_LVL);

        ResultSet rset = stmt.executeQuery("SELECT sys_id, organization, languages, edu_lvl, mmbr_type, user_categ, groups, user_id, first_name, last_name, parent_name, address, city, zip, phone, email, jmbg, doc_id, doc_no, doc_city, country, gender, age, sec_address, sec_zip," +
                " sec_city, sec_phone, note, interests, warning_ind, occupation, title, index_no, class_no, pass, block_reason FROM users where sys_id = " + sys_id);
        rset.next();
        Member member = new Member();

        //MembershipType
        membershipTypePS.setInt(1, rset.getInt("mmbr_type"));
        if(!rset.wasNull()) {
            ResultSet rMmbrt = membershipTypePS.executeQuery();

            // bgb slucaj, kada postoji mapiranje sifarnika
            if (library.equals("bgb")) {
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

            //specijalni slucaj bgb, mapiranje sifarnika
            if(library.equals("bgb")) {
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
            signing.setLibrarian(r2.getString("librarian"));
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

        rset.close();
        stmt.close();

        signingPS.close();
        organizationPS.close();
        membershipTypePS.close();
        userCategoryPS.close();
        corporateMemberPS.close();
        duplicatesPS.close();
        picturebooksPS.close();
        languagePS.close();
        eduLvlPS.close();
        return member;
    }

    public JoMember getJo(int sys_id) throws SQLException {
        Statement stmt = conn.createStatement();
        //PreparedStatement lendingPS = conn.prepareStatement("SELECT id, ctlg_no, lend_date, location, return_date, resume_date, deadline, librarian_lend, librarian_return, librarian_resume FROM lending WHERE sys_id=?");
        PreparedStatement signingPS = conn.prepareStatement(SELECT_SIGNINGS);
        PreparedStatement organizationPS = conn.prepareStatement(SELECT_ORGANIZATION);
        PreparedStatement membershipTypePS = conn.prepareStatement(SELECT_MMBR_TYPES);
        PreparedStatement userCategoryPS = conn.prepareStatement(SELECT_USR_CATEGS);
        PreparedStatement corporateMemberPS = conn.prepareStatement(SELECT_CORPORATE_MEMBERS);
        PreparedStatement duplicatesPS = conn.prepareStatement(SELECT_DUPLICATES);
        PreparedStatement picturebooksPS = conn.prepareStatement(SELECT_PICTUREBOOKS);
        PreparedStatement languagePS = conn.prepareStatement(SELECT_LANGUAGE);
        PreparedStatement eduLvlPS = conn.prepareStatement(SELECT_EDU_LVL);

        ResultSet rset = stmt.executeQuery("SELECT sys_id, organization, languages, edu_lvl, mmbr_type, user_categ, groups, user_id, first_name, last_name, parent_name, address, city, zip, phone, email, jmbg, doc_id, doc_no, doc_city, country, gender, age, sec_address, sec_zip," +
                " sec_city, sec_phone, note, interests, warning_ind, occupation, title, index_no, class_no, pass, block_reason FROM users where sys_id = " + sys_id);
        rset.next();
        JoMember member = new JoMember();

        //MembershipType
        membershipTypePS.setInt(1, rset.getInt("mmbr_type"));
        if(!rset.wasNull()) {
            ResultSet rMmbrt = membershipTypePS.executeQuery();

            // bgb slucaj, kada postoji mapiranje sifarnika
            if (library.equals("bgb")) {
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

            //specijalni slucaj bgb, mapiranje sifarnika
            if(library.equals("bgb")) {
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
        //if (!library.equals("bgb")) {
            corporateMemberPS.setInt(1, rset.getInt("groups"));
            if (!rset.wasNull()) {
                ResultSet rCpm = corporateMemberPS.executeQuery();
                if (rCpm.next()) {
                    JoCorporateMember cm = new JoCorporateMember();
                    cm.setUserId(rCpm.getString("user_id"));
                    cm.setInstName(rCpm.getString("inst_name"));
                    cm.setSignDate(rCpm.getDate("sign_date"));
                    cm.setAddress(rCpm.getString("address"));
                    cm.setCity(rCpm.getString("city"));
                    cm.setZip(DaoUtils.getInteger(rCpm, "zip"));
                    cm.setPhone(rCpm.getString("phone"));
                    cm.setEmail(rCpm.getString("email"));
                    cm.setFax(rCpm.getString("fax"));
                    cm.setSecAddress(rCpm.getString("sec_address"));
                    cm.setSecCity(rCpm.getString("sec_city"));
                    cm.setSecZip(DaoUtils.getInteger(rCpm, "sec_zip"));
                    cm.setSecPhone(rCpm.getString("sec_phone"));
                    cm.setContFirstName(rCpm.getString("cont_fname"));
                    cm.setContLastName(rCpm.getString("cont_lname"));
                    cm.setContEmail(rCpm.getString("cont_email"));
                    member.setCorporateMember(cm);
                }
                rCpm.close();
            }
       // }

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
            JoSigning signing = new JoSigning();
            signing.setSignDate(r2.getDate( "sign_date"));


            signing.setLocation(locationsMap.get(DaoUtils.getInteger(r2, "location")));
            signing.setUntilDate(r2.getDate( "until_date"));
            signing.setCost(r2.getDouble("cost"));
            signing.setReceipt(r2.getString("receipt_id"));
            signing.setLibrarian(r2.getString("librarian"));
            member.getSignings().add(signing);
        }
        r2.close();

        duplicatesPS.setInt(1, rset.getInt("sys_id"));
        if (!rset.wasNull()) {
            ResultSet rDu = duplicatesPS.executeQuery();
            List<JoDuplicate> dups = new ArrayList<>();
            while (rDu.next()) {
                JoDuplicate d = new JoDuplicate();
                d.setDupDate(rDu.getDate( "dup_date"));
                d.setDupNo(rDu.getInt("dup_no"));
                dups.add(d);
            }
            member.setDuplicates(dups);
        }

        picturebooksPS.setInt(1, rset.getInt("sys_id"));
        if(!rset.wasNull()) {
            ResultSet rPb = picturebooksPS.executeQuery();
            List<JoPictureBook> pictureBooks = new ArrayList<>();
            while (rPb.next()) {
                JoPictureBook p = new JoPictureBook();
                p.setLendDate(rPb.getDate( "sdate"));
                p.setLendNo(DaoUtils.getInteger(rPb,"lend_no"));
                p.setReturnNo(DaoUtils.getInteger(rPb,"return_no"));
                p.setStatus(DaoUtils.getInteger(rPb,"state"));
                pictureBooks.add(p);
            }
            member.setPicturebooks(pictureBooks);
        }

        rset.close();
        stmt.close();

        signingPS.close();
        organizationPS.close();
        membershipTypePS.close();
        userCategoryPS.close();
        corporateMemberPS.close();
        duplicatesPS.close();
        picturebooksPS.close();
        languagePS.close();
        eduLvlPS.close();
        return member;
    }
}
