DROP DATABASE IF EXISTS funddb;
CREATE DATABASE funddb CHARACTER SET utf8;
USE funddb;

CREATE TABLE `fund` (
  `code` char(64) NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=gbk;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `fund` VALUES ('003834', '华夏能源革新');