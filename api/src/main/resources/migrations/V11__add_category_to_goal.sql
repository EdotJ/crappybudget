ALTER TABLE `goals`
    ADD COLUMN IF NOT EXISTS `category_id` BIGINT;

ALTER TABLE `goals`
    ADD CONSTRAINT `fk_goal_category` FOREIGN KEY (`category_id`) REFERENCES categories (`id`);