package bisis.prepisBGB;

import bisis.circ.Lending;
import bisis.circ.Warning;
import bisis.jongo_circ.JoLending;
import bisis.jongo_circ.JoMember;
import bisis.utils.DaoUtils;
import bisis.utils.DateUtils;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MemberPairing2 {

    public static int USER_ID_CNT = 1;

    public static void main(String[] args) {


        try {

            if(args.length != 4) {
                System.out.println("Enter mysqldb name{1} location id{2} mongodb name{3} mongodb port{4} as arguments!");
                System.exit(0);
            }
            String locationId = "9" + args[1];

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + args[0] + "?useSSL=false&serverTimezone=CET"
                    , "bisis", "bisis");

            MongoClient mongoClient = new MongoClient(args[2], Integer.parseInt(args[3]));
            DB db = mongoClient.getDB("bisis");
            Jongo jongo = new Jongo(db);
            MongoCollection centralMembersCollection = jongo.getCollection("bgb_members");
            MongoCollection lendingsCollection = jongo.getCollection("bgb_lendings");

            List<Integer> sysIdsLocalList = new ArrayList<>();
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT sys_id FROM users");
            while (rset.next())
                sysIdsLocalList.add(rset.getInt(1));

            System.out.println(sysIdsLocalList.size());

            MemberStorage memberStorage = new MemberStorage("bgb", conn);
            int cnt = 0;
            for (Integer sysId: sysIdsLocalList) {
                JoMember localMember = memberStorage.getJo(sysId);
                JoMember centralMember = centralMembersCollection.findOne("{'userId':#}", localMember.getUserId()).as(JoMember.class);

                List<Lending> lendingList = getLendings( conn, String.valueOf(sysId));

                // ako ga nema, prepisi ga
                if (centralMember == null) {
                    centralMembersCollection.save(localMember);
                    List<JoLending> joLendingList = lendingList.stream().map(l -> new JoLending(l)).collect(Collectors.toList());
                    lendingsCollection.save(joLendingList);
                }
                // ako ga ima dodeli mu userId i upisi stari broj pa ga prepisi
                else {
                    String userId = computeUserId(locationId);
                    List<JoLending> joLendingList = transformLendingsUserId(lendingList, userId);
                    localMember.setOldNumbers(localMember.getUserId());
                    localMember.setUserId(userId);
                    centralMembersCollection.save(localMember);
                    lendingsCollection.save(joLendingList);
                    USER_ID_CNT++;
                }
                cnt++;
                if (cnt % 100 == 0)
                    System.out.println("Copied members: " + cnt);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

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


    public static List<Lending> getLendings(Connection conn, String sysId) throws SQLException {

        List<Lending> retVal = new ArrayList<>();

        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery("SELECT * FROM lending WHERE sys_id = " + sysId);
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