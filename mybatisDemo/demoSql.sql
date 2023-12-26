# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.7.44)
# Database: test
# Generation Time: 2023-12-26 02:45:16 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table t_address
# ------------------------------------------------------------

DROP TABLE IF EXISTS `t_address`;

CREATE TABLE `t_address` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `street` varchar(255) DEFAULT NULL,
                             `city` varchar(255) DEFAULT NULL,
                             `country` varchar(255) DEFAULT NULL,
                             `customer_id` bigint(20) DEFAULT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table t_customer
# ------------------------------------------------------------

DROP TABLE IF EXISTS `t_customer`;

CREATE TABLE `t_customer` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `name` varchar(255) NOT NULL,
                              `phone` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table t_order
# ------------------------------------------------------------

DROP TABLE IF EXISTS `t_order`;

CREATE TABLE `t_order` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                           `address_id` bigint(20) DEFAULT NULL,
                           `customer_id` bigint(20) DEFAULT NULL,
                           `create_time` bigint(20) DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table t_order_item
# ------------------------------------------------------------

DROP TABLE IF EXISTS `t_order_item`;

CREATE TABLE `t_order_item` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `amount` int(11) DEFAULT NULL,
                                `order_id` bigint(20) DEFAULT NULL,
                                `product_id` bigint(20) DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



# Dump of table t_product
# ------------------------------------------------------------

DROP TABLE IF EXISTS `t_product`;

CREATE TABLE `t_product` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `name` varchar(255) DEFAULT NULL,
                             `description` varchar(255) DEFAULT NULL,
                             `price` bigint(20) DEFAULT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
