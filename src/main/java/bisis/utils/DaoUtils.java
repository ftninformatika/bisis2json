package bisis.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Petar on 12/23/2017.
 */
public class DaoUtils {
    /**
     * covers the gap of rs.getInt() for returning 0 instead of null value
     */
    static public Integer getInteger(ResultSet rs, String strColName) throws SQLException {
        int nValue = rs.getInt(strColName);
        return rs.wasNull() ? null : nValue;
    }
}
