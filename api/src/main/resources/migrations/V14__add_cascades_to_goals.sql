ALTER TABLE `goals` DROP CONSTRAINT `fk_goal_account`;
ALTER TABLE `goals`
    ADD CONSTRAINT `fk_goal_account` FOREIGN KEY (`account_id`) REFERENCES accounts(`id`) ON DELETE CASCADE;

ALTER TABLE `goals` DROP CONSTRAINT `fk_goal_category`;
ALTER TABLE `goals`
    ADD CONSTRAINT `fk_goal_category` FOREIGN KEY (`category_id`) REFERENCES categories(`id`) ON DELETE CASCADE;

ALTER TABLE `goals` DROP CONSTRAINT `fk_goal_user`;
ALTER TABLE `goals`
    ADD CONSTRAINT `fk_goal_user` FOREIGN KEY (`user_id`) REFERENCES users(`id`) ON DELETE CASCADE;