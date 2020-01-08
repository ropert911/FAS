package com.xq.secser.provider.tt.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.secser.ssbser.model.FundTypeEnum;
import com.xq.secser.ssbser.model.PageInfo;
import com.xq.secser.ssbser.model.ZQSubTypeEnum;
import com.xq.secser.ssbser.pojo.po.FoundPo;
import com.xq.secser.provider.FundProvider;
import com.xq.secser.provider.tt.pojo.ITtFund;
import com.xq.secser.provider.tt.pojo.TtFundPo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sk-qianxiao
 * @date 2019/12/30
 */
@Service
public class TTFundProvider implements FundProvider {
    private static Logger logger = LoggerFactory.getLogger(TTFundProvider.class);
    private static RestTemplate restTemplate = new RestTemplate();
    @Autowired
    SqlSessionFactory sqlSessionFactory;


    private String getUrl(FundTypeEnum fundTypeEnum, String subType, long pageNum, long pageIndex) {
        String urlPattern = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=%s&rs=&gs=0&sc=zzf&st=desc&sd=%s&ed=%s&qdii=%s|&tabSubtype=%s,,,,,&pi=%d&pn=%d&dx=1&v=0.5797826098327001";
        if (fundTypeEnum == FundTypeEnum.QDII) {
            urlPattern = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=%s&rs=&gs=0&sc=zzf&st=desc&sd=%s&ed=%s&qdii=%s&tabSubtype=%s,,,,,&pi=%d&pn=%d&dx=1&v=0.5797826098327001";
        }

        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = localDate.format(formatter);
        String url = String.format(urlPattern, fundTypeEnum.getUrlParam(), date, date, subType, subType, pageIndex, pageNum);
        return url;
    }

    private String rmUnuseData(String data) {
        logger.debug("rmUnuseData data=={}", data);
        String outData = data.replace("var rankData =", "");
        outData = outData.replace(";", "");
        return outData;
    }

    private String funderRequest(FundTypeEnum ft, String subType, long pageNum, long pageIndex) {
        String url = getUrl(ft, subType, pageNum, pageIndex);
        logger.info("funderRequest ft={} subType={} url={}", ft, subType, url);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = rmUnuseData(responseEntity.getBody());
        return data;
    }

    private PageInfo parseReqData(String data, List<String> allData) {
        PageInfo pageInfo = PageInfo.builder().build();
        JSONObject configJson = JSON.parseObject(data);
        pageInfo.setPageNum(configJson.getLong("pageNum"));
        pageInfo.setPageIndex(configJson.getLong("pageIndex"));
        pageInfo.setAllPages(configJson.getLong("allPages"));
        pageInfo.setAllRecords(configJson.getLong("allRecords"));
        pageInfo.setAllNum(configJson.getLong("allNum"));
        pageInfo.setGpNum(configJson.getLong("gpNum"));
        pageInfo.setHhNum(configJson.getLong("hhNum"));
        pageInfo.setZqNum(configJson.getLong("zqNum"));
        pageInfo.setZsNum(configJson.getLong("zsNum"));
        pageInfo.setBbNum(configJson.getLong("bbNum"));
        pageInfo.setQdiiNum(configJson.getLong("qdiiNum"));
        pageInfo.setEtfNum(configJson.getLong("etfNum"));
        pageInfo.setLofNum(configJson.getLong("lofNum"));
        pageInfo.setFofNum(configJson.getLong("fofNum"));

        JSONArray jsonArray = configJson.getJSONArray("datas");
        jsonArray.forEach(item -> allData.add(item.toString()));

        return pageInfo;
    }

