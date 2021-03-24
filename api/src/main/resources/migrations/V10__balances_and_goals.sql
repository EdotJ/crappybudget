ALTER TABLE `password_reset_tokens`
    ALTER COLUMN `user_id` BIGINT NOT NULL;

ALTER TABLE `verification_tokens`
    ALTER COLUMN `user_id` BIGINT NOT NULL;

ALTER TABLE `accounts`
    ADD COLUMN IF NOT EXISTS `balance` DECIMAL(20, 2) NOT NULL DEFAULT 0;

ALTER TABLE `goals`
    ADD COLUMN IF NOT EXISTS `goal_value` DECIMAL(20, 2) NOT NULL DEFAULT 0;

ALTER TABLE `entries`
    ALTER COLUMN `value` DECIMAL(20, 2) NOT NULL;

CREATE INDEX entries_index ON entries (`user_id`, `account_id`, `date`);