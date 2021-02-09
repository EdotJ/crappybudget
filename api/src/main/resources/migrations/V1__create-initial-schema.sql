CREATE TABLE IF NOT EXISTS `users` (
    `id` IDENTITY NOT NULL PRIMARY KEY,
    `email` VARCHAR(60) NOT NULL,
    `username` VARCHAR(20) NOT NULL,
    `password` VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS `accounts` (
    `id` IDENTITY NOT NULL PRIMARY KEY,
    `name` VARCHAR(60) NOT NULL,
    `user_id` BIGINT NOT NULL,
    CONSTRAINT `fk_account_user` FOREIGN KEY (`user_id`) REFERENCES users(`id`)
);

CREATE TABLE IF NOT EXISTS `goals` (
    `id` IDENTITY NOT NULL PRIMARY KEY,
    `name` VARCHAR(60) NOT NULL,
    `description` VARCHAR(140),
    `user_id` BIGINT NOT NULL,
    `account_id` BIGINT,
    CONSTRAINT `fk_goal_user` FOREIGN KEY (`user_id`) REFERENCES users(`id`),
    CONSTRAINT `fk_goal_account` FOREIGN KEY (`account_id`) REFERENCES accounts(`id`)
);

CREATE TABLE IF NOT EXISTS `categories` (
    `id` IDENTITY NOT NULL PRIMARY KEY,
    `name` VARCHAR(60) NOT NULL,
    `parent` BIGINT,
    `user_id` BIGINT NOT NULL,
    CONSTRAINT `fk_category_user` FOREIGN KEY (`user_id`) REFERENCES users(`id`)
);

CREATE TABLE IF NOT EXISTS `entry` (
    `id` IDENTITY NOT NULL PRIMARY KEY,
    `date` DATE NOT NULL,
    `value` DECIMAL NOT NULL,
    `category_id` BIGINT NOT NULL,
    CONSTRAINT `fk_entry_category` FOREIGN KEY(`category_id`) REFERENCES categories(`id`)
);
