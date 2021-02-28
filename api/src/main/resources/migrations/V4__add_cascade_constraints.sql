ALTER TABLE `accounts` DROP CONSTRAINT `fk_account_user`;
ALTER TABLE `accounts`
    ADD CONSTRAINT `fk_account_user` FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE;

ALTER TABLE `goals` DROP CONSTRAINT `fk_goal_user`;
ALTER TABLE `goals`
    ADD CONSTRAINT `fk_goal_user` FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE;
ALTER TABLE `goals` DROP CONSTRAINT `fk_goal_account`;
ALTER TABLE `goals`
    ADD CONSTRAINT `fk_goal_account` FOREIGN KEY (`account_id`) REFERENCES accounts(`id`) ON DELETE CASCADE;

ALTER TABLE `categories` DROP CONSTRAINT `fk_category_user`;
ALTER TABLE `categories`
    ADD CONSTRAINT `fk_category_user` FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE;

ALTER TABLE `entries` DROP CONSTRAINT `fk_entry_user`;
ALTER TABLE `entries`
    ADD CONSTRAINT `fk_entry_user` FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE;

ALTER TABLE `entries` DROP CONSTRAINT `fk_entry_category`;
ALTER TABLE `entries`
    ADD CONSTRAINT `fk_entry_category` FOREIGN KEY (`category_id`) REFERENCES categories(`id`) ON DELETE CASCADE;

ALTER TABLE `entries` DROP CONSTRAINT `fk_entry_account`;
ALTER TABLE `entries`
    ADD CONSTRAINT `fk_entry_account` FOREIGN KEY (`account_id`) REFERENCES accounts(`id`) ON DELETE CASCADE;
