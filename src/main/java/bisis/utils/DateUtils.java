package bisis.utils;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;

public class DateUtils {

    public static Instant getInstant(ResultSet rs, String colName){
        try {
            Timestamp timestamp = rs.getTimestamp(colName);
            if (timestamp == null)
                return null;
            return timestamp.toInstant();
        }
        catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Tried parsing date 00-00-0000, parsed null ");
            return null;
        }
    }
}
