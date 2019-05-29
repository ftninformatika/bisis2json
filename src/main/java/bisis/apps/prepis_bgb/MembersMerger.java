package bisis.apps.prepis_bgb;

import bisis.model.circ.CircLocation;
import bisis.model.circ.Lending;
import bisis.model.jongo_circ.JoLending;
import bisis.model.jongo_circ.JoMember;
import bisis.model.jongo_circ.JoSigning;
import bisis.utils.LatCyrUtils;
import bisis.utils.ProgressBar;
import com.mongodb.DB;
import com.mongodb.DuplicateKeyException;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.Date;
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

    private String getNextUserId(String userId, MongoCollection membersCollection) {
        if (userId == null || userId.length() != 11)
            return null;
        String loc = userId.substring(0,3);
        MongoCursor<JoMember> jm  = membersCollection.find("{userId: {$regex: #}}", "^"+loc+".*$").projection("{userId:1}").sort("{userId:-1}").as(JoMember.class);
        if (jm.hasNext()) {
            JoMember tmpJm = jm.next();
            if (tmpJm.getUserId() == null || tmpJm.getUserId().length() != 11)
                return null;
            String lastUserId = tmpJm.getUserId();
            try {
                String next = String.valueOf(Integer.parseInt(lastUserId.substring(3)) + 1);
                int zerosBetween = 8 - next.length();
                next = loc + String.join("", Collections.nCopies(zerosBetween, "0")) + next;
                if (next.length() != 11) return  null;

                return next;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }


    public void mergeWinIsis2Bisis(DB mongoDatabase, List<JoMember> winMembers, List<JoLending> winLendings, boolean mergeMembersMode, boolean printMergedMode) throws FileNotFoundException {
        Jongo jongo = new Jongo(mongoDatabase);
        MongoCollection centralMembersCollection = jongo.getCollection("gbns_members");
        MongoCollection lendingsCentralCollection = jongo.getCollection("gbns_lendings");
        MongoCollection cmCentralCollection = jongo.getCollection("gbns_corporate_member");
        PrintWriter foundMembersWriter = new PrintWriter(new File("gbns" + MERGED_MEMBERS_FILE_NAME_CHUNK));
        PrintWriter mmWriter = new PrintWriter(new File("gbns_members_changedId.txt"));

        int cnt = 0;
        int found = 0;
        System.out.println("Migrating members starting...");
        if (mergeMembersMode)
            System.out.println("Merge members mode ON.");
        if (printMergedMode)
            System.out.println("Printing merged members ON.");
        ProgressBar progressBar = new ProgressBar();
        for (JoMember winMember: winMembers) {
            cnt++;
            JoMember centralMember = centralMembersCollection.findOne("{'userId':#}", winMember.getUserId()).as(JoMember.class);
            if (centralMember != null) {
                if (isSameMember(winMember, centralMember) && mergeMembersMode) {
                    if (winMember.getPicturebooks() == null) winMember.setPicturebooks(new ArrayList<>());
                    if (winMember.getSignings() == null) winMember.setSignings(new ArrayList<>());
                    if (winMember.getDuplicates() == null) winMember.setDuplicates(new ArrayList<>());
                    if (centralMember.getPicturebooks() == null) centralMember.setPicturebooks(new ArrayList<>());
                    if (centralMember.getSignings() == null) centralMember.setSignings(new ArrayList<>());
                    if (centralMember.getDuplicates() == null) centralMember.setDuplicates(new ArrayList<>());
                    if (centralMember.getJmbg() != null && winMember.getJmbg() != null) centralMember.setJmbg(winMember.getJmbg());

                    JoMember[] mems = getPrimaryMember(centralMember, winMember);
                    JoMember primary = mems[0];
                    JoMember sec = mems[1];

                    primary.getSignings().addAll(sec.getSignings());
                    primary.getDuplicates().addAll(sec.getDuplicates());
                    primary.getPicturebooks().addAll(sec.getPicturebooks());
                    List<JoLending> joLendingList = winLendings.stream().filter(l -> l.getUserId().equals(sec.getUserId())).collect(Collectors.toList());
                    joLendingList.stream().forEach(l -> lendingsCentralCollection.save(l));
                    centralMembersCollection.save(primary);
                    found++;
                    if (printMergedMode)
                        foundMembersWriter.write(toStringCompareMembers(sec, primary));
                }
                else {
                    // Generate new userId and copy it to central
//                    String userId = computeUserId(locationId);
                    String userId = getNextUserId(winMember.getUserId(), centralMembersCollection);
                    if (userId == null) {
                        System.err.println("Error generating new userId for: " + winMember.getUserId());
                        continue;
                    }
                    List<JoLending> joLendingList = winLendings.stream().filter(l -> l.getUserId().equals(winMember.getUserId())).collect(Collectors.toList());
                    joLendingList.stream().forEach(l -> l.setUserId(userId));
                    winMember.setOldNumbers(winMember.getUserId());
                    winMember.setUserId(userId);
                    try {
                        centralMembersCollection.save(winMember);
                    } catch (DuplicateKeyException e) {
                        System.err.println("Duplicate key for member: " + winMember.getUserId());
                    }
                    joLendingList.forEach(l -> {
                        try {
                            lendingsCentralCollection.save(l);
                        } catch (DuplicateKeyException e) {
                            System.err.println("Duplicate key for lending: " + l.getCtlgNo());
                        }
                    });
                    mmWriter.write(toStringCompareMembers(winMember, centralMember));
                    USER_ID_CNT++;
                }
            }
            else {
                List<JoLending> joLendingList = winLendings.stream().filter(l -> l.getUserId().equals(winMember.getUserId())).collect(Collectors.toList());
                centralMembersCollection.save(winMember);
                joLendingList.stream().forEach(l -> lendingsCentralCollection.save(l));
            }
            if (cnt % 100 == 0)
                progressBar.update(cnt, winMembers.size());
        }
        progressBar.update(winMembers.size(), winMembers.size());
        foundMembersWriter.close();
        mmWriter.close();
        System.out.println("\nFinished migrating members from WinIsis export to central library.");
        System.out.println("Merged: " + found + " members.");
    }

    private static boolean isSameMember(JoMember m, JoMember m2) {
        if (m.getJmbg() != null && m2.getJmbg() != null && m.getJmbg().equals(m2.getJmbg()))
            return true;
        if (m.getFirstName() == null || m.getLastName() == null
            || m2.getFirstName() == null || m2.getLastName() == null)
            return false;
        if (LatCyrUtils.toLatinUnaccented(m.getFirstName().toLowerCase()).trim().equals(LatCyrUtils.toLatinUnaccented(m2.getFirstName().toLowerCase()).trim())
                && LatCyrUtils.toLatinUnaccented(m.getLastName().toLowerCase()).trim().equals(LatCyrUtils.toLatinUnaccented(m2.getLastName().toLowerCase()).trim()))
            return true;
        return false;
    }

    private static List<JoLending> transformLendingsUserId(List<Lending> lendings, String newUserId ) {
        List<JoLending> retVal = lendings.stream().map(l -> new JoLending(l)).collect(Collectors.toList());
        retVal.forEach(l -> l.setUserId(newUserId));
        return retVal;
    }

    private static JoMember[] getPrimaryMember(JoMember m1, JoMember m2) {
        if (m1 == null || m2 == null)
            return null;
        JoMember[] retVal = {m1, m2};
        if (m1.getSignings() == null || m1.getSignings().size() == 0) {
            retVal[0] = m2;
            retVal[1] = m1;
            return retVal;
        }
        Date maxSign1 = Collections.max(m1.getSignings(), Comparator.comparing(JoSigning::getSignDate)).getSignDate();
        if (m2.getSignings() == null || m2.getSignings().size() == 0) {
            retVal[0] = m1;
            retVal[1] = m2;
            return retVal;
        }
        Date maxSign2 = Collections.max(m2.getSignings(), Comparator.comparing(JoSigning::getSignDate)).getSignDate();
        if (maxSign1 == null) {
            retVal[0] = m2;
            retVal[1] = m1;
            return retVal;
        }
        if (maxSign2 == null) {
            retVal[0] = m1;
            retVal[1] = m2;
            return retVal;
        }
        if (maxSign1.before(maxSign2)) {
            retVal[0] = m2;
            retVal[1] = m1;
        }
        else if (maxSign2.before(maxSign1)) {
            retVal[0] = m1;
            retVal[1] = m2;
        }
        else {
            retVal[0] = m2;
            retVal[1] = m1;
        };
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
