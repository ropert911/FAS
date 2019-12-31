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
  `ft`  char(4) Not null ,
  `date` DATE NULL DEFAULT NULL,
  `l1y`  double ,
  `l2y`  double ,
  `l3y`  double ,
  `ty`	double ,
  `cy`	double ,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;
