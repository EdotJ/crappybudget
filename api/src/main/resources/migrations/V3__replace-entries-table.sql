ALTER TABLE `categories` ADD COLUMN IF NOT EXISTS `budgeted` DECIMAL DEFAULT 0;
ALTER TABLE `goals` ADD COLUMN IF NOT EXISTS `date` DATE NOT NULL DEFAULT NOW();
ALTER TABLE `goals` ADD COLUMN IF NOT EXISTS `value` DECIMAL NOT NULL DEFAULT 0;
DROP TABLE IF EXISTS `entry`;
CREATE TABLE IF NOT EXISTS `entries` (
           `id` IDENTITY NOT NULL PRIMARY KEY,
           `name` VARCHAR(60) NOT NULL,
           `description` VARCHAR(140),
           `date` DATE NOT NULL,
           `value` DECIMAL NOT NULL,
           `user_id` BIGINT NOT NULL,
           `is_expense` BOOLEAN NOT NULL DEFAULT FALSE,
           `category_id` BIGINT NOT NULL,
           `account_id` BIGINT NOT NULL,
           CONSTRAINT `fk_entry_user` FOREIGN KEY(`user_id`) REFERENCES users(`id`),
           CONSTRAINT `fk_entry_category` FOREIGN KEY(`category_id`) REFERENCES categories(`id`),
           CONSTRAINT `fk_entry_account` FOREIGN KEY(`account_id`) REFERENCES accounts(`id`)
);