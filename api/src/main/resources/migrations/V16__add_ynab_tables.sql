CREATE TABLE IF NOT EXISTS `ynab_categories`
(
    `id`          IDENTITY    NOT NULL PRIMARY KEY,
    `user_id`     BIGINT      NOT NULL,
    `category_id` BIGINT      NOT NULL,
    `ynab_id`     VARCHAR(36) NOT NULL,
    CONSTRAINT `fk_ynab_category` FOREIGN KEY (`category_id`) REFERENCES categories (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ynab_category_user` FOREIGN KEY (`user_id`) REFERENCES users (`id`) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `ynab_accounts`
(
    `id`         IDENTITY    NOT NULL PRIMARY KEY,
    `user_id`    BIGINT      NOT NULL,
    `account_id` BIGINT      NOT NULL,
    `ynab_id`    VARCHAR(36) NOT NULL,
    CONSTRAINT `fk_ynab_account` FOREIGN KEY (`account_id`) REFERENCES accounts (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ynab_account_user` FOREIGN KEY (`user_id`) REFERENCES users (`id`) ON DELETE CASCADE
);