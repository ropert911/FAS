/*
Navicat PGSQL Data Transfer

Source Server         : 194
Source Server Version : 101200
Source Host           : 192.168.20.194:5432
Source Database       : image
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 101200
File Encoding         : 65001

Date: 2020-06-05 14:54:23
*/


-- ----------------------------
-- Table structure for device_model
-- ----------------------------
DROP TABLE IF EXISTS `gp_zcvz`;
CREATE TABLE  IF NOT EXISTS `gp_zcvz` (
    `code` char(64) NOT NULL,
    `time` char(64) NOT NULL,
    `SUMLASSET`  double,
    `MONETARYFUND`  double,
    `ACCOUNTBILLREC`  double,
    `BILLREC`  double,
    `ACCOUNTREC`  double,
    `ADVANCEPAY`  double,
    `TOTAL_OTHER_RECE`  double,
    `DIVIDENDREC`  double,
    `OTHERREC`  double,
    `INVENTORY`  double,
    `OTHERLASSET`  double,
    `SUMNONLASSET`  double,
    `LTREC`  double,
    `FIXEDASSET`  double,
    `INTANGIBLEASSET`  double,
    `LTDEFERASSET`  double,
    `DEFERINCOMETAXASSET`  double,
    `GOODWILL`  double,
    `SUMASSET`  double,
    `SUMLLIAB`  double,
    `STBORROW`  double,
    `ACCOUNTPAY`  double,
    `ACCOUNTBILLPAY`  double,
    `SALARYPAY`  double,
    `TAXPAY`  double,
    `TOTAL_OTHER_PAYABLE`  double,
    `INTERESTPAY`  double,
    `DIVIDENDPAY`  double,
    `OTHERPAY`  double,
    `NONLLIABONEYEAR`  double,
    `SUMNONLLIAB`  double,
    `LTACCOUNTPAY`  double,
    `SUMLIAB`  double,
    `SUMSHEQUITY`  double,
    `SHARECAPITAL`  double,
    `CAPITALRESERVE`  double,
    `SURPLUSRESERVE`  double,
    `RETAINEDEARNING`  double,
    `SUMPARENTEQUITY`  double,
    `SUMLIABSHEQUITY`  double,
    PRIMARY KEY (`code`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

DROP TABLE IF EXISTS `gp_lrb`;
CREATE TABLE  IF NOT EXISTS `gp_lrb` (
    `code` char(64) NOT NULL,
    `time` char(64) NOT NULL,
    `TOTALOPERATEREVE`  double,
    `OPERATEREVE`  double,
    `TOTALOPERATEEXP`  double,
    `OPERATEEXP`  double,
    `RDEXP`  double,
    `OPERATETAX`  double,
    `SALEEXP`  double,
    `MANAGEEXP`  double,
    `FINANCEEXP`  double,
    `OPERATEPROFIT`  double,
    `NONOPERATEREVE`  double,
    `NONOPERATEEXP`  double,
    `SUMPROFIT`  double,
    `INCOMETAX`  double,
    `NETPROFIT`  double,
    `PARENTNETPROFIT`  double,
    `KCFJCXSYJLR`  double,
    `BASICEPS`  double,
    `DILUTEDEPS`  double,
    `SUMCINCOME`  double,
    `PARENTCINCOME`  double,
    PRIMARY KEY (`code`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;

DROP TABLE IF EXISTS `gp_xjll`;
CREATE TABLE  IF NOT EXISTS `gp_xjll` (
    `code` char(64) NOT NULL,
    `time` char(64) NOT NULL,
    `SALEGOODSSERVICEREC`  double,
    `OTHEROPERATEREC`  double,
    `SUMOPERATEFLOWIN`  double,
    `BUYGOODSSERVICEPAY`  double,
    `EMPLOYEEPAY`  double,
    `TAXPAY`  double,
    `OTHEROPERATEPAY`  double,
    `SUMOPERATEFLOWOUT`  double,
    `NETOPERATECASHFLOW`  double,
    `DISPFILASSETREC`  double,
    `SUMINVFLOWIN`  double,
    `BUYFILASSETPAY`  double,
    `INVPAY`  double,
    `SUMINVFLOWOUT`  double,
    `NETINVCASHFLOW`  double,
    `ACCEPTINVREC`  double,
    `LOANREC`  double,
    `SUMFINAFLOWIN`  double,
    `REPAYDEBTPAY`  double,
    `DIVIPROFITORINTPAY`  double,
    `OTHERFINAPAY`  double,
    `SUMFINAFLOWOUT`  double,
    `NETFINACASHFLOW`  double,
    `NICASHEQUI`  double,
    `CASHEQUIBEGINNING`  double,
    `CASHEQUIENDING`  double,
    PRIMARY KEY (`code`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=gbk;