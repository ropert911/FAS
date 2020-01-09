DROP DATABASE IF EXISTS funddb;
CREATE DATABASE funddb  default CHARACTER SET utf8 collate utf8_general_ci;
USE funddb;

DROP TABLE IF EXISTS `ttfund`;
CREATE TABLE  IF NOT EXISTS `ttfund` (
  `code` char(64) NOT NULL,
  `ft`  char(4) Not null ,
  `info` varchar(400) character set gbk NOT NULL DEFAULT '',
  `subt`  char(6) Not null ,
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
  `l1m`  double comment '近1月收益',
  `l3m`  double comment '近3月收益',
  `l6m`  double comment '近6月收益',
  `l1y`  double comment '近1年收益',
  `l2y`  double comment '近2年收益',
  `l3y`  double comment '近3年收益',
  `ty`	double comment '今年收益',
  `cy`	double comment '成立以来收益',
  `subt`  char(6) Not null ,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

DROP TABLE IF EXISTS `fund_quarter`;
CREATE TABLE  IF NOT EXISTS `fund_quarter` (
  `code` char(64) NOT NULL comment '基金代码',
  `quarter` char(10)  comment '季度',
  `rank`  double comment '同类排名',
  `rise`  double comment '季度涨幅',
  PRIMARY KEY (`code`,`quarter`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

DROP TABLE IF EXISTS `fund_year`;
CREATE TABLE  IF NOT EXISTS `fund_year` (
  `code` char(64) NOT NULL comment '基金代码',
  `year` char(10)  comment '年度',
  `rank`  double comment '同类排名',
  `rise`  double comment '季度涨幅',
  PRIMARY KEY (`code`,`year`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

CREATE TABLE  IF NOT EXISTS `fundfl` (
  `code` char(64) NOT NULL comment '基金代码',
  `sgfl`  double  comment '申购费率',
  `yzfl`  double  comment '动作费率',
  `managefl`  double  comment '管理费率',
  `tgfl`  double  comment '托管费率',
  `c1`  char(30)  comment '说明1',
  `f1`  double  comment '费率',
  `c2`  char(30)  comment '说明2',
  `f2`  double  comment '费率',
  `c3`  char(30)  comment '说明3',
  `f3`  double  comment '费率',
  `c4`  char(30)  comment '说明1',
  `f4`  double  comment '费率',
  PRIMARY KEY (`code`)
) ENGINE=InnoDB  DEFAULT CHARSET=gbk;

DROP TABLE IF EXISTS `comp`;
CREATE TABLE  IF NOT EXISTS `comp` (
  `comcode`  char(10) Not null ,
  `ft`  char(4) Not null ,
  `ordernum`  bigint ,
  `name` varchar(64) character set gbk NOT NULL DEFAULT '',
  `estime` DATE NULL DEFAULT NULL,
  `scale`  double ,
  `fnnum`  bigint ,
  `managernum`  bigint ,
  PRIMARY KEY (`comcode`,`ft`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;
