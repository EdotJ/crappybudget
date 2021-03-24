CREATE TABLE IF NOT EXISTS `goal_types`
(
    `id`   IDENTITY    NOT NULL PRIMARY KEY,
    `name` VARCHAR(20) NOT NULL
);

DROP TABLE IF EXISTS `goals`;

CREATE TABLE IF NOT EXISTS `goals`
(
    `id`          IDENTITY    NOT NULL PRIMARY KEY,
    `name`        VARCHAR(60) NOT NULL,
    `description` VARCHAR(140),
    `date`        DATE        NOT NULL,
    `value`       DECIMAL(20,2)     NOT NULL DEFAULT 0,
    `goal_value`  DECIMAL(20,2)     NOT NULL,
    `user_id`     BIGINT      NOT NULL,
    `account_id`  BIGINT,
    `goal_type`   BIGINT      NOT NULL,
    `category_id` BIGINT,
    CONSTRAINT `fk_goal_user` FOREIGN KEY (`user_id`) REFERENCES users (`id`),
    CONSTRAINT `fk_goal_account` FOREIGN KEY (`account_id`) REFERENCES accounts (`id`),
    CONSTRAINT `fk_goal_type` FOREIGN KEY (`goal_type`) REFERENCES goal_types (`id`),
    CONSTRAINT `fk_goal_category` FOREIGN KEY (`category_id`) REFERENCES categories (`id`)
);

CREATE TRIGGER IF NOT EXISTS ENT_INS_GOAL AFTER INSERT ON "entries"
    FOR EACH ROW CALL "com.budgeteer.api.core.GoalValueTrigger";

CREATE TRIGGER IF NOT EXISTS ENT_UPD_GOAL AFTER UPDATE ON "entries"
    FOR EACH ROW CALL "com.budgeteer.api.core.GoalValueTrigger";

CREATE TRIGGER IF NOT EXISTS ENT_DEL_GOAL AFTER DELETE ON "entries"
    FOR EACH ROW CALL "com.budgeteer.api.core.GoalValueTrigger";
