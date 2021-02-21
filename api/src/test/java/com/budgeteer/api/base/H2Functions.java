package com.budgeteer.api.base;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class H2Functions {
    /**
     * H2 database function to truncate all tables.
     * Used to clean up after tests that manipulate the database.
     *
     * @param conn Connection to DB
     */
    public static void truncateAllTables(Connection conn) throws SQLException {
        conn.createStatement().executeUpdate("SET REFERENTIAL_INTEGRITY FALSE");
        ResultSet rs = conn.createStatement()
                .executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = SCHEMA()");
        while (rs.next()) {
            String tableName = rs.getString(1);
            conn.createStatement().executeUpdate("TRUNCATE TABLE \"" + tableName + "\" RESTART IDENTITY");
        }
        conn.createStatement().executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
