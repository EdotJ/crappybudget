package com.budgeteer.api.core;

import com.budgeteer.api.base.DatabaseCleanupExtension;
import io.micronaut.context.annotation.Value;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
@ExtendWith(DatabaseCleanupExtension.class)
@Tag("Integration")
public class GoalValueTriggerTest {

    @Value("${datasources.default.url}")
    private String url;

    @Value("${datasources.default.username}")
    private String username;

    @Value("${datasources.default.password}")
    private String password;

    @Test
    public void testTrigger() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection(url, username, password);
        Statement stat = conn.createStatement();
        stat.execute("INSERT INTO `users` VALUES(1, 'testuser@mail.com', 'testuser', 'testpassword', true)");
        stat.execute("INSERT INTO `categories` VALUES(1, 'testCategory', NULL, 1, 0)");
        stat.execute("INSERT INTO `categories` VALUES(2, 'secondTestCategory', NULL, 1, 0)");
        stat.execute("INSERT INTO `accounts` VALUES(1, 'Bank', 1)");
        stat.execute("INSERT INTO `goal_types` VALUES(1, 'SAVE')");
        stat.execute("INSERT INTO `goal_types` VALUES(2, 'SPEND')");
        stat.execute("INSERT INTO `goals` VALUES(1, 'saveGoal', '', '2030-10-10', 0, 600, 1, NULL, 1, 1)");
        stat.execute("INSERT INTO `goals` VALUES(2, 'spendGoal', '', '2030-10-10', 0, 600, 1, NULL, 2, 1)");
        stat.execute("INSERT INTO `entries` VALUES(1, 'add money', '', '2020-10-11', 30, false, 1, 1, 1, CURRENT_TIMESTAMP())");
        ResultSet rs;
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=1");
        rs.next();
        assertEquals(new BigDecimal("30.00"), rs.getBigDecimal(1));
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=2");
        rs.next();
        assertEquals(new BigDecimal("-30.00"), rs.getBigDecimal(1));
        stat.execute("INSERT INTO `entries` VALUES(2, 'get milk', '', '2020-10-11', 15, true, 1, 1, 1, CURRENT_TIMESTAMP())");
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=1");
        rs.next();
        assertEquals(new BigDecimal("15.00"), rs.getBigDecimal(1));
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=2");
        rs.next();
        assertEquals(new BigDecimal("-15.00"), rs.getBigDecimal(1));
        stat.execute("UPDATE `entries` SET `value`=11.0 WHERE `id`=1");
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=1");
        rs.next();
        assertEquals(new BigDecimal("-4.00"), rs.getBigDecimal(1));
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=2");
        rs.next();
        assertEquals(new BigDecimal("4.00"), rs.getBigDecimal(1));
        stat.execute("UPDATE `entries` SET `category_id`=2 WHERE `id`=1");
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=1");
        rs.next();
        assertEquals(new BigDecimal("-15.00"), rs.getBigDecimal(1));
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=2");
        rs.next();
        assertEquals(new BigDecimal("15.00"), rs.getBigDecimal(1));
        stat.execute("UPDATE `entries` SET `category_id`=1 WHERE `id`=1");
        stat.execute("UPDATE `entries` SET `is_expense`=true WHERE `id`=1");
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=1");
        rs.next();
        assertEquals(new BigDecimal("-26.00"), rs.getBigDecimal(1));
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=2");
        rs.next();
        assertEquals(new BigDecimal("26.00"), rs.getBigDecimal(1));
        stat.execute("DELETE FROM `entries` WHERE `id`=2");
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=1");
        rs.next();
        assertEquals(new BigDecimal("-11.00"), rs.getBigDecimal(1));
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=2");
        rs.next();
        assertEquals(new BigDecimal("11.00"), rs.getBigDecimal(1));
        stat.execute("INSERT INTO `entries` VALUES(3, 'a new entry', '', '2020-10-11', 80, true, 1, 1, 1, CURRENT_TIMESTAMP())");
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=1");
        rs.next();
        assertEquals(new BigDecimal("-91.00"), rs.getBigDecimal(1));
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=2");
        rs.next();
        assertEquals(new BigDecimal("91.00"), rs.getBigDecimal(1));
        stat.execute("UPDATE `entries` SET `is_expense` = false, `value` = 50 WHERE `id`=3");
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=1");
        rs.next();
        assertEquals(new BigDecimal("39.00"), rs.getBigDecimal(1));
        rs = stat.executeQuery("SELECT `value` FROM `goals` WHERE `id`=2");
        rs.next();
        assertEquals(new BigDecimal("-39.00"), rs.getBigDecimal(1));
        rs.close();
        stat.close();
        conn.close();
    }
}
