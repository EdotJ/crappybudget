CREATE TABLE IF NOT EXISTS `refresh_tokens`
(
    `id`            IDENTITY     NOT NULL PRIMARY KEY,
    `user_id`       BIGINT       NOT NULL,
    `username`      VARCHAR(20)  NOT NULL,
    `refresh_token` VARCHAR(256) NOT NULL,
    `revoked`       BOOLEAN      NOT NULL DEFAULT FALSE,
    `date_created`  TIMESTAMP    NOT NULL
);
