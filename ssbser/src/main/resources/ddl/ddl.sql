DROP DATABASE IF EXISTS funddb;
CREATE DATABASE funddb  default CHARACTER SET utf8 collate utf8_general_ci;
USE funddb;

DROP TABLE IF EXISTS `ttfund`;
CREATE TABLE  IF NOT EXISTS `ttfund` (
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

DROP TABLE IF EXISTS `comp`;
CREATE TABLE  IF NOT EXISTS `comp` (
  `ft`  char(4) Not null ,
  `ordernum`  long ,
  `name` varchar(64) character set gbk NOT NULL DEFAULT '',
  `estime` DATE NULL DEFAULT NULL,
  `scale`  double ,
  `fnnum`  long ,
  `managernum`  long ,
  PRIMARY KEY (`ft`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;
