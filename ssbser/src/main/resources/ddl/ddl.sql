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
  `code` char(64) NOT NULL comment '基金代码',
  `comcode` char(64)  comment '公司代码',
  `name` varchar(64) character set gbk NOT NULL DEFAULT '' comment '基金名称',
  `ft`  char(4)  comment '基金类型',
  `date` DATE NULL DEFAULT NULL comment '数据更新时间',
  `level`  double comment '基金评级',
  `l1y`  double comment '近1年收益',
  `l2y`  double comment '近2年收益',
  `l3y`  double comment '近3年收益',
  `ty`	double comment '今年收益',
  `cy`	double comment '成立以来收益',
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

DROP TABLE IF EXISTS `comp`;
CREATE TABLE  IF NOT EXISTS `comp` (
  `comcode`  char(10) Not null ,
  `ft`  char(4) Not null ,
  `ordernum`  long ,
  `name` varchar(64) character set gbk NOT NULL DEFAULT '',
  `estime` DATE NULL DEFAULT NULL,
  `scale`  double ,
  `fnnum`  long ,
  `managernum`  long ,
  PRIMARY KEY (`comcode`,`ft`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;
