CREATE TABLE IF NOT EXISTS `password_reset_tokens`
(
    `id`         IDENTITY    NOT NULL PRIMARY KEY,
    `value`      VARCHAR(60) NOT NULL,
    `user_id`    DECIMAL     NOT NULL,
    `expiration` DATETIME    NOT NULL,
    CONSTRAINT `fk_password_token_user` FOREIGN KEY (`user_id`) REFERENCES users (`id`)
);