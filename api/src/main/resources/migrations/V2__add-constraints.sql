ALTER TABLE `users` ALTER COLUMN `password` varchar(64);
ALTER TABLE `users` ADD CONSTRAINT `unique_username` UNIQUE(`username`);
ALTER TABLE `users` ADD CONSTRAINT `unique_email` UNIQUE(`email`);