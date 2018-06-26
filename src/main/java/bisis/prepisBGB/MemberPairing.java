package bisis.prepisBGB;

import bisis.circ.*;
import bisis.export.ExportCoders;
import bisis.jongo_circ.JoLending;
import bisis.jongo_circ.JoMember;
import bisis.utils.DaoUtils;
import bisis.utils.DateUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MemberPairing {

    public static final String LIBRARY = "bgb";

    public static void main(String[] args) {

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/" + "bisis" + "?useSSL=false&serverTimezone=CET"
                    , "bisis", "bisis");
            DB db = new MongoClient().getDB("bisis");
            Jongo jongo = new Jongo(db);
            MongoCollection centralMembersCollection = jongo.getCollection("bgb_members");
            MongoCollection lendingsCollection = jongo.getCollection("bgb_lendings");

            PrintWriter foundMembers = new PrintWriter(new File(LIBRARY +"_upareni_clanovi.txt"));

            List<Integer> sysIdsLocalList = new ArrayList<>();
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT sys_id FROM users");
            while (rset.next())
                sysIdsLocalList.add(rset.getInt(1));

            int total = sysIdsLocalList.size();
            int cnt = 0;
            int found = 0;
            int nFound = 0;
            for (Integer sysId: sysIdsLocalList) {
                //izvuci iz lokalne(opstinske) clana bez organizacije
                cnt++;
                Member localMember = getMember(conn, sysId, LIBRARY);
                JoMember centralMember = centralMembersCollection.findOne("{'userId':#}", localMember.getUserId()).as(JoMember.class);


                System.out.println("Nasao ih: " + found + "\nNije nasao: " + nFound + "\nOd: " + total);
                if (centralMember != null && isSameMember(localMember, centralMember)) {
                    foundMembers.write(toStringCompareMembers(localMember, centralMember));
                    found++;
                    //prepisi zaduzenja
                    List<Lending> lendings = getLendings(conn,sysId + "");
                    List<JoLending> joLendings = lendings.stream().map(l -> new JoLending(l)).collect(Collectors.toList());
                    System.out.println("stani");
                }
                else {

                    String query = getPairingQuery(localMember);
                    if (!query.equals("{}"))
                        centralMember = getMemberFromCursor(centralMembersCollection.find(query).as(JoMember.class));

                    if (centralMember != null) {
                        foundMembers.write(toStringCompareMembers(localMember, centralMember));
                        found++;
                        continue;
                    }
                    else {
                        String queryJmbg = getJmbgQuery(localMember);
                        if (!queryJmbg.equals("{}"))
                            centralMember = getMemberFromCursor(centralMembersCollection.find(queryJmbg).as(JoMember.class));
                        if (centralMember != null) {
                            foundMembers.write(toStringCompareMembers(localMember, centralMember));
                            found++;
                            continue;
                        }

                    }

                    nFound++;
                }
            }
            foundMembers.close();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static JoMember getMemberFromCursor(MongoCursor<JoMember> cursor) {
        if (cursor.count() == 1)
            return cursor.next();
        else  if (cursor.count() == 0 ) {
            return null;
        } else {
            return null;
        }
    }

    public static String toStringCompareMembers(Member m, JoMember jm) {
        StringBuffer sb = new StringBuffer();
        sb.append("\n---------------------");
        sb.append("Podaci(br. korisnika, ime, prezime, srednje ime, adresa):\n");
        sb.append(m.getUserId() + " | " + jm.getUserId() + "\n");
        sb.append(m.getFirstName() + " | " + jm.getFirstName() + "\n");
        sb.append(m.getLastName() + " | " + jm.getLastName() + "\n");
        sb.append(m.getParentName() + " | " + jm.getParentName() + "\n");
        sb.append(m.getAddress() + " | " + jm.getAddress() + "\n");

        return sb.toString();
    }

    private static String getJmbgQuery(Member m) {
        if (m.getJmbg()!= null && !m.getJmbg().equals("") && !m.getJmbg().equals("0000000000000"))
            return "{'jmbg': '" + m.getJmbg() + "'}";
        else
            return "{}";
    }

    private static String getPairingQuery(Member m) {

        String retVal = "{";

        if (m.getFirstName() != null && !m.getFirstName().equals(""))
            retVal += "'firstName':'" + m.getFirstName() + "',";
        if (m.getLastName() != null && !m.getLastName().equals(""))
            retVal += "'lastName':'" + m.getLastName() + "',";
        if (m.getParentName() != null && !m.getParentName().equals(""))
            retVal += "'parentName':'" + m.getParentName() + "',";

        if(!retVal.equals("{"))
            retVal.substring(0,retVal.length() - 1);

        retVal += "}";
        return retVal;
    }

    private static boolean isSameMember(Member m, JoMember m2) {

        if (m.getJmbg()!= null && !m.getJmbg().equals("") && !m.getJmbg().equals("0000000000000") && m.getJmbg().equals(m2.getJmbg()))
            return true;
        else
            return (m.getFirstName().equals(m2.getFirstName()) && m.getLastName().equals(m2.getLastName()) /*&& m.getParentName().equals(m2.getParentName())*/);
    }

    private static Member getMember(Connection conn, Integer sys_id, String library) throws SQLException {
        Statement stmt = conn.createStatement();
        //PreparedStatement lendingPS = conn.prepareStatement("SELECT id, ctlg_no, lend_date, location, return_date, resume_date, deadline, librarian_lend, librarian_return, librarian_resume FROM lending WHERE sys_id=?");
        PreparedStatement signingPS = conn.prepareStatement("SELECT id, sign_date, location, until_date, cost, receipt_id, librarian FROM signing WHERE sys_id=?");
        PreparedStatement organizationPS = conn.prepareStatement("SELECT id, address, name, city, zip from organization where id=?");
        PreparedStatement membershipTypePS = conn.prepareStatement("SELECT name, period from mmbr_types where id=?");
        PreparedStatement userCategoryPS = conn.prepareStatement("SELECT * from user_categs where id=?");
        PreparedStatement corporateMemberPS = conn.prepareStatement("SELECT * from groups where sys_id=?");
        PreparedStatement duplicatesPS = conn.prepareStatement("SELECT * from duplicate where id=?");
        PreparedStatement picturebooksPS = conn.prepareStatement("SELECT * from picturebooks where id=?");
        PreparedStatement languagePS = conn.prepareStatement("SELECT * from languages where id=?");
        PreparedStatement eduLvlPS = conn.prepareStatement("SELECT * from edu_lvl where id=?");

//        String organizationsMapJson = new Scanner(new File("export" + library.toUpperCase() + "/circ_coders_json_output/organization_id-id.json")).useDelimiter("\\Z").next();
//        Map<Integer, String> orgMap = mapper.readValue(organizationsMapJson, new TypeReference<Map<Integer, String>>(){});

        ResultSet rset = stmt.executeQuery("SELECT sys_id, organization, languages, edu_lvl, mmbr_type, user_categ, groups, user_id, first_name, last_name, parent_name, address, city, zip, phone, email, jmbg, doc_id, doc_no, doc_city, country, gender, age, sec_address, sec_zip," +
                " sec_city, sec_phone, note, interests, warning_ind, occupation, title, index_no, class_no, pass, block_reason FROM users where sys_id = " + sys_id);
            rset.next();
           Member member = new Member();

//            //Organization
//            organizationPS.setInt(1, rset.getInt("organization"));
//            if (!rset.wasNull()) {
//                ResultSet rOrg = organizationPS.executeQuery();
//                if (rOrg.next()) {
//
//                    Organization org = new Organization();
//                    org.set_id(orgMap.get(rOrg.getInt("id")));
//                    org.setName(rOrg.getString("name"));
//                    org.setAddress(rOrg.getString("address"));
//                    org.setCity(rOrg.getString("city"));
//                    org.setZip(rOrg.getString("zip"));
//                    member.setOrganization(org);
//                }
//                rOrg.close();
//            }

            //MembershipType
            membershipTypePS.setInt(1, rset.getInt("mmbr_type"));
            if(!rset.wasNull()) {
                ResultSet rMmbrt = membershipTypePS.executeQuery();
                MembershipType mmbrt = new MembershipType();
                if (rMmbrt.next()) {
                    mmbrt.setDescription(rMmbrt.getString("name"));
                    mmbrt.setPeriod(DaoUtils.getInteger(rMmbrt,"period"));
                    mmbrt.setLibrary(library);
                    member.setMembershipType(mmbrt);
                }
                rMmbrt.close();
            }

            //UserCategory
            userCategoryPS.setInt(1, rset.getInt("user_categ"));
            if(!rset.wasNull()) {
                ResultSet rUc = userCategoryPS.executeQuery();
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

    public static List<Lending> getLendings(Connection conn, String memberId) throws SQLException {

        List<Lending> retVal = new ArrayList<>();

        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery("SELECT * FROM lending WHERE sys_id = " + memberId);
        PreparedStatement userPS = conn.prepareStatement("SELECT user_id FROM users where sys_id = ?");
        PreparedStatement locPS = conn.prepareStatement("SELECT name FROM location where id = ?");
        PreparedStatement warningsPS = conn.prepareStatement("SELECT * FROM warnings WHERE lending_id = ?");

        ResultSet warningTypesRs = conn.createStatement().executeQuery("SELECT * from warning_types");
        Map<Integer, String> warningTypesMap = new HashMap<>();
        while(warningTypesRs.next()){
            warningTypesMap.put(DaoUtils.getInteger(warningTypesRs,"id"), warningTypesRs.getString("name"));
        }


        int lendingCount = 0;

        while(rset.next()) {

            Lending lending = new Lending();

            //warnings
            Integer leindgId = DaoUtils.getInteger(rset, "id");
            warningsPS.setInt(1, leindgId);
            ResultSet warningsResulsts = warningsPS.executeQuery();
            List<Warning> warningList = new ArrayList<>();
            while (warningsResulsts.next()){
                Warning w = new Warning();
                w.setWarningDate(DateUtils.getInstant(warningsResulsts, "wdate"));
                w.setWarningType(warningTypesMap.get(warningsResulsts.getInt("wtype")));
                w.setWarnNo(warningsResulsts.getString("warn_no"));
                w.setDeadline(DateUtils.getInstant(warningsResulsts, "deadline"));
                w.setNote(warningsResulsts.getString("note"));
                warningList.add(w);
            }
            lending.setWarnings(warningList);


            userPS.setInt(1, rset.getInt("sys_id"));
            ResultSet rUser = userPS.executeQuery();
            if(rUser.next())
                lending.setUserId(rUser.getString("user_id"));

            lending.setCtlgNo(rset.getString("ctlg_no"));
            lending.setLendDate(DateUtils.getInstant(rset, "lend_date"));
            lending.setResumeDate(DateUtils.getInstant(rset, "resume_date"));
            lending.setReturnDate(DateUtils.getInstant(rset, "return_date"));
            lending.setDeadline(DateUtils.getInstant(rset, "deadline"));
            lending.setLibrarianLend(rset.getString("librarian_lend"));
            lending.setLibrarianReturn(rset.getString("librarian_return"));
            lending.setLibrarianResume(rset.getString("librarian_resume"));


            locPS.setInt(1, rset.getInt("location"));
            if (!rset.wasNull()) {
                ResultSet locRs = locPS.executeQuery();
                if (locRs.next())
                    lending.setLocation(locRs.getString("name"));
            }
            retVal.add(lending);
        }
        warningsPS.close();
        userPS.close();
        stmt.close();

        return retVal;
    }

}
