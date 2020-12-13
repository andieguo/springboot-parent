CREATE TABLE IF NOT EXISTS example.user (
 `id` int unsigned NOT NULL AUTO_INCREMENT,
 `name` varchar(64) DEFAULT NULL,
 `email` varchar(64) DEFAULT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
