package bisis.prepis_bgb;

import bisis.circ.CircLocation;
import bisis.circ.Lending;
import bisis.jongo_circ.JoLending;
import bisis.jongo_circ.JoMember;
import bisis.utils.ProgressBar;
import com.mongodb.DB;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MembersMerger {

    public static final String MERGED_MEMBERS_FILE_NAME_CHUNK = "_merged_members.txt";
    public static int USER_ID_CNT = 1;

    public void fixCircLocationsMySQL() {
        try {
            fixCircLocationsMySQL(MembersPairingBGB.mongoDatabase, MembersPairingBGB.mysqlConn);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void fixCircLocationsMySQL(DB mongoDatabase, Connection mysqlConn) throws Exception {
        Jongo jongo = new Jongo(mongoDatabase);
        Map<Integer, String> circLocationsCentralMap = new HashMap<>();
        MongoCollection circLocationCollection = jongo.getCollection("coders.circ_location");
        MongoCursor<CircLocation> circLocationsMCursor = circLocationCollection.find("{'library':'bgb'}").as(CircLocation.class);

        System.out.println("Fixing circ location in MySQL.");
        while (circLocationsMCursor.hasNext()) {
            CircLocation cl = circLocationsMCursor.next();
            if (circLocationsCentralMap.get(Integer.parseInt(cl.getLocationCode())) != null) {
                //throw new Exception("Error in fixing circ locations! For code:" + cl.getLocationCode());
                System.out.println("Error in fixing circ locations! For code:" + cl.getLocationCode());
                continue;
            }

            circLocationsCentralMap.put(Integer.parseInt(cl.getLocationCode()), cl.getDescription());
        }

        Statement stmt = mysqlConn.createStatement();
        Statement stmtSet = mysqlConn.createStatement();
        ResultSet rsetCircLocation = stmt.executeQuery("SELECT id FROM location");
        while (rsetCircLocation.next()) {
            Integer id = rsetCircLocation.getInt("id");
            String newDescription = circLocationsCentralMap.get(id);
            if (newDescription == null) {
//                throw new Exception("Id for circ location:" + rsetCircLocation.getInt("id") + " doesn't exist in central database.");
                System.out.println("Id for circ location:" + rsetCircLocation.getInt("id") + " doesn't exist in central database.");
            }
            else {
                stmtSet.execute("UPDATE location SET name = '" + newDescription + "' WHERE id = " + id);
            }
        }
    }

    public void merge(boolean mergeMembersMode, boolean printMergedMode) {
        try {
            merge(MembersPairingBGB.mongoDatabase, MembersPairingBGB.mysqlConn, MembersPairingBGB.mysqlDbName, MembersPairingBGB.branchPrefix, mergeMembersMode, printMergedMode);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void merge(DB mongoDatabase, Connection mysqlConn, String mysqlDbName, String branchPrefix, boolean mergeMembersMode, boolean printMergedMode) throws SQLException, FileNotFoundException {

        String locationId = "9" + branchPrefix;
        Jongo jongo = new Jongo(mongoDatabase);
        MongoCollection centralMembersCollection = jongo.getCollection("bgb_members");
        MongoCollection lendingsCentralCollection = jongo.getCollection("bgb_lendings");
        MongoCollection cmCentralCollection = jongo.getCollection("bgb_corporate_member");
        PrintWriter foundMembersWriter = new PrintWriter(new File(mysqlDbName + MERGED_MEMBERS_FILE_NAME_CHUNK));

        List<Integer> sysIdsLocalList = new ArrayList<>();
        Statement stmt = mysqlConn.createStatement();
        ResultSet rset = stmt.executeQuery("SELECT sys_id FROM users");
        while (rset.next())
            sysIdsLocalList.add(rset.getInt(1));

        MemberStorage memberStorage = new MemberStorage("bgb", mysqlConn, cmCentralCollection);

        int cnt = 0;
        int found = 0;
        System.out.println("Migrating members starting...");
        if (mergeMembersMode)
            System.out.println("Merge members mode ON.");
        if (printMergedMode)
            System.out.println("Printing merged members ON.");
        ProgressBar progressBar = new ProgressBar();
        // TODO - append for every library, defalut mapping to sublocation
        String circLoc = MemberCodersPairingMap.memberCircMap.get(branchPrefix);
        for (Integer sys_id: sysIdsLocalList) {
            cnt++;
            JoMember localMember = memberStorage.getJo(sys_id);
            JoMember centralMember = centralMembersCollection.findOne("{'userId':#}", localMember.getUserId()).as(JoMember.class);

            // If exists, check if it's same member - merge it, if not then copy it with new userId and all lendings
            if (centralMember != null) {
                if (isSameMember(localMember, centralMember) && mergeMembersMode) {
                    JoMember mainMember;
                    JoMember secMember;
                    if(circLoc.equals(localMember.getUserId().substring(0, 4))) {
                        mainMember = localMember;
                        secMember = centralMember;
                    }
                    else {
                        mainMember = centralMember;
                        secMember = localMember;
                    }

                    // Copy lendings, signings and warnings to central member and save it
                    mainMember.getSignings().addAll(secMember.getSignings());
                    mainMember.getDuplicates().addAll(secMember.getDuplicates());
                    mainMember.getPicturebooks().addAll(secMember.getPicturebooks());
                    List<Lending> lendingList = memberStorage.getLendings( mysqlConn, String.valueOf(sys_id));
                    List<JoLending> joLendingList = lendingList.stream().map(l -> new JoLending(l)).collect(Collectors.toList());
                    joLendingList.stream().forEach(l -> lendingsCentralCollection.save(l));
                    centralMembersCollection.save(mainMember);
                    found++;
                    if (printMergedMode)
                        foundMembersWriter.write(toStringCompareMembers(localMember, centralMember));
                }
                else {
                    // Generate new userId and copy it to central
                    String userId = computeUserId(locationId);
                    List<Lending> lendingList = memberStorage.getLendings( mysqlConn, String.valueOf(sys_id));
                    List<JoLending> joLendingList = transformLendingsUserId(lendingList, userId);
                    localMember.setOldNumbers(localMember.getUserId());
                    localMember.setUserId(userId);
                    centralMembersCollection.save(localMember);
                    joLendingList.stream().forEach(l -> lendingsCentralCollection.save(l));
                    USER_ID_CNT++;
                }
            }
            // Else copy it
            else {
                List<Lending> lendingList = memberStorage.getLendings( mysqlConn, String.valueOf(sys_id));
                centralMembersCollection.save(localMember);
                List<JoLending> joLendingList = lendingList.stream().map(l -> new JoLending(l)).collect(Collectors.toList());
                joLendingList.stream().forEach(l -> lendingsCentralCollection.save(l));
            }
            if (cnt % 100 == 0)
                progressBar.update(cnt, sysIdsLocalList.size());
        }
        progressBar.update(sysIdsLocalList.size(), sysIdsLocalList.size());
        foundMembersWriter.close();
        System.out.println("\nFinished migrating members from " + branchPrefix + " to central library.");
        System.out.println("Merged: " + found + " members.");

    }


    private static boolean isSameMember(JoMember m, JoMember m2) {
        if (m.getFirstName().toLowerCase().equals(m2.getFirstName().toLowerCase())
                && m.getLastName().toLowerCase().equals(m2.getLastName().toLowerCase())
                && m.getParentName().toLowerCase().equals(m2.getParentName().toLowerCase()))
            return true;
        return false;
    }

    private static List<JoLending> transformLendingsUserId(List<Lending> lendings, String newUserId ) {
        List<JoLending> retVal = lendings.stream().map(l -> new JoLending(l)).collect(Collectors.toList());
        retVal.forEach(l -> l.setUserId(newUserId));
        return retVal;
    }

    private static String computeUserId(String prefix) {
        String retVal = prefix;
        int nums = String.valueOf(USER_ID_CNT).length();
        int zeros = 8 - nums;
        for (int i = 0; i < zeros; i++)
            retVal += "0";
        retVal += String.valueOf(USER_ID_CNT);
        return retVal;
    }

    public static String toStringCompareMembers(JoMember m, JoMember jm) {
        StringBuffer sb = new StringBuffer();
        sb.append("\n---------------------");
        sb.append("Podaci(br. korisnika, ime, prezime, srednje ime, adresa, jmbg, broj dokumenta):\n");
        sb.append(m.getUserId() + " | " + jm.getUserId() + "\n");
        sb.append(m.getFirstName() + " | " + jm.getFirstName() + "\n");
        sb.append(m.getLastName() + " | " + jm.getLastName() + "\n");
        sb.append(m.getParentName() + " | " + jm.getParentName() + "\n");
        sb.append(m.getAddress() + " | " + jm.getAddress() + "\n");
        sb.append(m.getJmbg() + " | " + jm.getJmbg() + "\n");
        sb.append(m.getDocNo() + " | " + jm.getDocNo() + "\n");

        return sb.toString();
    }
}