    @Override
    public List<FoundPo> parseFund() {
        List<FoundPo> foundPoList = new ArrayList<>();

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            java.util.Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            ITtFund iTtFund = session.getMapper(ITtFund.class);
            List<TtFundPo> ttFundPoList = iTtFund.getAll();
            ttFundPoList.forEach(data -> {
                String[] items = data.getInfo().split(",");
                String code = items[0];
                String name = items[1];
                String date = items[3];

                Double l1y = items[11].length() == 0 ? null : Double.valueOf(items[11]);
                Double l2y = items[12].length() == 0 ? null : Double.valueOf(items[12]);
                Double l3y = items[13].length() == 0 ? null : Double.valueOf(items[13]);
                Double ty = items[14].length() == 0 ? null : Double.valueOf(items[14]);
                Double cy = items[15].length() == 0 ? null : Double.valueOf(items[15]);
                FoundPo foundPo = FoundPo.builder().code(code).name(name).ft(data.getFt()).l1y(l1y).l2y(l2y).l3y(l3y).ty(ty).cy(cy).build();
                try {
                    java.util.Date d = now;
                    if (date.length() > 0) {
                        d = format.parse(date);
                    }

                    foundPo.setDate(new java.sql.Date(d.getTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                foundPo.setSubt(data.getSubt());

                foundPoList.add(foundPo);
            });
        } finally {
        }


        //包含基金的公司和基金的评级
        List<FoundPo> cCodeLevel = parseFundLevelData();
        Map<String, FoundPo> levMap = cCodeLevel.stream().collect(Collectors.toMap(FoundPo::getCode, a -> a, (key1, key2) -> key2));
        foundPoList.forEach(item -> {
            FoundPo t = levMap.get(item.getCode());
            if (t != null) {
                item.setComcode(t.getComcode());
                item.setLevel(t.getLevel());
            }
        });


        return foundPoList;
    }

    @Override
    public void getflinfo(String fundCode) {
//        String patten = "http://fundf10.eastmoney.com/jjfl_%s.html";
//        String url = String.format(patten, fundCode);
//        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
//        String data = responseEntity.getBody();
        String data = "\n" +
                "\n" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head>\n" +
                "    <title>易方达消费行业股票(110022)基金费率 _ 基金档案 _ 天天基金网</title>\n" +
                "    <meta name=\"keywords\" content=\"易方达消费行业股票,110022,基金,基金档案,F10,基金费率,天天基金网,fund,1234567\" />\n" +
                "    <meta name=\"description\" content=\"东方财富网(www.eastmoney.com)旗下基金平台——天天基金网(fund.eastmoney.com)提供易方达消费行业股票(110022)最新的基金档案信息，易方达消费行业股票(110022)基金的基金费率信息。\" />\n" +
                "    \n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
                "<meta http-equiv=\"Content-Language\" content=\"zh-CN\" />\n" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "<meta http-equiv=\"Cache-Control\" content=\"no-cache\" />\n" +
                "<link href=\"http://j5.dfcfw.com/css/f10/common_min_20180105093654.css\" rel=\"stylesheet\" type=\"text/css\" />\n" +
                "<link href=\"http://j5.dfcfw.com/css/f10/ucbody_min_20190510155950.css\" rel=\"stylesheet\" type=\"text/css\" />\n" +
                "<script src=\"http://fund.eastmoney.com/js/fundcode_search.js\" type=\"text/javascript\"></script>\n" +
                "<base target=\"_blank\" />\n" +
                "\n" +
                "    <style type=\"text/css\">pre{border:#CCC solid 1px;padding:10px;background-color:#f9f9f9;font-family:\"Courier New\",Courier,Arial;font-size:12px;line-height:1.75;white-space:pre-wrap!important;word-wrap:break-word!important}.beizhu{color:#fb6900;font-size:12px;font-weight:normal}.beizhu1{font-size:12px;font-weight:normal}.sgtip{display:block;padding:0 10px;font-size:14px;line-height:1.5}.sgtip .icon{background:url('http://j5.dfcfw.com/image/201809/20180912171607.png') center no-repeat; color:#f40;display:block;height:22px;width:16px;float:left;margin-right:10px}.sgtipClear{clear:both}table.jjfl td.th.w110{width:140px}</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"bodydiv\">\n" +
                "        \n" +
                "<link rel=\"stylesheet\" href=\"//f1.dfcfw.com/??modules/header/v.1.0/css/header_min.css,modules/nav/v.1.0/css/nav_min.css,css/searchbox.css,modules/declare/v.1.0/css/declare_min.css,modules/searchbar/v.1.0/css/searchBar_min.css,modules/footer/v.1.0/css/footer_min.css\"\n" +
                "    />\n" +
                "<style>.map_banner{line-height:30px;height:30px}.phone-qrcode{position:relative;width:165px;cursor:pointer;height:30px}.phone-qrcode .icon-phone{background:url(http://j5.dfcfw.com/image/201610/20161025170115.png) no-repeat;padding-left:15px;height:22px;margin-top:5px;display:inline-block}.phone-qrcode a.text{position:absolute;text-decoration:none;width:140px;top:0;left:23px}.tipsBubble.tipsQcode{position:absolute;background-color:white;padding:4px 8px;border:1px solid #ccc;line-height:1.2;font-size:12px;font-weight:400;z-index:999999;text-align:left;display:none;color:#333;margin-top:5px;width:94px}.tipsQcode p{text-align:center;color:#999;line-height:18px}.poptip-arrow-top{top:-6px}.poptip-arrow-top{height:6px;width:12px;left:7px;margin-left:-6px}.poptip-arrow{position:absolute;overflow:hidden;font-style:normal;font-family:simsun;font-size:12px;text-shadow:0 0 2px #ccc}.poptip-arrow i{color:#fff;text-shadow:none}.tit_h3 .searchbox input{width:250px;height:25px; line-height:25px; padding-left:5px; color: #333;}.tit_h3 .searchbox label{float: none;}.clearfix:after{content:\"020\";display:block;height:0;clear:both;visibility:hidden;}.clearfix{zoom:1;}</style>\n" +
                "<div class=\"mainFrame\">\n" +
                "    <div id=\"fund_common_header\" class=\"fund-common-header\" ><div class=\"ui-clear header-nav\"><div class=\"ui-left\"><ul><li class=\"head-item\"><a href=\"javascript:void(0);\" target=\"_self\" id=\"addFavor\">收藏本站</a></li><li class=\" head-item login\" id=\"loginTTJJ\"><span class=\"blank blankL\">|</span><a id=\"link_login\" href=\"https://login.1234567.com.cn/login\" target=\"_blank\" class=\" header-icon \">安全登录</a><span class=\"blank blankR blankZ\">|</span></li><li class=\"head-item ui-hide\" id=\"logoutTTJJ\"><span class=\"blank blankL\">|</span><span class=\" p8 CustomerName\"></span><a href=\"javascript:void(0);\" target=\"_self\" class=\" header-icon pl20\" id=\"logout\"><span class=\"icon close-icon\"></span>安全退出</a></li><li class=\"head-item js-logout-show\"><a href=\"https://register.1234567.com.cn/reg/step1\" target=\"_blank\">免费开户</a></li><li class=\"head-item js-logout-show\"><a href=\"https://register.1234567.com.cn/forgetpwd\" target=\"_blank\">忘记密码</a></li><li class=\"head-item dropdown\"><span class=\"blank blankL\">|</span><a href=\"http://fundact.eastmoney.com/app/\" class=\"a_phone\" id=\"a_phone\"><span class=\"phone-icon\"></span>手机客户端</a><div id=\"float_phone\" class=\"float_phone dropdown-menu\"><a href=\"http://js1.eastmoney.com/tg.aspx?ID=2771\" class=\"btn btn_iphone\">&nbsp;</a><a href=\"http://js1.eastmoney.com/tg.aspx?ID=2772\" class=\"btn btn_android\">&nbsp;</a></div></li></ul></div><div class=\"ui-right\"><ul class=\"headRight\"><li class=\"head-item\"><a href=\"http://www.1234567.com.cn/\" class=\"header-icon pl20\"><span class=\"icon eastmoney-icon\"></span>返回天天基金网</a></li><li class=\"dropdown  head-item droplist\"><span class=\"blank blankL\">|</span><a href=\"https://trade.1234567.com.cn/\" class=\"red header-icon pr20\">基金交易<span class=\"icon dropdown-icon\"></span></a><span class=\"blank blankR\">|</span><div class=\"dropdown-menu\"><ul><li><a href=\"https://trade.1234567.com.cn/MyAssets/Default\">我的资产</a></li><li><a href=\"https://trade.1234567.com.cn/xjb/index\">活期宝</a></li><li><a href=\"https://trade.1234567.com.cn/dqb/default\">定期宝</a></li><li><a href=\"https://trade.1234567.com.cn/zsb/default\">指数宝</a></li><li><a href=\"http://fund.eastmoney.com/trade/\">买基金</a></li><li><a href=\"https://trade.1234567.com.cn/Investment/default?\">基金定投</a></li><li><a href=\"https://query.1234567.com.cn/Query/Index\">交易查询</a></li></ul></div></li><li class=\"dropdown  head-item droplist\"><a href=\"http://fund.eastmoney.com/daogou/\" class=\" header-icon pr20\">产品导购<span class=\"icon dropdown-icon\"></span></a><span class=\"blank blankR\">|</span><div class=\"dropdown-menu\"><ul><li><a href=\"http://huoqibao.1234567.com.cn/\">活期宝</a></li><li><a href=\"http://dingqibao.1234567.com.cn/\">定期宝</a></li><li><a href=\"http://zhishubao.1234567.com.cn/\">指数宝</a></li><li><a href=\"http://simubao.1234567.com.cn/\">私募宝</a></li><li><a href=\"http://fund.eastmoney.com/trade/default.html\">收益榜</a></li><li><a href=\"http://fundlc.eastmoney.com\">稳健理财</a></li><li><a href=\"http://fund.eastmoney.com/gaoduan/\">高端理财</a></li><li><a href=\"http://fundzt.eastmoney.com/2016/pcbaoxian/index.html\">保险理财</a></li></ul></div></li><li class=\"head-item\"><a href=\"http://favor.fund.eastmoney.com/\">自选基金</a></li><li class=\"head-item\"><span class=\"blank\">|</span></li><li class=\"head-item\"><a href=\"https://vip.1234567.com.cn/Default\">VIP俱乐部</a></li><li class=\"head-item\"><span class=\"blank\">|</span></li><li class=\"head-item\"><a href=\"http://help.1234567.com.cn/\">帮助中心</a></li><li class=\"dropdown  head-item webMap  \"><span class=\"blank blankL\">|</span><a href=\"javascript:void(0)\" target=\"_self\" class=\" header-icon  pl20 pr20\"><span class=\"list-icon icon\"></span>网站导航<span class=\"dropdown-icon icon\"></span></a><span class=\"blank blankR blankZ\">|</span><div class=\"dropdown-menu\"><table class=\"ui-table\"><thead><tr><th class=\"nowrap\"><h3>热点推荐</h3><div class=\"ui-left\" ><a href=\"http://fundact.eastmoney.com/app/\" class=\"red header-icon\" style=\"_top:8px\" ><span class=\"icon iphone-icon\">&nbsp;</span><span class=\"pbuyfund\">手机买基金</span></a></div></th><th><h3>基金数据</h3></th><th class=\"nowrap\"><h3>基金交易</h3><div class=\"ui-left\"><a href=\"https://login.1234567.com.cn/login\" class=\"red\">登录</a><span class=\"red\">|</span><a href=\"https://register.1234567.com.cn/reg/step1\" class=\"red\">开户</a></div></th><th><h3>服务指南</h3></th></tr></thead><tbody><tr><td><div class=\"content col1\"><a href=\"http://favor.fund.eastmoney.com/\">自选基金</a><a href=\"http://fund.eastmoney.com/Compare/\">基金比较</a><br /><a href=\"http://fundlc.eastmoney.com\">稳健理财</a><a href=\"http://data.eastmoney.com/money/calc/CalcFundSY.html\">收益计算</a><br /><a href=\"http://fund.eastmoney.com/ztjj/#sort:SYL_3Y:rs:WRANK\">主题基金</a><a href=\"http://fund.eastmoney.com/manager/default.html#dt14;mcreturnjson;ftall;pn20;pi1;scabbname;stasc\">基金经理</a><br /><a href=\"http://fund.eastmoney.com/yanbao/\">基金研究</a><a href=\"http://jijinba.eastmoney.com/\">基&nbsp;&nbsp;金&nbsp;&nbsp;吧</a></div></td><td><div class=\"content col2\"><a href=\"http://fund.eastmoney.com/fund.html\">基金净值</a><a href=\"http://fund.eastmoney.com/fundguzhi.html\">净值估算</a><br /><a href=\"http://fund.eastmoney.com/data/fundranking.html\">基金排行</a><a href=\"http://fund.eastmoney.com/dingtou/syph_yndt.html\">定投排行</a><br /><a href=\"http://fund.eastmoney.com/data/fundrating.html\">基金评级</a><a href=\"http://fund.eastmoney.com/data/fundfenhong.html\">基金分红</a><br /><a href=\"http://fund.eastmoney.com/company/default.html\">基金公司</a><a href=\"http://simu.eastmoney.com\">私募基金</a></div></td><td><div class=\"content col3\"><a href=\"http://huoqibao.1234567.com.cn/\">活&nbsp;&nbsp;期&nbsp;&nbsp;宝</a><a href=\"http://dingqibao.1234567.com.cn\">定&nbsp;&nbsp;期&nbsp;&nbsp;宝</a><br /><a href=\"http://zhishubao.1234567.com.cn/\">指&nbsp;&nbsp;数&nbsp;&nbsp;宝</a><a href=\"http://simubao.1234567.com.cn/\">私&nbsp;&nbsp;募&nbsp;&nbsp;宝</a><br /><a href=\"http://fund.eastmoney.com/daogou/\">基金导购</a><a href=\"http://fund.eastmoney.com/trade/default.html\">收益排行</a><br /><a href=\"http://fundlc.eastmoney.com\">稳健理财</a><a href=\"http://fund.eastmoney.com/gaoduan/\">高端理财</a></div></td><td><div class=\"content col4\"><a href=\"http://help.1234567.com.cn/\">帮助中心</a><br /><a href=\"http://help.1234567.com.cn/list_812.html\">机构指南</a><br /><a href=\"http://feedback.1234567.com.cn/\">意见反馈</a><br /><a href=\"http://fundact.eastmoney.com/safety/\">安全保障</a><br /></div></td></tr></tbody></table><span class=\"line line1\"></span><span class=\"line line2\"></span><span class=\"line line3\"></span></div><span class=\"blankW\"></span></li></ul></div></div></div>\n" +
                "</div>\n" +
                "<div class=\"mainFrame\">\n" +
                "    \n" +
                "<div class=\"fund-common-searchBar\" >\n" +
                "    <div class=\"inner\">\n" +
                "        <a class=\"logo fl\" href=\"http://www.1234567.com.cn/\" title=\"天天基金网\" target=\"_blank\"></a>\n" +
                "        <h1 class=\"fl\"><a class=\"h1\" href=\"http://fundf10.eastmoney.com/\" target=\"_blank\" title=\"基金档案主页\">基金档案</a></h1>\n" +
                "        <div class=\"wrapper_min searchArea fr\">\n" +
                "            <div class=\"navbar-item funtype-searchArea-search\">\n" +
                "                <div class=\"searchbar-form search-left\" data-plugin=\"searchBox\" data-target=\"#search-tooltip\">\n" +
                "                    <div class=\"searchbar-select search-left\" data-searchbox=\"select\">\n" +
                "                        <div class=\"select-head\">\n" +
                "                            <p class=\"headContent\" onselectstart=\"return false;\" style=\"-moz-user-select: none;\">基&nbsp;金</p>\n" +
                "                            <span class=\"searchBtnIco\"></span>\n" +
                "                        </div>\n" +
                "                        <div class=\"select-option\">\n" +
                "                            <ul>\n" +
                "                                <li class=\"active\" data-select-to=\"fund\">基&nbsp;金</li>\n" +
                "                                <li data-select-to=\"fund-manager\">基金经理</li>\n" +
                "                                <li class=\"noborder\" data-select-to=\"fund-corp\">基金公司</li>\n" +
                "                            </ul>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                    <div class=\"searchbar-input search-left\">\n" +
                "\n" +
                "                        <input type=\"text\" id=\"search-input\" data-searchbox=\"input\" autocomplete=\"off\" url=\" \" />\n" +
                "                        <label class=\"em-placehold search-que\" for=\"search-input\" onselectstart=\"return false;\" style=\"-moz-user-select: none;\"><i class=\"\"></i>请输入基金代码、名称或简拼</label>\n" +
                "                        <div class=\"search-tooltip\" id=\"search-tooltip\"></div>\n" +
                "                    </div>\n" +
                "                    <div class=\"searchbar-btn search-left\">\n" +
                "                        <button class=\"search-submit\" data-searchbox=\"submit\" type=\"submit\">搜索</button>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class=\"fl links\">\n" +
                "                <a class=\"first\" href=\"http://fund.eastmoney.com/allfund.html\" target=\"_blank\">基金代码</a>\n" +
                "                <a href=\"http://fund.eastmoney.com/company/default.html#scomname;dasc\" target=\"_blank\">基金公司</a>\n" +
                "                <a class=\"favorPage\" href=\"javascript:;\" id=\"favorPage\" target=\"_self\"><span class=\"favorPage-star\">★</span>收藏本页</a>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div class=\"clear\"></div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "</div>\n" +
                "<div class=\"mainFrame\">\n" +
                "    <div class=\"fund-common-nav\"><div class=\"fundtypeNav width990\"><div class=\"navItem navItem01\"><ul><li><a class=\"ui-blod w52\" href=\"http://fund.eastmoney.com/data/\">基金数据</a><a href=\"http://fund.eastmoney.com/fund.html\">基金净值</a><a href=\"http://fund.eastmoney.com/fundguzhi.html\">净值估算</a><a href=\"http://fund.eastmoney.com/data/fundranking.html\">基金排行</a><a href=\"http://fund.eastmoney.com/dingtou/syph_yndt.html\">定投</a><a href=\"http://fund.eastmoney.com/fjjj.html\">分级</a><a href=\"http://fund.eastmoney.com/data/fundrating.html\">评级</a></li><li><a  class=\"w52\" href=\"http://fund.eastmoney.com/company/default.html\">基金公司</a><a href=\"http://fund.eastmoney.com/000001.html\">基金品种</a><a href=\"http://fund.eastmoney.com/XFXJJ_jzrgq12.html\">新发基金</a><a href=\"http://fund.eastmoney.com/Fund_sgzt_bzdm.html\">申购状态</a><a href=\"http://fund.eastmoney.com/data/fundfenhong.html\">分红</a><a href=\"http://fund.eastmoney.com/gonggao\">公告</a><a href=\"http://simu.eastmoney.com\">私募</a></li></ul></div><span class=\"lineType01\"></span><div class=\"navItem navItem02\"><ul><li><a class=\"ui-blod w52\" href=\"http://favor.fund.eastmoney.com/\">投资工具</a><a href=\"http://favor.fund.eastmoney.com/\">自选基金</a><a href=\"http://fund.eastmoney.com/Compare/\">比较</a></li><li><a  class=\"w52\"  href=\"http://fund.eastmoney.com/data/fundshaixuan.html\">基金筛选</a><a href=\"http://data.eastmoney.com/money/calc/CalcFundSY.html\">收益计算</a><a href=\"http://fundbook.eastmoney.com/\">账本</a></li></ul></div><span class=\"lineType01\"></span><div class=\"navItem navItem03\"><ul><li><a class=\"ui-blod w52\" href=\"http://roll.eastmoney.com/fund.html\">资讯互动</a><a href=\"http://fund.eastmoney.com/a/cjjyw.html\">要闻</a><a href=\"http://fund.eastmoney.com/a/cjjgd.html\">观点</a><a  class=\"tSpace01\" href=\"http://fund.eastmoney.com/a/cjjxx.html\">学校</a><a href=\"http://fund.eastmoney.com/topic/#000\">专题</a></li><li><a  class=\"w52\"  href=\"http://fund.eastmoney.com/yanbao/\">基金研究</a><a href=\"http://fund.eastmoney.com/a/cjjtzcl.html\">策略</a><a href=\"http://fund.eastmoney.com/a/csmjj.html\">私募</a><a href=\"http://jijinba.eastmoney.com/\">基金吧</a><a class=\"dgx\" href=\"http://js1.eastmoney.com/tg.aspx?ID=5098\">大<span class=\"nav-num-lineheight\">咖</span>秀</a></li></ul></div><span class=\"lineType02 icon ico-shopcar\"><i></i></span><div class=\"navItem navItem04\"><ul><li><a class=\"shopcar w52\" href=\"https://trade.1234567.com.cn/\"><span>基金交易</span></a><a class=\"tSpace01\" href=\"http://huoqibao.1234567.com.cn/\">活期宝</a><a class=\"tSpace01\" href=\"http://dingqibao.1234567.com.cn/\">定期宝</a><a class=\"tSpace01\" href=\"http://zhishubao.1234567.com.cn/\">指数宝</a><a href=\"http://fund.eastmoney.com/gaoduan/\">高端理财</a></li><li><a class=\"w52\"  href=\"http://fund.eastmoney.com/tradeindex.html\">基金超市</a><a href=\"http://fund.eastmoney.com/daogou\">基金导购</a><a href=\"http://fundact.eastmoney.com/banner/default.html\">收益排行</a><a href=\"http://fund.eastmoney.com/fundhot8.html\">热销基金</a><a href=\"http://fund.eastmoney.com/topic.html\">优选基金</a></li></ul></div></div></div>\n" +
                "</div>\n" +
                "\n" +
                "<div class=\"mainFrame\">\n" +
                "    <div class=\"space6\"></div>\n" +
                "    <div class=\"fund_quote\">\n" +
                "        <div>\n" +
                "            <iframe marginheight='0' marginwidth='0' frameborder='0' width='998' height='30' scrolling='no' src='http://fund.eastmoney.com/fund_favor_quote2_beta.html'></iframe>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "\n" +
                "<div class=\"mainFrame\">\n" +
                "    <div class=\"map_banner\">\n" +
                "        <div class=\"left\">\n" +
                "            <a href=\"http://fund.eastmoney.com/\" class=\"map\">天天基金网</a>&nbsp;>&nbsp;<a href=\"http://fundf10.eastmoney.com/\">基金档案</a>&nbsp;>&nbsp;易方达消费行业股票\n" +
                "        </div>\n" +
                "        <div class=\"right \">\n" +
                "            <div class=\"phone-qrcode\" onmouseover=\"document.getElementById('tipsQcode').style.display = 'block'\" onmouseout=\"document.getElementById('tipsQcode').style.display = 'none'\">\n" +
                "                <span class=\"icon-phone\"></span>\n" +
                "                <a class=\"text\">手机访问当前基金品种页</a>\n" +
                "            </div>\n" +
                "            <div id=\"tipsQcode\" class=\"tipsBubble  tipsQcode\">\n" +
                "                <span class=\"poptip-arrow poptip-arrow-top\"><i>◆</i>\n" +
                "                </span>\n" +
                "                <img src=\"http://fund.eastmoney.com/images/QRCode/110022.jpg\"><p>扫一扫二维码</p>\n" +
                "                <p>用手机打开页面</p>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"mainFrame\">\n" +
                "    <div>\n" +
                "        <iframe marginheight='0' marginwidth='0' frameborder='0' width='1000' height=\"60\" scrolling='no' src='http://fundact.eastmoney.com/banner/hqb_hq.html?spm=001001.sbb'></iframe>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"mainFrame\">\n" +
                "    <div id=\"visitfund\"></div>\n" +
                "</div>\n" +
                "<script type=\"text/javascript\" src=\"http://j5.dfcfw.com/js/pinzhong/getcookies_20140928104112.js\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "    if (returnvisit != null && returnvisit != \"\") { document.getElementById(\"visitfund\").innerHTML = returnvisit; }\n" +
                "</script>\n" +
                "\n" +
                "        <div class=\"mainFrame\">\n" +
                "            <div class=\"space6\"></div>\n" +
                "            <div class=\"l_menu left\">\n" +
                "                \n" +
                "<h3></h3>\n" +
                "<div class=\"lm_out\">\n" +
                "    <div class=\"lm_in\" id=\"dlcontent\">\n" +
                "        <div class=\"backpz\"><a class=\"red\" href=\"http://fund.eastmoney.com/110022.html\">返回基金品种页</a></div>\n" +
                "        <dl>\n" +
                "            <dt><span>基本资料</span><b class=\"ico pinch\"></b></dt>\n" +
                "            <dd id=\"f10_menu_jjgk\"><a href=\"jbgk_110022.html\" target=\"_self\">基本概况</a></dd>\n" +
                "            <dd id=\"f10_menu_jjjl\"><a href=\"jjjl_110022.html\" target=\"_self\">基金经理</a></dd>\n" +
                "            <dd id=\"f10_menu_jjgs\"><a href=\"http://fund.eastmoney.com/company/80000229.html\" target=\"_blank\">基金公司</a></dd>\n" +
                "            <dd id=\"f10_menu_jjpj\"><a href=\"jjpj_110022.html\" target=\"_self\">基金评级</a></dd>\n" +
                "            <dd id=\"f10_menu_tssj\" class=\"nb\"><a href=\"tsdata_110022.html\" target=\"_self\">特色数据</a></dd>\n" +
                "        </dl>\n" +
                "        <dl>\n" +
                "            <dt><span>净值回报</span><b class=\"ico pinch\"></b></dt>\n" +
                "            <dd id=\"f10_menu_lsjz\"><a href=\"jjjz_110022.html\" target=\"_self\">历史净值</a><b class=\"ico hot\"></b></dd>\n" +
                "            <dd id=\"f10_menu_fhsp\"><a href=\"fhsp_110022.html\" target=\"_self\">分红送配</a></dd>\n" +
                "            <dd id=\"f10_menu_jdzf\"><a href=\"jdzf_110022.html\" target=\"_self\">阶段涨幅</a></dd>\n" +
                "            <dd id=\"f10_menu_jndzf\" class=\"nb\"><a href=\"jndzf_110022.html\" target=\"_self\">季/年度涨幅</a></dd>\n" +
                "        </dl>\n" +
                "        <dl>\n" +
                "            <dt><span>投资组合</span><b class=\"ico pinch\"></b></dt>\n" +
                "            <dd id=\"f10_menu_jjcc\"><a href=\"ccmx_110022.html\" target=\"_self\">基金持仓</a></dd>\n" +
                "            <dd id=\"f10_menu_zqcc\"><a href=\"ccmx1_110022.html\" target=\"_self\">债券持仓</a></dd>\n" +
                "            <dd id=\"f10_menu_ccbdzs\"><a href=\"ccbdzs_110022.html\" target=\"_self\">持仓变动走势</a></dd>\n" +
                "            <dd id=\"f10_menu_hypz\"><a href=\"hytz_110022.html\" target=\"_self\">行业配置</a></dd>\n" +
                "            <dd id=\"f10_menu_hypzsy\"><a href=\"hypzsy_110022.html\" target=\"_self\">行业配置比较</a></dd>\n" +
                "            <dd id=\"f10_menu_zcpz\"><a href=\"zcpz_110022.html\" target=\"_self\">资产配置</a></dd>\n" +
                "            <dd id=\"f10_menu_zdbd\" class=\"nb\"><a href=\"ccbd_110022.html\" target=\"_self\">重大变动</a></dd>\n" +
                "        </dl>\n" +
                "        <dl>\n" +
                "            <dt><span>规模份额</span><b class=\"ico pinch\"></b></dt>\n" +
                "            <dd id=\"f10_menu_gmbd\"><a href=\"gmbd_110022.html\" target=\"_self\">规模变动</a></dd>\n" +
                "            <dd id=\"f10_menu_cyrjg\" class=\"nb\"><a href=\"cyrjg_110022.html\" target=\"_self\">持有人结构</a></dd>\n" +
                "        </dl>\n" +
                "        <dl>\n" +
                "            <dt><span>基金公告</span><b class=\"ico pinch\"></b></dt>\n" +
                "            <dd id=\"f10_menu_jjgg\"><a href=\"jjgg_110022.html\" target=\"_self\">全部公告</a></dd>\n" +
                "            <dd id=\"f10_menu_fxyz\"><a href=\"jjgg_110022_1.html\" target=\"_self\">发行运作</a></dd>\n" +
                "            <dd id=\"f10_menu_fhgg\"><a href=\"jjgg_110022_2.html\" target=\"_self\">分红公告</a></dd>\n" +
                "            <dd id=\"f10_menu_dqbg\"><a href=\"jjgg_110022_3.html\" target=\"_self\">定期报告</a></dd>\n" +
                "            <dd id=\"f10_menu_rstz\"><a href=\"jjgg_110022_4.html\" target=\"_self\">人事调整</a></dd>\n" +
                "            <dd id=\"f10_menu_jjxs\"><a href=\"jjgg_110022_5.html\" target=\"_self\">基金销售</a></dd>\n" +
                "            <dd id=\"f10_menu_qtgg\" class=\"nb\"><a href=\"jjgg_110022_6.html\" target=\"_self\">其他公告</a></dd>\n" +
                "        </dl>\n" +
                "        <dl>\n" +
                "            <dt><span>财务报表</span><b class=\"ico pinch\"></b></dt>\n" +
                "            <dd id=\"f10_menu_cwzb\"><a href=\"cwzb_110022.html\" target=\"_self\">财务指标</a></dd>\n" +
                "            <dd id=\"f10_menu_fzb\"><a href=\"zcfzb_110022.html\" target=\"_self\">资产负债表</a></dd>\n" +
                "            <dd id=\"f10_menu_lrb\"><a href=\"lrfpb_110022.html\" target=\"_self\">利润表</a></dd>\n" +
                "            \n" +
                "            <dd id=\"f10_menu_srfx\"><a href=\"srfx_110022.html\" target=\"_self\">收入分析</a></dd>\n" +
                "            <dd id=\"f10_menu_fyfx\" class=\"nb\"><a href=\"fyfx_110022.html\" target=\"_self\">费用分析</a></dd>\n" +
                "        </dl>\n" +
                "        <dl>\n" +
                "            <dt><span>销售信息</span><b class=\"ico pinch\"></b></dt>\n" +
                "            <dd id=\"f10_menu_jjfl\" class=\"at\"><a href=\"jjfl_110022.html\" target=\"_self\">购买信息（费率表）</a></dd>\n" +
                "            <dd id=\"f10_menu_jjzh\"><a href=\"jjzh_110022.html\" target=\"_self\">同公司基金转换</a></dd>\n" +
                "            \n" +
                "        </dl>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<div class=\"space6\"></div>\n" +
                "<h3 class=\"rel\"><a href=\"http://fund.eastmoney.com/company/80000229.html\"></a></h3>\n" +
                "<div class=\"lm_out relout\">\n" +
                "    <div class=\"lm_in relin\">\n" +
                "        <ul id='scompany'><li><b class=\"ico trig\"></b><a title='易方达天天理财货币R' href=\"jbgk_000013.html\">易方达天天理财货币R</a></li><li><b class=\"ico trig\"></b><a title='易方达天天理财货币B' href=\"jbgk_000010.html\">易方达天天理财货币B</a></li><li><b class=\"ico trig\"></b><a title='易方达现金增利货币B' href=\"jbgk_000621.html\">易方达现金增利货币B</a></li><li><b class=\"ico trig\"></b><a title='易方达现金增利货币C' href=\"jbgk_005097.html\">易方达现金增利货币C</a></li><li><b class=\"ico trig\"></b><a title='易方达财富快线货币B' href=\"jbgk_000648.html\">易方达财富快线货币B</a></li><li><b class=\"ico trig\"></b><a title='易方达龙宝货币B' href=\"jbgk_000790.html\">易方达龙宝货币B</a></li></ul><ul class=\"more\"><li><a href=\"http://fund.eastmoney.com/company/80000229.html\">查看旗下全部基金>></a></li></ul>\n" +
                "    </div>\n" +
                "</div>\n" +
                "\n" +
                "            </div>\n" +
                "            <div class=\"r_cont right\">\n" +
                "                \n" +
                "<div class=\"basic-new \">\n" +
                "    <div class=\"bs_jz\">\n" +
                "        <div class=\"col-left\">\n" +
                "            <h4 class=\"title\"><a href=\"http://fund.eastmoney.com/110022.html\">易方达消费行业股票 (110022)</a></h4>\n" +
                "            <div>\n" +
                "                \n" +
                "                <a class=\"btn  btn-red \" href=\"https://trade.1234567.com.cn/FundtradePage/default2.aspx?fc=110022&spm=pzm\" target=\"_blank\">\n" +
                "                    <span class=\" label\">购买</span>\n" +
                "                    <span>100元起</span>\n" +
                "                </a>\n" +
                "\n" +
                "                \n" +
                "                <a class=\"btn btn-org\" href=\"https://trade.1234567.com.cn/Investment/add.aspx?fc=110022&spm=pzm\">\n" +
                "                    <span class=\"label\">定投</span>\n" +
                "                    <span>10元起</span>\n" +
                "                </a>\n" +
                "                \n" +
                "                <a class=\"btn btn-blue sm\" href=\"http://fund.eastmoney.com/data/favorapi.aspx?c=110022\">\n" +
                "                    <span class=\"midd label\">+加自选</span>\n" +
                "                </a>\n" +
                "            </div>\n" +
                "\n" +
                "        </div>\n" +
                "        <div class=\"col-right\">\n" +
                "            \n" +
                "            <p class=\"row row1\">\n" +
                "                <label>\n" +
                "                    盘中估算：<span id=\"fund_gsz\" class=\"red lar bold guzhi\">3.0258</span>\n" +
                "                    <span id=\"fundgz_icon\" class=\"icon  icon-up\"></span>\n" +
                "                    <span id=\"fund_gszf\" class=\"red lar bold \">0.09%</span>\n" +
                "                </label>\n" +
                "                <label>\n" +
                "                    单位净值（01-08）：\n" +
                "                <b class=\"red lar bold\">\n" +
                "                    3.0310 ( 0.26% )</b>\n" +
                "                </label>\n" +
                "            </p>\n" +
                "            \n" +
                "\n" +
                "            <p class=\"row\">\n" +
                "                <label>\n" +
                "                    交易状态：<span>开放申购 </span>\n" +
                "                    \n" +
                "                    <span>&nbsp;</span>\n" +
                "                    <span>开放赎回</span>\n" +
                "                </label>\n" +
                "            </p>\n" +
                "            <p class=\"row\">\n" +
                "                <label>\n" +
                "                    购买手续费：\n" +
                "                    <b class=\"sourcerate\">1.50%</b>&nbsp;\n" +
                "                    <b>0.15%</b>&nbsp;\n" +
                "                    \n" +
                "                    <span><b class=\"red\">1</b><b>折</b> </span>\n" +
                "                    \n" +
                "                    <a class=\"link-flxq\" href=\"http://fundf10.eastmoney.com/jjfl_110022.html\" style=\"font-family: SimSun\">费率详情&gt;</a>\n" +
                "                </label>\n" +
                "\n" +
                "                \n" +
                "            </p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <div class=\"bs_gl\">\n" +
                "        <div class=\"space0\"></div>\n" +
                "        <p>\n" +
                "            <label>成立日期：<span>2010-08-20</span></label>\n" +
                "            <label>\n" +
                "                基金经理：&nbsp;&nbsp;<a href=\"http://fund.eastmoney.com/manager/30189741.html\">萧楠</a></label>\n" +
                "            <label>类型：<span>股票型</span></label>\n" +
                "            <label>管理人：<a href=\"http://fund.eastmoney.com/company/80000229.html\">易方达基金</a></label>\n" +
                "            <label>\n" +
                "                资产规模：<span>\n" +
                "                    197.35亿元\n" +
                "                    （截止至：09-30）</span></label>\n" +
                "        </p>\n" +
                "    </div>\n" +
                "</div>\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "    setInterval(function () {\n" +
                "        var today = new Date();\n" +
                "        if (today.getDay() > 0 && today.getDay() < 6) {\n" +
                "            if (today.getHours() >= 9 && today.getHours() < 12) {\n" +
                "                reGetGZ('110022');\n" +
                "            }\n" +
                "            else if (today.getHours() >= 13 && today.getHours() < 15) {\n" +
                "                reGetGZ('110022');\n" +
                "            }\n" +
                "    }\n" +
                "    }, 45000);\n" +
                "</script>\n" +
                "\n" +
                "                <div class=\"detail\">\n" +
                "                    <div class=\"space8\"></div>\n" +
                "                    <div class=\"tit_h3\">\n" +
                "                        <div class=\"space0\"></div>\n" +
                "                        <h1 class=\"left\">\n" +
                "                            <a class=\"tit\" href=\"jjfl_110022.html\">购买信息</a><b class=\"ico arrow\"></b>\n" +
                "                        </h1>\n" +
                "                        <div class=\"right clearfix\" style=\"margin-top: 13px; padding-right: 10px;\">\n" +
                "                            <div style=\"float:left; line-height: 30px;\"> <label>其他基金基金费率查询：</label></div>\n" +
                "                            <div class=\"searchbox\" style=\"position: relative; float: left;\">\n" +
                "                                    <input type=\"text\" id=\"search-input1\" />\n" +
                "                                </div>\n" +
                "                          \n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                    <div class=\"txt_cont\">\n" +
                "                        <div class=\"txt_in\">\n" +
                "                            <div class=\"box\"><div class=\"boxitem w790\"><h4 class=\"t\"><label class=\"left\">交易状态</label><label class=\"right\"></label></h4><div class=\"space0\"></div><table class=\"w770 comm jjfl\"><tbody><tr><td class=\"th w110\">申购状态</td><td class=\"w135\">开放申购</td><td class=\"th w110\">赎回状态</td><td class=\"w135\">开放赎回</td><td class=\"th w110\">定投状态</td><td class=\"w135\">支持</td></tr><tr><td class=\"th w110\">普通回活期宝</td><td class=\"w135\">支持</td><td class=\"th w110\">极速回活期宝 <a  href='http://help.1234567.com.cn/question_882.html'><img src='http://j5.dfcfw.com/image/201512/20151217153747.gif'></a></td><td class=\"w135\">支持</td><td class=\"th w110\">超级转换 <a  href='http://help.1234567.com.cn/question_881.html'><img src='http://j5.dfcfw.com/image/201512/20151217153747.gif'></a></a></td><td class=\"w135\">支持</td></tr></tbody></table></div></div><div class=\"box\"><div class=\"boxitem w790\"><h4 class=\"t\"><label class=\"left\">申购与赎回金额</label><label class=\"right\"></label></h4><div class=\"space0\"></div><table class=\"w770 comm jjfl\"><tbody><tr><td class=\"th w110\">申购起点</td><td class=\"w135\">100元</td><td class=\"th w110\">定投起点</td><td class=\"w135\">10元</td><td class=\"th w110\">日累计申购限额</td><td class=\"w135\">无限额</td></tr><tr><td class=\"th w110\">首次购买</td><td class=\"w135\">100元</td><td class=\"th w110\">追加购买</td><td class=\"w135\">100元</td><td class=\"th w110\">持仓上限</td><td class=\"w135\">无限额</td></tr></tbody></table><hr class='w770 ' style='border: 0;  border-top: 1px dotted #CCC; margin-top: 10px;'><table class=\"w770 comm jjfl\"><tbody><tr><td class=\"th w110\">最小赎回份额</td><td class=\"w135\">1份</td><td class=\"th w110\" style='line-height:18px'>部分赎回最低保留份额</td><td class=\"w135\">---份</td><td class=\"th w110\"></td><td class=\"w135\"></td></tr></tbody></table></div></div><div class=\"box\"><div class=\"boxitem w790\"><h4 class=\"t\"><label class=\"left\">交易确认日</label><label class=\"right\"></label></h4><div class=\"space0\"></div><table class=\"w770 comm jjfl\"><tbody><tr><td class=\"th\" style=\"width:106px;\">买入确认日</td><td style=\"width: 251px;\">T+1</td><td class=\"th w110\">卖出确认日</td><td style=\"width: 272px;\">T+1</td></tr></tbody></table><div class='confirmhelp'><p>相关帮助：</p><p><a href='http://help.1234567.com.cn/question_243.html'>1、什么是T日？</a></p><p><a href='http://help.1234567.com.cn/question_355.html'>2、发起基金赎回交易，赎回金额何时到账？</a></p></div></div></div><div class=\"box\"><div class=\"boxitem w790\"><h4 class=\"t\"><label class=\"left\">运作费用</label><label class=\"right\"></label></h4><div class=\"space0\"></div><table class=\"w770 comm jjfl\"><tbody><tr><td class=\"th w110\">管理费率</td><td class=\"w135\">1.50%（每年）</td><td class=\"th w110\">托管费率</td><td class=\"w135\">0.25%（每年）</td><td class=\"th w110\">销售服务费率</td><td class=\"w135\">---</td></tr></tbody></table><div class=\"tfoot2\"><font class=\"px12\">注：管理费和托管费从基金资产中每日计提。每个交易日公告的基金净值已扣除管理费和托管费，<span style='color:#f40'>无需投资者在每笔交易中另行支付</span>。</font></div></div></div><div class=\"box\"><div class=\"boxitem w790\"><h4 class=\"t\"><label class=\"left\">认购费率（前端）</label><label class=\"right\"></label></h4><div class=\"space0\"></div><table class=\"w650 comm jjfl\"><thead><tr><th class=\"first je\">适用金额</th><th class=\"qx\">适用期限</th><th class=\"last fl speciacol\"><span class=\"sgfv\">原费率</span><span class=\"sgline\">|</span><div class=\"sgyh\">天天基金优惠费率</div></th></tr></thead><tbody><tr><td class=\"th\">小于100万元</td><td>---</td><td>1.30%</td></tr><tr><td class=\"th\">大于等于100万元，小于500万元</td><td>---</td><td>1.00%</td></tr><tr><td class=\"th\">大于等于500万元，小于1000万元</td><td>---</td><td>0.20%</td></tr><tr><td class=\"th\">大于等于1000万元</td><td>---</td><td>每笔1000元</td></tr></tbody></table></div></div><div class=\"box\"><div class=\"boxitem w790\"><h4 class=\"t\"><label class=\"left\">申购费率（前端）</label><label class=\"right\"></label></h4><div class=\"space0\"></div><table class=\"w650 comm jjfl\" style='width:720px;'><thead><tr><th class=\"first je\">适用金额</th><th class=\"qx\">适用期限</th><th class=\"last fl speciacol w230\" style='width:230px;'><span class=\"sgfv\">原费率</span><span class=\"sgline\">|</span><div class=\"sgyh\"  style='width:154px;'>天天基金优惠费率<br/>银行卡购买<span class=\"sgline\" style='float:none;'>|</span>活期宝购买</div></th></tr></thead><tbody><tr><td class=\"th\">小于100万元</td><td>---</td><td><strike class='gray'>1.50%</strike>&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0.15%&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;0.15%</td></tr><tr><td class=\"th\">大于等于100万元，小于500万元</td><td>---</td><td><strike class='gray'>1.20%</strike>&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0.12%&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;0.12%</td></tr><tr><td class=\"th\">大于等于500万元，小于1000万元</td><td>---</td><td><strike class='gray'>0.30%</strike>&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0.03%&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;0.03%</td></tr><tr><td class=\"th\">大于等于1000万元</td><td>---</td><td>每笔1000元</td></tr></tbody></table><div class='sgfltip'><div class='hqbtip'><font class='px12'>友情提示：</font>活期宝买基金方便又快捷。<a href='http://help.1234567.com.cn/question_474.html'>了解什么是活期宝</a></div><div class='hqbtip'><font class='px12' style='visibility:hidden'>友情提示：</font>基金超级转换，转入基金的申购费率参照天天基金活期宝购买优惠费率。<a href='http://help.1234567.com.cn/question_837.html'>了解基金超级转换</a></div></div></div></div><div class=\"box\"><div class=\"boxitem w790\"><h4 class=\"t\"><label class=\"left\">赎回费率<a name=\"shfl\"></a></label><label class=\"right\"></label></h4><div class=\"space0\"></div><table class=\"w650 comm jjfl\"><thead><tr><th class=\"first je\">适用金额</th><th class=\"qx\">适用期限</th><th class=\"last fl\">赎回费率</th></tr></thead><tbody><tr><td class=\"th\">---</td><td>小于等于6天</td><td>1.50%</td></tr><tr><td class=\"th\">---</td><td>大于等于7天，小于等于364天</td><td>0.50%</td></tr><tr><td class=\"th\">---</td><td>大于等于365天，小于等于729天</td><td>0.25%</td></tr><tr><td class=\"th\">---</td><td>大于等于730天</td><td>0.00%</td></tr></tbody></table><div class='sgfltip'><div class='hqbtip'><font class='px12'>友情提示：</font>赎回份额会按照先进先出算持有时间和对应赎回费用。</div></div></div></div>\n" +
                "                            <div class=\"box nb\">\n" +
                "                                <div class=\"boxitem w790\">\n" +
                "                                    <div class=\"remark\">\n" +
                "                                        <p class=\"h1\">\n" +
                "                                            注： <span class=\"beizhu1\">在基金首次募集期购买基金的行为称为认购，认购期结束后基金需要进入不超过3个月的封闭期。</span>\n" +
                "                                        </p>\n" +
                "                                        <p>\n" +
                "                                            基金封闭期结束后，您若申请购买开放式基金，习惯上称为基金申购，以区分在发行期间的认购。\n" +
                "                                        </p>\n" +
                "                                        <p>\n" +
                "\n" +
                "                                            <span class=\"beizhu\">基金申购费用计算公式：</span>\n" +
                "                                        </p>\n" +
                "                                        <p>前端申购费用＝申购金额-申购金额 /（1＋申购费率）</p>\n" +
                "                                        <p>后端申购费用＝赎回份额×申购日基金份额净值（基金份额面值）×后端申购（认购）费率</p>\n" +
                "                                        <p class=\"h2\">基金赎回费用计算公式：</p>\n" +
                "                                        <p>基金赎回费用＝赎回金额×赎回费率</p>\n" +
                "\n" +
                "                                        <p style=\"text-indent: 0; color: #7F7F7F; font-size: 12px; margin-top: 5px; font-family: 宋体,Arial;\">*本基金费率来源基金公司，请以基金公司官网提供的费率数据为准。</p>\n" +
                "                                    </div>\n" +
                "                                </div>\n" +
                "                            </div>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "\n" +
                "    </div>\n" +
                "    \n" +
                "\n" +
                "<div class=\"mainFrame\">\n" +
                "    <div class=\"space6\"></div>\n" +
                "    <div class=\"fund-common-declare\">郑重声明：以上信息（包括但不限于文字、视频、音频、数据及图表）均基于公开信息采集，相关信息并未经过本公司证实，本公司不保证该信息全部或者部分内容的准确性、真实性、完整性，不构成本公司任何推荐或保证。基金具体信息以管理人相关公告为准。投资者投资前需仔细阅读《基金合同》、《招募说明书》等法律文件，了解产品收益与风险特征。过往业绩不预示其未来表现，市场有风险，投资需谨慎。数据来源：东方财富Choice数据。</div>\n" +
                "\n" +
                "</div>\n" +
                "<div class=\"mainFrame\">\n" +
                "    <div class=\"space6\"></div>\n" +
                "    <div class=\"fund-common-footer\"><p class=\"p1\"><a  id=\"footer-setHome\" href=\"javascript:;\" target=\"_self\">将天天基金网设为上网首页吗？</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a id=\"foot-addFavor\" href=\"javascript:;\"  target=\"_self\">将天天基金网添加到收藏夹吗？</a></p><p><a target=\"_blank\" href=\"http://help.1234567.com.cn/aboutus/about.html\">关于我们</a><span>|</span><a target=\"_blank\" href=\"http://help.1234567.com.cn/aboutus/licenses.html\">资质证明</a><span>|</span><a target=\"_blank\" href=\"http://fundact.eastmoney.com/ttjjyjzx/\">研究中心</a><span>|</span><a target=\"_blank\" href=\"http://help.1234567.com.cn/aboutus/connect.html\">联系我们</a><span>|</span><a target=\"_blank\" href=\"http://help.1234567.com.cn/aboutus/security_guid.html\">安全指引</a><span>|</span><a target=\"_blank\" href=\"http://help.1234567.com.cn/aboutus/relief.html\">免责条款</a><span>|</span><a target=\"_blank\" href=\"http://help.1234567.com.cn/aboutus/secret.html\">隐私条款</a><span>|</span><a target=\"_blank\" href=\"http://help.1234567.com.cn/aboutus/risktips.html\">风险提示函</a><span>|</span><a target=\"_blank\" href=\"http://feedback.1234567.com.cn/\">意见建议</a><span>|</span><a target=\"_blank\" href=\"https://fundcs.eastmoney.com/pc/switch.html\">在线客服</a></p><p class=\"cp\">        天天基金客服热线：95021&nbsp;/&nbsp;4001818188<span>|</span>客服邮箱：<a target=\"_blank\" href=\"mailto:vip@1234567.com.cn\">vip@1234567.com.cn</a><span>|</span>人工服务时间：工作日 7:30-21:30 双休日 9:00-21:30<br>        郑重声明：<a target=\"_blank\" href=\"http://help.1234567.com.cn/aboutus/licenses.html\" class=\"footFundCertLink\">天天基金系证监会批准的基金销售机构[000000303]</a>。天天基金网所载文章、数据仅供参考，使用前请核实，风险自负。<br>        中国证监会上海监管局网址：<a href=\"http://www.csrc.gov.cn/pub/shanghai/\" target=\"_blank\">www.csrc.gov.cn/pub/shanghai</a><br>        CopyRight&nbsp;&nbsp;上海天天基金销售有限公司&nbsp;&nbsp;<span id=\"_year\">2011-现在</span>&nbsp;&nbsp;沪ICP证：沪B2-20130026&nbsp;&nbsp;<a href=\"http://www.beian.miit.gov.cn\" target=\"_blank\">网站备案号：沪ICP备11042629号-1</a><br><br></p><p><a class=\"footera footer-police\" title=\"上海网警网络110\" target=\"_blank\" href=\"http://www.cyberpolice.cn/\"></a><a class=\"footera footer-zx110\" title=\"网络社会征信网\" target=\"_blank\" href=\"http://www.zx110.org/\"></a><a class=\"footera footer-shjubao\" title=\"上海违法和违规信息举报中心\" target=\"_blank\" href=\"http://www.shjbzx.cn/\"></a><a class=\"footera footer-hgwb\" title=\"沪公网备\" target=\"_blank\" href=\"http://www.zx110.org/picp/?sn=310104031200\"></a></p><script type=\"text/javascript\" src=\"http://j5.dfcfw.com/libs/js/counter.js\"></script><script type=\"text/javascript\" src=\"https://bdstatics.eastmoney.com/web/prd/jump_tracker.js\" charset=\"UTF-8\"></script>\n" +
                "</div>\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "<script>\n" +
                "    _cpyno = \"c1\";\n" +
                "    var rightAd_bodywidth = 1000; //网页主体宽度\n" +
                "    var rightAd_fixedwidth = 150; //模块占用宽度,一般不变\n" +
                "    var rightAd_width = 135; //模块iframe实际宽度,一般不变\n" +
                "    var rightAd_height = 1861; //模块iframe实际高度,一般不变\n" +
                "    var rightAd_top = 50; //模块距顶部top值\n" +
                "    var rightAd_zindex = 0; //模块z-index值\n" +
                "    var rightAd_url = 'http://fundact.eastmoney.com/banner/hot_em.html?spm=001001.rw'; //内嵌iframe的url\n" +
                "</script>\n" +
                "\n" +
                "<script src=\"http://j5.dfcfw.com/j1/js/embasef10.js?v=20111103.js\" type=\"text/javascript\"></script>\n" +
                "<script src=\"http://j5.dfcfw.com/j1/js/hq-fund.js?v=20120615.js\" type=\"text/javascript\"></script>\n" +
                "<script type=\"text/javascript\" src=\"http://fund.eastmoney.com/js/rightAd.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"http://j5.dfcfw.com/js/data/jquery_183min_20140312171610.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"http://fund.eastmoney.com/webcommon/js/FundCommonPage.min.js\"></script>\n" +
                "<script src=\"http://j5.dfcfw.com/js/f10/f10_min_20191114173714.js\"></script>\n" +
                "<script type=\"text/javascript\" src=\"http://j5.dfcfw.com/js/f10/fundminisearchcom_min_20181207111845.js\"></script>\n" +
                "\n" +
                "<script src=\"//f1.dfcfw.com/??js/FundCommon_min.js,js/searchbox.js\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "    \n" +
                "    jQuery(document).ready(function () {\n" +
                "        dtClickEvent();\n" +
                "    });\n" +
                "    /* 公司同系基金 */\n" +
                "    (function sameCompanyEvent() {\n" +
                "        try {\n" +
                "            var as = $(\"scompany\").getElementsByTagName(\"a\");\n" +
                "            var _ov = function (e) {\n" +
                "                var eO = $E(e);\n" +
                "                eO.parentNode.className = \"at\";\n" +
                "            }\n" +
                "            var _ou = function (e) {\n" +
                "                var eO = $E(e);\n" +
                "                eO.parentNode.className = \"\";\n" +
                "            }\n" +
                "            for (var i = 0; i < as.length; i++) {\n" +
                "                $aE(as[i], \"mouseover\", _ov, true);\n" +
                "                $aE(as[i], \"mouseout\", _ou, true);\n" +
                "            }\n" +
                "        } catch (ex) { }\n" +
                "    })();\n" +
                "</script>\n" +
                "\n" +
                "    <script type=\"text/javascript\">\n" +
                "        //LoadFundSelect(\"jjlist\", \"jjfl\");\n" +
                "        //ChkSelectItem(\"jjlist\", \"110022\")\n" +
                "        \n" +
                "        (function ($) {\n" +
                "            $(\"#search-input1\").fundsearchbox({\n" +
                "                cols: [\"_id\", \"NAME\", \"CODE\", \"NAME\"],\n" +
                "                width: \"257px\",\n" +
                "                url: 'https://fundsuggest.eastmoney.com/FundSearch/api/FundSearchAPI.ashx?callback=?&m=10&t=700&IsNeedBaseInfo=0&IsNeedZTInfo=0&key=',\n" +
                "                    onSelectFund: function (e) {\n" +
                "                        window.open(\"http://fundf10.eastmoney.com/jjfl_\" + e.CODE + \".html\");\n" +
                "\n" +
                "                    }\n" +
                "                })\n" +
                "           \n" +
                "        })(jQuery);\n" +
                "    \n" +
                "    </script>\n" +
                "    <script type=\"text/javascript\" src=\"http://j5.dfcfw.com/libs/js/counter.js\"></script>\n" +
                "</body>\n" +
                "</html>";
        int index = data.indexOf("申购费率（前端）");
        index = data.indexOf("</strike>", index);
        int index2 = data.indexOf("</td>", index);
        String sub = data.substring(index + 9, index2);
        sub = sub.replaceAll("&nbsp;", "");
        sub = sub.substring(1);
        logger.info("aaa==={}", sub);
        String[] items = sub.split("\\|");
        Double purchfl = Double.valueOf(items[0].replace("%", ""));

        index = data.indexOf("管理费率");
        sub = data.substring(index, index + 300);
        index = sub.indexOf("\">");
        sub = sub.substring(index + 2, index + 6);
        Double managefl = Double.valueOf(sub);


        index = data.indexOf("托管费率");
        sub = data.substring(index, index + 300);
        index = sub.indexOf("\">");
        sub = sub.substring(index + 2, index + 6);
        Double tgfl = Double.valueOf(sub);


        index = data.indexOf("赎回费率");
        index = data.indexOf("赎回费率", index + 1);
        sub = data.substring(index, index + 500);
        index = sub.indexOf("<tr>");

        sub = sub.substring(index);
        index = sub.indexOf("<tr>");
        index2 = sub.indexOf("<tr>", index + 3);
        String r1 = sub.substring(index, index2);
        sub = sub.substring(index2);

        index = sub.indexOf("<tr>");
        index2 = sub.indexOf("<tr>", index + 3);
        String r2 = sub.substring(index, index2);
        sub = sub.substring(index2);

        index = sub.indexOf("<tr>");
        index2 = sub.indexOf("<tr>", index + 3);
        String r3 = sub.substring(index, index2);
        sub = sub.substring(index2);

        index = r1.indexOf("<td>");
        index2 = r1.indexOf("</td>", index);
        String item1 = r1.substring(index + 4, index2);
        index = r1.indexOf("<td>", index2);
        index2 = r1.indexOf("</td>", index);
        String value1 = r1.substring(index + 4, index2);
        logger.info("{}=={}", item1, value1.replace("%", ""));

        index = r2.indexOf("<td>");
        index2 = r2.indexOf("</td>", index);
        String item2 = r2.substring(index + 4, index2);
        index = r2.indexOf("<td>", index2);
        index2 = r2.indexOf("</td>", index);
        String value2 = r2.substring(index + 4, index2);
        logger.info("{}=={}", item2, value2.replace("%", ""));

        index = r3.indexOf("<td>");
        index2 = r3.indexOf("</td>", index);
        String item3 = r3.substring(index + 4, index2);
        index = r3.indexOf("<td>", index2);
        index2 = r3.indexOf("</td>", index);
        String value3 = r3.substring(index + 4, index2);
        logger.info("{}=={}", item3, value3.replace("%", ""));

        logger.info("{}", r1);
        logger.info("{}", r2);
        logger.info("{}", r3);
//
//        String item1 = sub.substring(index, index2);
//        logger.info("{}", item1);
//        index = data.indexOf("小于等于",index);


        logger.info("申购费率=={} 管理费率={} 托管费率={}", purchfl, managefl, tgfl);
    }


    private List<FoundPo> parseFundLevelData() {
        List<FoundPo> foundLevelList = new ArrayList<>();
        try {
            Resource res2 = new ClassPathResource("评级.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(res2.getInputStream());
            XSSFSheet dataSheet = workbook.getSheet("data");
            XSSFRow dataheader = dataSheet == null ? null : dataSheet.getRow(0);
            int itfCellNum = dataheader == null ? 0 : dataheader.getPhysicalNumberOfCells();
            int fcodei = 0;
            int ccodei = 0;
            int flevelsh = -1, flevelzs = -1, flevelja = -1;
            for (int i = 0; i < itfCellNum; i++) {
                String content = getCellString(dataheader, i);
                switch (content) {
                    case "代码":
                        fcodei = i;
                        break;
                    case "简称":
                        break;
                    case "相关链接":
                        break;
                    case "基金经理":
                        break;
                    case "基金公司":
                        ccodei = i;
                        break;
                    case "5星评级家数":
                        break;
                    case "上海证券":
                        flevelsh = i;
                        break;
                    case "招商证券":
                        flevelzs = i;
                        break;
                    case "济安金信":
                        flevelja = i;
                        break;
                    default:
                        break;
                }
            }

            int itfRowsNum = dataSheet.getPhysicalNumberOfRows();
            for (int i = 1; i < itfRowsNum; i++) {
                FoundPo foundLevel = FoundPo.builder().build();
                //从第二列获取
                XSSFRow xssfRow = dataSheet.getRow(i);
                String code = getCellString(xssfRow, fcodei);
                while (code.length() < 6) {
                    code = "0" + code;
                }
                foundLevel.setCode(code);

                String ccode = getLinkString(xssfRow, ccodei);
                ccode = ccode.replaceAll("[^\\d]+", "");
                foundLevel.setComcode(ccode);

                String lsh = getCellString(xssfRow, flevelsh);
                lsh = lsh.replaceAll("[^★]+", "");

                String lzs = getCellString(xssfRow, flevelzs);
                lzs = lzs.replaceAll("[^★]+", "");

                String lja = getCellString(xssfRow, flevelja);
                lja = lja.replaceAll("[^★]+", "");
                double level = getLevel(lsh.length(), lzs.length(), lja.length());
                foundLevel.setLevel(level);
                foundLevelList.add(foundLevel);
                logger.debug("{}-{}-{}-{}-{}==={}", code, ccode, lsh, lzs, lja, level);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return foundLevelList;
    }

    private List<String> getAllFund(FundTypeEnum ft, String subType) {
        List<String> allData = new ArrayList<>();

        String data = funderRequest(ft, subType, 500, 1);
        PageInfo pageInfo = parseReqData(data, allData);
        for (long i = pageInfo.getPageIndex() + 1; i <= pageInfo.getAllPages(); ++i) {
            String data2 = funderRequest(ft, subType, pageInfo.getPageNum(), i);
            parseReqData(data2, allData);
        }

        return allData;
    }

    @Override
    public void initFoundData() {
        for (FundTypeEnum fte : FundTypeEnum.values()) {
            if (FundTypeEnum.ZQ == fte) {
                for (ZQSubTypeEnum st : ZQSubTypeEnum.values()) {
                    List<String> allData1 = getAllFund(fte, st.getCode());
                    batchInsertDb(fte, st.getSubt(), allData1);
                }
            } else {
                List<String> allData1 = getAllFund(fte, "");
                batchInsertDb(fte, "", allData1);
            }
        }

        //评组表格从 http://fund.eastmoney.com/data/fundrating.html 基金评级得到
    }

    private void batchInsertDb(FundTypeEnum fundTypeEnum, String subType, List<String> allData) {
        List<TtFundPo> ttFundPoList = new ArrayList<>();
        allData.forEach(data -> {
            String[] items = data.split(",", 2);
            TtFundPo ttFundPo = TtFundPo.builder().code(items[0]).ft(fundTypeEnum.getUrlParam()).info(data).subt(subType).build();
            ttFundPoList.add(ttFundPo);
        });

        SqlSession session = sqlSessionFactory.openSession(true);
        try {
            ITtFund iTtFund = session.getMapper(ITtFund.class);
            iTtFund.insertFundBatch(ttFundPoList);
        } finally {
            session.close();
        }
    }

    private String getCellString(XSSFRow xssfRow, int index) {
        XSSFCell cell = xssfRow.getCell(index);
        if (cell == null) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return cell.toString();
    }

    private String getLinkString(XSSFRow xssfRow, int index) {
        XSSFCell cell = xssfRow.getCell(index);
        if (cell == null) {
            return "";
        }
        XSSFHyperlink link = cell.getHyperlink();
        if (link == null) {
            return "";
        }
        return link.getAddress();
    }

    private double getLevel(double i, double j, double k) {
        double sum = 0;
        int num = 0;
        if (i > 0) {
            sum += i;
            num++;
        }
        if (j > 0) {
            sum += j;
            num++;
        }
        if (k > 0) {
            sum += k;
            num++;
        }

        return num > 0 ? (sum / num) : 0d;
    }
}
