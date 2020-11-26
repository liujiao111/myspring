
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `cardNo` varchar(100) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `money` int(10) DEFAULT NULL,
  PRIMARY KEY (`cardNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `account` VALUES ('6029621011000', '李大雷', '600');
INSERT INTO `account` VALUES ('6029621011001', '韩梅梅', '2000');