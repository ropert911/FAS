DROP DATABASE IF EXISTS funddb;
CREATE DATABASE funddb  default CHARACTER SET utf8 collate utf8_general_ci;
USE funddb;

DROP TABLE IF EXISTS `funddownload`;
CREATE TABLE  IF NOT EXISTS `funddownload` (
  `code` char(64) NOT NULL,
  `ft`  char(4) Not null ,
  `info` varchar(400) character set gbk NOT NULL DEFAULT '',
  PRIMARY KEY (`code`)
) ENGINE=InnoDB  DEFAULT CHARSET=gbk;

DROP TABLE IF EXISTS `fund`;
CREATE TABLE  IF NOT EXISTS `fund` (
  `code` char(64) NOT NULL,
  `name` varchar(64) character set gbk NOT NULL DEFAULT '',
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

-- ----------------------------
-- Records of user
-- ----------------------------
-- INSERT INTO `funddownload` VALUES ('003834', 'gp','003834,华夏能源革新股票,HXNYGXGP,2019-12-27,1.2080,1.2080,-0.4942,7.6649,15.3773,23.5174,25.4413,48.5855,11.1316,,48.4029,20.80,2017-06-07,1,48.4029,1.50%,0.15%,1,0.15%,1,');
-- INSERT INTO `fund` VALUES ('003834', '华夏能源革新');