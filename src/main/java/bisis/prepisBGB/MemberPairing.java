package bisis.prepisBGB;

import bisis.circ.Lending;
import bisis.circ.Member;
import bisis.circ.Warning;
import bisis.jongo_circ.JoLending;
import bisis.jongo_circ.JoMember;
import bisis.utils.DaoUtils;
import bisis.utils.DateUtils;
import bisis.utils.LatCyrUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.apache.commons.cli.Options;
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
        Options options = new Options();
        initOptions(options);


        try {

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/" + "bisis" + "?useSSL=false&serverTimezone=CET"
                    , "bisis", "bisis");
            DB db = new MongoClient().getDB("bisis");
            Jongo jongo = new Jongo(db);
            MongoCollection centralMembersCollection = jongo.getCollection("bgb_members");
            MongoCollection lendingsCollection = jongo.getCollection("bgb_lendings");

            PrintWriter foundMembers = new PrintWriter(new File(LIBRARY +"_upareni_clanovi.txt"));
            PrintWriter sameUserIDPW = new PrintWriter(new File(LIBRARY +"_same_userID.txt"));

            List<Integer> sysIdsLocalList = new ArrayList<>();
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT sys_id FROM users");
            while (rset.next())
                sysIdsLocalList.add(rset.getInt(1));

            int total = sysIdsLocalList.size();
            int cnt = 0;
            int found = 0;
            int nFound = 0;
            MemberStorage memberStorage = new MemberStorage(LIBRARY, conn);

            for (Integer sysId: sysIdsLocalList) {
                //izvuci iz lokalne(opstinske) clana bez organizacije
                cnt++;
                Member localMember = memberStorage.get(sysId);
                JoMember centralMember = centralMembersCollection.findOne("{'userId':#}", localMember.getUserId()).as(JoMember.class);

                System.out.println("Nasao ih: " + found + "\nNije nasao: " + nFound + "\nOd: " + total);
                if (centralMember != null && isSameMember(localMember, centralMember)) {
                    foundMembers.write(toStringCompareMembers(localMember, centralMember));
                    found++;
                    //prepisi zaduzenja

                }
                else if (centralMember != null &&  !isSameMember(localMember, centralMember)) {
                    sameUserIDPW.write(toStringCompareMembers(localMember, centralMember));
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

    public static void copyLendings(Connection conn, String sysId, MongoCollection memCollection) {
        List<Lending> lendings = null;
        try {
            lendings = getLendings(conn,sysId + "");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        List<JoLending> joLendings = lendings.stream().map(l -> new JoLending(l)).collect(Collectors.toList());
        memCollection.save(joLendings);

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
            return (LatCyrUtils.toLatinUnaccented(m.getFirstName()).toLowerCase().equals(LatCyrUtils.toLatinUnaccented(m2.getFirstName()).toLowerCase())
                    &&
                    LatCyrUtils.toLatinUnaccented(m.getLastName()).toLowerCase().equals(LatCyrUtils.toLatinUnaccented(m2.getLastName()).toLowerCase()));
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

    private static void initOptions(Options options){
        options.addOption("a", "mysqladress", true, "MySQL server address (default: localhost)");
        options.addOption("p", "mysqlport", true,"MySQL server port (default: 3306)");
        options.addOption("n","mysqdblname", true, "MySQL database name (default: bisis)");
        options.addOption("u","mysqlusername", true, "MySQL server username (default: bisis)");
        options.addOption("w","mysqlpassword", true, "MySQL server password (default: bisis)");

    }

}
