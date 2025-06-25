DROP DATABASE IF EXISTS shareTrip; 
CREATE DATABASE shareTrip;


USE shareTrip;

CREATE TABLE IF NOT EXISTS `users` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) DEFAULT NULL,
    `email` varchar(255) DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

//pode ter varios destinos, hoteis, pontos turisticos etc

CREATE TABLE IF NOT EXISTS `trips` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



CREATE TABLE IF NOT EXISTS `hoteis` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


