package com.budgeteer.api.core;

import org.h2.api.Trigger;
import org.h2.tools.SimpleResultSet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GoalValueTrigger implements Trigger {

    private static final String SAVE_GOAL_TYPE = "SAVE";
    private static final String SPEND_GOAL_TYPE = "SPEND";
    private static final int GOAL_ID = 1;
    private static final int USER_ID = 6;
    private static final int CATEGORY_ID = 7;
    private static final int GOAL_TYPE = 4;
    private static final int GOAL_VALUE = 2;
    private static final int ENTRY_VALUE = 4;
    private static final int IS_EXPENSE = 5;

    private Connection conn;

    @Override
    public void init(Connection conn,
                     String schemaName,
                     String triggerName,
                     String tableName,
                     boolean before,
                     int type) {
        this.conn = conn;
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        if (oldRow != null && newRow != null) {
            handleUpdate(oldRow, newRow);
        } else if (oldRow == null && newRow != null) {
            handleInsert(newRow);
        } else if (oldRow != null) {
            handleDelete(oldRow);
        }
    }

    private void handleUpdate(Object[] oldRow, Object[] newRow) throws SQLException {
        Long oldCategoryId = (Long) oldRow[CATEGORY_ID];
        Long newCategoryId = (Long) newRow[CATEGORY_ID];
        if (oldCategoryId != null && oldCategoryId.equals(newCategoryId)) {
            ResultSet rs = getGoals(newRow);
            while (rs.next()) {
                String goalType = rs.getString(GOAL_TYPE);
                BigDecimal goalValue = rs.getBigDecimal(GOAL_VALUE);
                Long goalId = rs.getLong(GOAL_ID);
                BigDecimal oldVal = getValue(oldRow);
                BigDecimal newVal = getValue(newRow);
                if (SAVE_GOAL_TYPE.equals(goalType)) {
                    goalValue = goalValue.subtract(oldVal).add(newVal);
                } else if (SPEND_GOAL_TYPE.equals(goalType)) {
                    goalValue = goalValue.add(oldVal).subtract(newVal);
                }
                updateGoal(goalValue, goalId);
            }
        } else {
            ResultSet newGoals = getGoals(newRow);
            ResultSet oldGoals = getGoals(oldRow);
            while (oldGoals.next()) {
                String goalType = oldGoals.getString(GOAL_TYPE);
                BigDecimal goalValue = oldGoals.getBigDecimal(GOAL_VALUE);
                Long goalId = oldGoals.getLong(GOAL_ID);
                BigDecimal oldVal = getValue(oldRow);
                if (SAVE_GOAL_TYPE.equals(goalType)) {
                    goalValue = goalValue.subtract(oldVal);
                } else if (SPEND_GOAL_TYPE.equals(goalType)) {
                    goalValue = goalValue.add(oldVal);
                }
                updateGoal(goalValue, goalId);
            }
            while (newGoals.next()) {
                String goalType = newGoals.getString(GOAL_TYPE);
                BigDecimal goalValue = newGoals.getBigDecimal(GOAL_VALUE);
                Long goalId = newGoals.getLong(GOAL_ID);
                BigDecimal newVal = getValue(newRow);
                if (SAVE_GOAL_TYPE.equals(goalType)) {
                    goalValue = goalValue.add(newVal);
                } else if (SPEND_GOAL_TYPE.equals(goalType)) {
                    goalValue = goalValue.subtract(newVal);
                }
                updateGoal(goalValue, goalId);
            }
        }
    }

    private void handleInsert(Object[] newRow) throws SQLException {
        ResultSet rs = getGoals(newRow);
        while (rs.next()) {
            String goalType = rs.getString(GOAL_TYPE);
            BigDecimal goalValue = rs.getBigDecimal(GOAL_VALUE);
            Long goalId = rs.getLong(GOAL_ID);

            if (SAVE_GOAL_TYPE.equals(goalType)) {
                goalValue = goalValue.add(getValue(newRow));
            } else if (SPEND_GOAL_TYPE.equals(goalType)) {
                goalValue = goalValue.subtract(getValue(newRow));
            }

            updateGoal(goalValue, goalId);
        }
    }

    private void updateGoal(BigDecimal goalValue, Long goalId) throws SQLException {
        PreparedStatement preparedStatement;
        preparedStatement = conn.prepareStatement(
                "UPDATE `goals` SET `value` = ? WHERE `id` = ?");
        preparedStatement.setBigDecimal(1, goalValue);
        preparedStatement.setLong(2, goalId);
        preparedStatement.execute();
    }

    private ResultSet getGoals(Object[] newRow) throws SQLException {
        Long categoryId = (Long) newRow[CATEGORY_ID];
        if (categoryId == null) {
            return new SimpleResultSet();
        }
        Long userId = (Long) newRow[USER_ID];
        PreparedStatement preparedStatement = conn.prepareStatement(
                "SELECT g.id, g.value, gt.id, gt.name FROM `goals` g LEFT JOIN goal_types gt ON gt.id = g.goal_type "
                        + "WHERE `user_id` = ? AND `category_id` = ?"
        );
        preparedStatement.setLong(1, userId);
        preparedStatement.setLong(2, categoryId);
        return preparedStatement.executeQuery();
    }

    private BigDecimal getValue(Object[] row) {
        BigDecimal value = (BigDecimal) row[ENTRY_VALUE];
        boolean isExpense = (Boolean) row[IS_EXPENSE];
        if (isExpense) {
            value = value.negate();
        }
        return value;
    }

    private void handleDelete(Object[] oldRow) throws SQLException {
        ResultSet rs = getGoals(oldRow);
        while (rs.next()) {
            String goalType = rs.getString(GOAL_TYPE);
            BigDecimal goalValue = rs.getBigDecimal(GOAL_VALUE);
            Long goalId = rs.getLong(GOAL_ID);

            if (SAVE_GOAL_TYPE.equals(goalType)) {
                goalValue = goalValue.subtract(getValue(oldRow));
            } else if (SPEND_GOAL_TYPE.equals(goalType)) {
                goalValue = goalValue.add(getValue(oldRow));
            }

            updateGoal(goalValue, goalId);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void remove() {

    }
}
