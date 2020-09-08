package com.xq.fin.analyser.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.fin.analyser.pojo.BfbVo;
import com.xq.fin.analyser.util.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BfbUtil {
    private static Logger logger = LoggerFactory.getLogger(BfbUtil.class);

    @Autowired
    private GloableData gloableData;
    @Autowired
    private RestTemplate restTemplate;

    public void getData(String code) {
        List<BfbVo> bfbVoList = new ArrayList<>(10);
        String url = "http://f10.eastmoney.com/NewFinanceAnalysis/PercentAjax?code=" + StringUtil.getSCByCode(code) + "&ctype=4&type=0";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();
//        String data = "\"[{\\\"SECURITYCODE\\\":\\\"605168.SH\\\",\\\"SECURITYSHORTNAME\\\":\\\"三人行\\\",\\\"REPORTTYPE\\\":\\\"1\\\",\\\"TYPE\\\":\\\"4\\\",\\\"REPORTDATE\\\":\\\"2020/6/30 0:00:00\\\",\\\"CURRENCY\\\":\\\"人民币\\\",\\\"KCFJCXSYJLR\\\":\\\"102659936.37\\\",\\\"KCFJCXSYJLR_YOY\\\":\\\"155.718736274227\\\",\\\"TOTALOPERATEREVE\\\":\\\"1149487542.89\\\",\\\"TOTALOPERATEREVE_YOY\\\":\\\"61.2290214930814\\\",\\\"OPERATEREVE\\\":\\\"1149487542.89\\\",\\\"OPERATEREVE_YOY\\\":\\\"61.2290214930814\\\",\\\"INTREVE\\\":\\\"\\\",\\\"INTREVE_YOY\\\":\\\"\\\",\\\"PREMIUMEARNED\\\":\\\"\\\",\\\"PREMIUMEARNED_YOY\\\":\\\"\\\",\\\"COMMREVE\\\":\\\"\\\",\\\"COMMREVE_YOY\\\":\\\"\\\",\\\"OTHERREVE\\\":\\\"\\\",\\\"OTHERREVE_YOY\\\":\\\"\\\",\\\"TOTALOPERATEEXP\\\":\\\"1031441640.73\\\",\\\"TOTALOPERATEEXP_YOY\\\":\\\"55.9609686798747\\\",\\\"OPERATEEXP\\\":\\\"964196436.15\\\",\\\"OPERATEEXP_YOY\\\":\\\"62.5440486365618\\\",\\\"INTEXP\\\":\\\"\\\",\\\"INTEXP_YOY\\\":\\\"\\\",\\\"COMMEXP\\\":\\\"\\\",\\\"COMMEXP_YOY\\\":\\\"\\\",\\\"RDEXP\\\":\\\"867697.86\\\",\\\"RDEXP_YOY\\\":\\\"43.7077012926786\\\",\\\"SURRENDERPREMIUM\\\":\\\"\\\",\\\"SURRENDERPREMIUM_YOY\\\":\\\"\\\",\\\"NETINDEMNITYEXP\\\":\\\"\\\",\\\"NETINDEMNITYEXP_YOY\\\":\\\"\\\",\\\"NETCONTACTRESERVE\\\":\\\"\\\",\\\"NETCONTACTRESERVE_YOY\\\":\\\"\\\",\\\"POLICYDIVIEXP\\\":\\\"\\\",\\\"POLICYDIVIEXP_YOY\\\":\\\"\\\",\\\"RIEXP\\\":\\\"\\\",\\\"RIEXP_YOY\\\":\\\"\\\",\\\"OTHEREXP\\\":\\\"\\\",\\\"OTHEREXP_YOY\\\":\\\"\\\",\\\"OPERATETAX\\\":\\\"2949066.82\\\",\\\"OPERATETAX_YOY\\\":\\\"14.3456039244724\\\",\\\"SALEEXP\\\":\\\"46612810.14\\\",\\\"SALEEXP_YOY\\\":\\\"-5.00389314128285\\\",\\\"MANAGEEXP\\\":\\\"15671984.7\\\",\\\"MANAGEEXP_YOY\\\":\\\"7.12207358598493\\\",\\\"FINANCEEXP\\\":\\\"1143645.06\\\",\\\"FINANCEEXP_YOY\\\":\\\"-10.2407233879411\\\",\\\"ASSETDEVALUELOSS\\\":\\\"\\\",\\\"ASSETDEVALUELOSS_YOY\\\":\\\"\\\",\\\"FVALUEINCOME\\\":\\\"\\\",\\\"FVALUEINCOME_YOY\\\":\\\"\\\",\\\"INVESTINCOME\\\":\\\"\\\",\\\"INVESTINCOME_YOY\\\":\\\"\\\",\\\"INVESTJOINTINCOME\\\":\\\"\\\",\\\"INVESTJOINTINCOME_YOY\\\":\\\"\\\",\\\"EXCHANGEINCOME\\\":\\\"\\\",\\\"EXCHANGEINCOME_YOY\\\":\\\"\\\",\\\"OPERATEPROFIT\\\":\\\"120166219.8\\\",\\\"OPERATEPROFIT_YOY\\\":\\\"123.76163108493\\\",\\\"NONOPERATEREVE\\\":\\\"2102.63\\\",\\\"NONOPERATEREVE_YOY\\\":\\\"212286.868686869\\\",\\\"NONLASSETREVE\\\":\\\"\\\",\\\"NONLASSETREVE_YOY\\\":\\\"\\\",\\\"NONOPERATEEXP\\\":\\\"459999.92\\\",\\\"NONOPERATEEXP_YOY\\\":\\\"3997.63343811993\\\",\\\"NONLASSETNETLOSS\\\":\\\"\\\",\\\"NONLASSETNETLOSS_YOY\\\":\\\"\\\",\\\"SUMPROFIT\\\":\\\"119708322.51\\\",\\\"SUMPROFIT_YOY\\\":\\\"122.955582470952\\\",\\\"INCOMETAX\\\":\\\"16986819.12\\\",\\\"INCOMETAX_YOY\\\":\\\"97.7529187489681\\\",\\\"NETPROFIT\\\":\\\"102721503.39\\\",\\\"NETPROFIT_YOY\\\":\\\"127.755605403799\\\",\\\"COMBINEDNETPROFITB\\\":\\\"\\\",\\\"COMBINEDNETPROFITB_YOY\\\":\\\"\\\",\\\"PARENTNETPROFIT\\\":\\\"102721503.39\\\",\\\"PARENTNETPROFIT_YOY\\\":\\\"127.755605403799\\\",\\\"MINORITYINCOME\\\":\\\"\\\",\\\"MINORITYINCOME_YOY\\\":\\\"\\\",\\\"BASICEPS\\\":\\\"1.88\\\",\\\"BASICEPS_YOY\\\":\\\"116.091954022988\\\",\\\"DILUTEDEPS\\\":\\\"1.88\\\",\\\"DILUTEDEPS_YOY\\\":\\\"116.091954022988\\\",\\\"OTHERCINCOME\\\":\\\"\\\",\\\"OTHERCINCOME_YOY\\\":\\\"\\\",\\\"PARENTOTHERCINCOME\\\":\\\"\\\",\\\"PARENTOTHERCINCOME_YOY\\\":\\\"\\\",\\\"MINORITYOTHERCINCOME\\\":\\\"\\\",\\\"MINORITYOTHERCINCOME_YOY\\\":\\\"\\\",\\\"SUMCINCOME\\\":\\\"102721503.39\\\",\\\"SUMCINCOME_YOY\\\":\\\"127.755605403799\\\",\\\"PARENTCINCOME\\\":\\\"102721503.39\\\",\\\"PARENTCINCOME_YOY\\\":\\\"127.755605403799\\\",\\\"MINORITYCINCOME\\\":\\\"\\\",\\\"MINORITYCINCOME_YOY\\\":\\\"\\\"},{\\\"SECURITYCODE\\\":\\\"605168.SH\\\",\\\"SECURITYSHORTNAME\\\":\\\"三人行\\\",\\\"REPORTTYPE\\\":\\\"1\\\",\\\"TYPE\\\":\\\"4\\\",\\\"REPORTDATE\\\":\\\"2020/3/31 0:00:00\\\",\\\"CURRENCY\\\":\\\"人民币\\\",\\\"KCFJCXSYJLR\\\":\\\"40316908.72\\\",\\\"KCFJCXSYJLR_YOY\\\":\\\"166.2047338185\\\",\\\"TOTALOPERATEREVE\\\":\\\"444847999.55\\\",\\\"TOTALOPERATEREVE_YOY\\\":\\\"37.15738532305\\\",\\\"OPERATEREVE\\\":\\\"444847999.55\\\",\\\"OPERATEREVE_YOY\\\":\\\"37.15738532305\\\",\\\"INTREVE\\\":\\\"\\\",\\\"INTREVE_YOY\\\":\\\"\\\",\\\"PREMIUMEARNED\\\":\\\"\\\",\\\"PREMIUMEARNED_YOY\\\":\\\"\\\",\\\"COMMREVE\\\":\\\"\\\",\\\"COMMREVE_YOY\\\":\\\"\\\",\\\"OTHERREVE\\\":\\\"\\\",\\\"OTHERREVE_YOY\\\":\\\"\\\",\\\"TOTALOPERATEEXP\\\":\\\"397622860.4\\\",\\\"TOTALOPERATEEXP_YOY\\\":\\\"30.6165776045408\\\",\\\"OPERATEEXP\\\":\\\"365647095.15\\\",\\\"OPERATEEXP_YOY\\\":\\\"35.1406537953437\\\",\\\"INTEXP\\\":\\\"\\\",\\\"INTEXP_YOY\\\":\\\"\\\",\\\"COMMEXP\\\":\\\"\\\",\\\"COMMEXP_YOY\\\":\\\"\\\",\\\"RDEXP\\\":\\\"332758.56\\\",\\\"RDEXP_YOY\\\":\\\"12.6916258678725\\\",\\\"SURRENDERPREMIUM\\\":\\\"\\\",\\\"SURRENDERPREMIUM_YOY\\\":\\\"\\\",\\\"NETINDEMNITYEXP\\\":\\\"\\\",\\\"NETINDEMNITYEXP_YOY\\\":\\\"\\\",\\\"NETCONTACTRESERVE\\\":\\\"\\\",\\\"NETCONTACTRESERVE_YOY\\\":\\\"\\\",\\\"POLICYDIVIEXP\\\":\\\"\\\",\\\"POLICYDIVIEXP_YOY\\\":\\\"\\\",\\\"RIEXP\\\":\\\"\\\",\\\"RIEXP_YOY\\\":\\\"\\\",\\\"OTHEREXP\\\":\\\"\\\",\\\"OTHEREXP_YOY\\\":\\\"\\\",\\\"OPERATETAX\\\":\\\"1208159.72\\\",\\\"OPERATETAX_YOY\\\":\\\"-17.1868267499635\\\",\\\"SALEEXP\\\":\\\"22363925.86\\\",\\\"SALEEXP_YOY\\\":\\\"-6.0121291707379\\\",\\\"MANAGEEXP\\\":\\\"7385851.14\\\",\\\"MANAGEEXP_YOY\\\":\\\"-4.79413718678748\\\",\\\"FINANCEEXP\\\":\\\"685069.97\\\",\\\"FINANCEEXP_YOY\\\":\\\"25.5459214552779\\\",\\\"ASSETDEVALUELOSS\\\":\\\"\\\",\\\"ASSETDEVALUELOSS_YOY\\\":\\\"\\\",\\\"FVALUEINCOME\\\":\\\"\\\",\\\"FVALUEINCOME_YOY\\\":\\\"\\\",\\\"INVESTINCOME\\\":\\\"\\\",\\\"INVESTINCOME_YOY\\\":\\\"\\\",\\\"INVESTJOINTINCOME\\\":\\\"\\\",\\\"INVESTJOINTINCOME_YOY\\\":\\\"\\\",\\\"EXCHANGEINCOME\\\":\\\"\\\",\\\"EXCHANGEINCOME_YOY\\\":\\\"\\\",\\\"OPERATEPROFIT\\\":\\\"47869915.3\\\",\\\"OPERATEPROFIT_YOY\\\":\\\"162.208887387732\\\",\\\"NONOPERATEREVE\\\":\\\"0.32\\\",\\\"NONOPERATEREVE_YOY\\\":\\\"-99.8857142857143\\\",\\\"NONLASSETREVE\\\":\\\"\\\",\\\"NONLASSETREVE_YOY\\\":\\\"\\\",\\\"NONOPERATEEXP\\\":\\\"250000\\\",\\\"NONOPERATEEXP_YOY\\\":\\\"\\\",\\\"NONLASSETNETLOSS\\\":\\\"\\\",\\\"NONLASSETNETLOSS_YOY\\\":\\\"\\\",\\\"SUMPROFIT\\\":\\\"47619915.62\\\",\\\"SUMPROFIT_YOY\\\":\\\"160.835506235621\\\",\\\"INCOMETAX\\\":\\\"7197606.9\\\",\\\"INCOMETAX_YOY\\\":\\\"141.805606855079\\\",\\\"NETPROFIT\\\":\\\"40422308.72\\\",\\\"NETPROFIT_YOY\\\":\\\"164.542592910436\\\",\\\"COMBINEDNETPROFITB\\\":\\\"\\\",\\\"COMBINEDNETPROFITB_YOY\\\":\\\"\\\",\\\"PARENTNETPROFIT\\\":\\\"40422308.72\\\",\\\"PARENTNETPROFIT_YOY\\\":\\\"164.542592910436\\\",\\\"MINORITYINCOME\\\":\\\"\\\",\\\"MINORITYINCOME_YOY\\\":\\\"\\\",\\\"BASICEPS\\\":\\\"0.78\\\",\\\"BASICEPS_YOY\\\":\\\"168.965517241379\\\",\\\"DILUTEDEPS\\\":\\\"\\\",\\\"DILUTEDEPS_YOY\\\":\\\"\\\",\\\"OTHERCINCOME\\\":\\\"\\\",\\\"OTHERCINCOME_YOY\\\":\\\"\\\",\\\"PARENTOTHERCINCOME\\\":\\\"\\\",\\\"PARENTOTHERCINCOME_YOY\\\":\\\"\\\",\\\"MINORITYOTHERCINCOME\\\":\\\"\\\",\\\"MINORITYOTHERCINCOME_YOY\\\":\\\"\\\",\\\"SUMCINCOME\\\":\\\"40422308.72\\\",\\\"SUMCINCOME_YOY\\\":\\\"164.542592910436\\\",\\\"PARENTCINCOME\\\":\\\"40422308.72\\\",\\\"PARENTCINCOME_YOY\\\":\\\"164.542592910436\\\",\\\"MINORITYCINCOME\\\":\\\"\\\",\\\"MINORITYCINCOME_YOY\\\":\\\"\\\"},{\\\"SECURITYCODE\\\":\\\"605168.SH\\\",\\\"SECURITYSHORTNAME\\\":\\\"三人行\\\",\\\"REPORTTYPE\\\":\\\"1\\\",\\\"TYPE\\\":\\\"4\\\",\\\"REPORTDATE\\\":\\\"2019/12/31 0:00:00\\\",\\\"CURRENCY\\\":\\\"人民币\\\",\\\"KCFJCXSYJLR\\\":\\\"182097212.46\\\",\\\"KCFJCXSYJLR_YOY\\\":\\\"50.9780731413383\\\",\\\"TOTALOPERATEREVE\\\":\\\"1631426143.56\\\",\\\"TOTALOPERATEREVE_YOY\\\":\\\"48.3773997292584\\\",\\\"OPERATEREVE\\\":\\\"1631426143.56\\\",\\\"OPERATEREVE_YOY\\\":\\\"48.3773997292584\\\",\\\"INTREVE\\\":\\\"\\\",\\\"INTREVE_YOY\\\":\\\"\\\",\\\"PREMIUMEARNED\\\":\\\"\\\",\\\"PREMIUMEARNED_YOY\\\":\\\"\\\",\\\"COMMREVE\\\":\\\"\\\",\\\"COMMREVE_YOY\\\":\\\"\\\",\\\"OTHERREVE\\\":\\\"\\\",\\\"OTHERREVE_YOY\\\":\\\"\\\",\\\"TOTALOPERATEEXP\\\":\\\"1418140805.96\\\",\\\"TOTALOPERATEEXP_YOY\\\":\\\"48.5127694918619\\\",\\\"OPERATEEXP\\\":\\\"1266685601.41\\\",\\\"OPERATEEXP_YOY\\\":\\\"54.2960985305568\\\",\\\"INTEXP\\\":\\\"\\\",\\\"INTEXP_YOY\\\":\\\"\\\",\\\"COMMEXP\\\":\\\"\\\",\\\"COMMEXP_YOY\\\":\\\"\\\",\\\"RDEXP\\\":\\\"2287686.63\\\",\\\"RDEXP_YOY\\\":\\\"-34.8944714522044\\\",\\\"SURRENDERPREMIUM\\\":\\\"\\\",\\\"SURRENDERPREMIUM_YOY\\\":\\\"\\\",\\\"NETINDEMNITYEXP\\\":\\\"\\\",\\\"NETINDEMNITYEXP_YOY\\\":\\\"\\\",\\\"NETCONTACTRESERVE\\\":\\\"\\\",\\\"NETCONTACTRESERVE_YOY\\\":\\\"\\\",\\\"POLICYDIVIEXP\\\":\\\"\\\",\\\"POLICYDIVIEXP_YOY\\\":\\\"\\\",\\\"RIEXP\\\":\\\"\\\",\\\"RIEXP_YOY\\\":\\\"\\\",\\\"OTHEREXP\\\":\\\"\\\",\\\"OTHEREXP_YOY\\\":\\\"\\\",\\\"OPERATETAX\\\":\\\"5596519.31\\\",\\\"OPERATETAX_YOY\\\":\\\"-29.8027274172184\\\",\\\"SALEEXP\\\":\\\"106894471.36\\\",\\\"SALEEXP_YOY\\\":\\\"19.7684876356834\\\",\\\"MANAGEEXP\\\":\\\"33331118.3\\\",\\\"MANAGEEXP_YOY\\\":\\\"7.56612357903388\\\",\\\"FINANCEEXP\\\":\\\"3345408.95\\\",\\\"FINANCEEXP_YOY\\\":\\\"50.2696730163302\\\",\\\"ASSETDEVALUELOSS\\\":\\\"\\\",\\\"ASSETDEVALUELOSS_YOY\\\":\\\"\\\",\\\"FVALUEINCOME\\\":\\\"\\\",\\\"FVALUEINCOME_YOY\\\":\\\"\\\",\\\"INVESTINCOME\\\":\\\"\\\",\\\"INVESTINCOME_YOY\\\":\\\"\\\",\\\"INVESTJOINTINCOME\\\":\\\"\\\",\\\"INVESTJOINTINCOME_YOY\\\":\\\"\\\",\\\"EXCHANGEINCOME\\\":\\\"\\\",\\\"EXCHANGEINCOME_YOY\\\":\\\"\\\",\\\"OPERATEPROFIT\\\":\\\"228734977.75\\\",\\\"OPERATEPROFIT_YOY\\\":\\\"54.5729046437287\\\",\\\"NONOPERATEREVE\\\":\\\"156942.26\\\",\\\"NONOPERATEREVE_YOY\\\":\\\"\\\",\\\"NONLASSETREVE\\\":\\\"\\\",\\\"NONLASSETREVE_YOY\\\":\\\"\\\",\\\"NONOPERATEEXP\\\":\\\"511276.1\\\",\\\"NONOPERATEEXP_YOY\\\":\\\"-5.55427592109066\\\",\\\"NONLASSETNETLOSS\\\":\\\"\\\",\\\"NONLASSETNETLOSS_YOY\\\":\\\"\\\",\\\"SUMPROFIT\\\":\\\"228380643.91\\\",\\\"SUMPROFIT_YOY\\\":\\\"54.9001195697972\\\",\\\"INCOMETAX\\\":\\\"34757589.92\\\",\\\"INCOMETAX_YOY\\\":\\\"48.4935057627501\\\",\\\"NETPROFIT\\\":\\\"193623053.99\\\",\\\"NETPROFIT_YOY\\\":\\\"56.1091634701642\\\",\\\"COMBINEDNETPROFITB\\\":\\\"\\\",\\\"COMBINEDNETPROFITB_YOY\\\":\\\"\\\",\\\"PARENTNETPROFIT\\\":\\\"193623053.99\\\",\\\"PARENTNETPROFIT_YOY\\\":\\\"56.1091634701642\\\",\\\"MINORITYINCOME\\\":\\\"\\\",\\\"MINORITYINCOME_YOY\\\":\\\"\\\",\\\"BASICEPS\\\":\\\"3.74\\\",\\\"BASICEPS_YOY\\\":\\\"56.4853556485356\\\",\\\"DILUTEDEPS\\\":\\\"3.74\\\",\\\"DILUTEDEPS_YOY\\\":\\\"56.4853556485356\\\",\\\"OTHERCINCOME\\\":\\\"\\\",\\\"OTHERCINCOME_YOY\\\":\\\"\\\",\\\"PARENTOTHERCINCOME\\\":\\\"\\\",\\\"PARENTOTHERCINCOME_YOY\\\":\\\"\\\",\\\"MINORITYOTHERCINCOME\\\":\\\"\\\",\\\"MINORITYOTHERCINCOME_YOY\\\":\\\"\\\",\\\"SUMCINCOME\\\":\\\"193623053.99\\\",\\\"SUMCINCOME_YOY\\\":\\\"56.1091634701642\\\",\\\"PARENTCINCOME\\\":\\\"193623053.99\\\",\\\"PARENTCINCOME_YOY\\\":\\\"56.1091634701642\\\",\\\"MINORITYCINCOME\\\":\\\"\\\",\\\"MINORITYCINCOME_YOY\\\":\\\"\\\"},{\\\"SECURITYCODE\\\":\\\"605168.SH\\\",\\\"SECURITYSHORTNAME\\\":\\\"三人行\\\",\\\"REPORTTYPE\\\":\\\"1\\\",\\\"TYPE\\\":\\\"4\\\",\\\"REPORTDATE\\\":\\\"2019/6/30 0:00:00\\\",\\\"CURRENCY\\\":\\\"人民币\\\",\\\"KCFJCXSYJLR\\\":\\\"40145645.12\\\",\\\"KCFJCXSYJLR_YOY\\\":\\\"\\\",\\\"TOTALOPERATEREVE\\\":\\\"712953246.41\\\",\\\"TOTALOPERATEREVE_YOY\\\":\\\"\\\",\\\"OPERATEREVE\\\":\\\"712953246.41\\\",\\\"OPERATEREVE_YOY\\\":\\\"\\\",\\\"INTREVE\\\":\\\"\\\",\\\"INTREVE_YOY\\\":\\\"\\\",\\\"PREMIUMEARNED\\\":\\\"\\\",\\\"PREMIUMEARNED_YOY\\\":\\\"\\\",\\\"COMMREVE\\\":\\\"\\\",\\\"COMMREVE_YOY\\\":\\\"\\\",\\\"OTHERREVE\\\":\\\"\\\",\\\"OTHERREVE_YOY\\\":\\\"\\\",\\\"TOTALOPERATEEXP\\\":\\\"661346008.21\\\",\\\"TOTALOPERATEEXP_YOY\\\":\\\"\\\",\\\"OPERATEEXP\\\":\\\"593190857.64\\\",\\\"OPERATEEXP_YOY\\\":\\\"\\\",\\\"INTEXP\\\":\\\"\\\",\\\"INTEXP_YOY\\\":\\\"\\\",\\\"COMMEXP\\\":\\\"\\\",\\\"COMMEXP_YOY\\\":\\\"\\\",\\\"RDEXP\\\":\\\"603793.57\\\",\\\"RDEXP_YOY\\\":\\\"\\\",\\\"SURRENDERPREMIUM\\\":\\\"\\\",\\\"SURRENDERPREMIUM_YOY\\\":\\\"\\\",\\\"NETINDEMNITYEXP\\\":\\\"\\\",\\\"NETINDEMNITYEXP_YOY\\\":\\\"\\\",\\\"NETCONTACTRESERVE\\\":\\\"\\\",\\\"NETCONTACTRESERVE_YOY\\\":\\\"\\\",\\\"POLICYDIVIEXP\\\":\\\"\\\",\\\"POLICYDIVIEXP_YOY\\\":\\\"\\\",\\\"RIEXP\\\":\\\"\\\",\\\"RIEXP_YOY\\\":\\\"\\\",\\\"OTHEREXP\\\":\\\"\\\",\\\"OTHEREXP_YOY\\\":\\\"\\\",\\\"OPERATETAX\\\":\\\"2579081.94\\\",\\\"OPERATETAX_YOY\\\":\\\"\\\",\\\"SALEEXP\\\":\\\"49068126.77\\\",\\\"SALEEXP_YOY\\\":\\\"\\\",\\\"MANAGEEXP\\\":\\\"14630023.65\\\",\\\"MANAGEEXP_YOY\\\":\\\"\\\",\\\"FINANCEEXP\\\":\\\"1274124.64\\\",\\\"FINANCEEXP_YOY\\\":\\\"\\\",\\\"ASSETDEVALUELOSS\\\":\\\"\\\",\\\"ASSETDEVALUELOSS_YOY\\\":\\\"\\\",\\\"FVALUEINCOME\\\":\\\"\\\",\\\"FVALUEINCOME_YOY\\\":\\\"\\\",\\\"INVESTINCOME\\\":\\\"\\\",\\\"INVESTINCOME_YOY\\\":\\\"\\\",\\\"INVESTJOINTINCOME\\\":\\\"\\\",\\\"INVESTJOINTINCOME_YOY\\\":\\\"\\\",\\\"EXCHANGEINCOME\\\":\\\"\\\",\\\"EXCHANGEINCOME_YOY\\\":\\\"\\\",\\\"OPERATEPROFIT\\\":\\\"53702781.49\\\",\\\"OPERATEPROFIT_YOY\\\":\\\"\\\",\\\"NONOPERATEREVE\\\":\\\"0.99\\\",\\\"NONOPERATEREVE_YOY\\\":\\\"\\\",\\\"NONLASSETREVE\\\":\\\"\\\",\\\"NONLASSETREVE_YOY\\\":\\\"\\\",\\\"NONOPERATEEXP\\\":\\\"11225.99\\\",\\\"NONOPERATEEXP_YOY\\\":\\\"\\\",\\\"NONLASSETNETLOSS\\\":\\\"\\\",\\\"NONLASSETNETLOSS_YOY\\\":\\\"\\\",\\\"SUMPROFIT\\\":\\\"53691556.49\\\",\\\"SUMPROFIT_YOY\\\":\\\"\\\",\\\"INCOMETAX\\\":\\\"8589920.81\\\",\\\"INCOMETAX_YOY\\\":\\\"\\\",\\\"NETPROFIT\\\":\\\"45101635.68\\\",\\\"NETPROFIT_YOY\\\":\\\"\\\",\\\"COMBINEDNETPROFITB\\\":\\\"\\\",\\\"COMBINEDNETPROFITB_YOY\\\":\\\"\\\",\\\"PARENTNETPROFIT\\\":\\\"45101635.68\\\",\\\"PARENTNETPROFIT_YOY\\\":\\\"\\\",\\\"MINORITYINCOME\\\":\\\"\\\",\\\"MINORITYINCOME_YOY\\\":\\\"\\\",\\\"BASICEPS\\\":\\\"0.87\\\",\\\"BASICEPS_YOY\\\":\\\"\\\",\\\"DILUTEDEPS\\\":\\\"0.87\\\",\\\"DILUTEDEPS_YOY\\\":\\\"\\\",\\\"OTHERCINCOME\\\":\\\"\\\",\\\"OTHERCINCOME_YOY\\\":\\\"\\\",\\\"PARENTOTHERCINCOME\\\":\\\"\\\",\\\"PARENTOTHERCINCOME_YOY\\\":\\\"\\\",\\\"MINORITYOTHERCINCOME\\\":\\\"\\\",\\\"MINORITYOTHERCINCOME_YOY\\\":\\\"\\\",\\\"SUMCINCOME\\\":\\\"45101635.68\\\",\\\"SUMCINCOME_YOY\\\":\\\"\\\",\\\"PARENTCINCOME\\\":\\\"45101635.68\\\",\\\"PARENTCINCOME_YOY\\\":\\\"\\\",\\\"MINORITYCINCOME\\\":\\\"\\\",\\\"MINORITYCINCOME_YOY\\\":\\\"\\\"},{\\\"SECURITYCODE\\\":\\\"605168.SH\\\",\\\"SECURITYSHORTNAME\\\":\\\"三人行\\\",\\\"REPORTTYPE\\\":\\\"1\\\",\\\"TYPE\\\":\\\"4\\\",\\\"REPORTDATE\\\":\\\"2019/3/31 0:00:00\\\",\\\"CURRENCY\\\":\\\"人民币\\\",\\\"KCFJCXSYJLR\\\":\\\"15145075.8\\\",\\\"KCFJCXSYJLR_YOY\\\":\\\"\\\",\\\"TOTALOPERATEREVE\\\":\\\"324333974.8\\\",\\\"TOTALOPERATEREVE_YOY\\\":\\\"\\\",\\\"OPERATEREVE\\\":\\\"324333974.8\\\",\\\"OPERATEREVE_YOY\\\":\\\"\\\",\\\"INTREVE\\\":\\\"\\\",\\\"INTREVE_YOY\\\":\\\"\\\",\\\"PREMIUMEARNED\\\":\\\"\\\",\\\"PREMIUMEARNED_YOY\\\":\\\"\\\",\\\"COMMREVE\\\":\\\"\\\",\\\"COMMREVE_YOY\\\":\\\"\\\",\\\"OTHERREVE\\\":\\\"\\\",\\\"OTHERREVE_YOY\\\":\\\"\\\",\\\"TOTALOPERATEEXP\\\":\\\"304419904.19\\\",\\\"TOTALOPERATEEXP_YOY\\\":\\\"\\\",\\\"OPERATEEXP\\\":\\\"270567800.94\\\",\\\"OPERATEEXP_YOY\\\":\\\"\\\",\\\"INTEXP\\\":\\\"\\\",\\\"INTEXP_YOY\\\":\\\"\\\",\\\"COMMEXP\\\":\\\"\\\",\\\"COMMEXP_YOY\\\":\\\"\\\",\\\"RDEXP\\\":\\\"295282.42\\\",\\\"RDEXP_YOY\\\":\\\"\\\",\\\"SURRENDERPREMIUM\\\":\\\"\\\",\\\"SURRENDERPREMIUM_YOY\\\":\\\"\\\",\\\"NETINDEMNITYEXP\\\":\\\"\\\",\\\"NETINDEMNITYEXP_YOY\\\":\\\"\\\",\\\"NETCONTACTRESERVE\\\":\\\"\\\",\\\"NETCONTACTRESERVE_YOY\\\":\\\"\\\",\\\"POLICYDIVIEXP\\\":\\\"\\\",\\\"POLICYDIVIEXP_YOY\\\":\\\"\\\",\\\"RIEXP\\\":\\\"\\\",\\\"RIEXP_YOY\\\":\\\"\\\",\\\"OTHEREXP\\\":\\\"\\\",\\\"OTHEREXP_YOY\\\":\\\"\\\",\\\"OPERATETAX\\\":\\\"1458897.99\\\",\\\"OPERATETAX_YOY\\\":\\\"\\\",\\\"SALEEXP\\\":\\\"23794480.78\\\",\\\"SALEEXP_YOY\\\":\\\"\\\",\\\"MANAGEEXP\\\":\\\"7757769.24\\\",\\\"MANAGEEXP_YOY\\\":\\\"\\\",\\\"FINANCEEXP\\\":\\\"545672.82\\\",\\\"FINANCEEXP_YOY\\\":\\\"\\\",\\\"ASSETDEVALUELOSS\\\":\\\"\\\",\\\"ASSETDEVALUELOSS_YOY\\\":\\\"\\\",\\\"FVALUEINCOME\\\":\\\"\\\",\\\"FVALUEINCOME_YOY\\\":\\\"\\\",\\\"INVESTINCOME\\\":\\\"\\\",\\\"INVESTINCOME_YOY\\\":\\\"\\\",\\\"INVESTJOINTINCOME\\\":\\\"\\\",\\\"INVESTJOINTINCOME_YOY\\\":\\\"\\\",\\\"EXCHANGEINCOME\\\":\\\"\\\",\\\"EXCHANGEINCOME_YOY\\\":\\\"\\\",\\\"OPERATEPROFIT\\\":\\\"18256404.57\\\",\\\"OPERATEPROFIT_YOY\\\":\\\"\\\",\\\"NONOPERATEREVE\\\":\\\"280\\\",\\\"NONOPERATEREVE_YOY\\\":\\\"\\\",\\\"NONLASSETREVE\\\":\\\"\\\",\\\"NONLASSETREVE_YOY\\\":\\\"\\\",\\\"NONOPERATEEXP\\\":\\\"\\\",\\\"NONOPERATEEXP_YOY\\\":\\\"\\\",\\\"NONLASSETNETLOSS\\\":\\\"\\\",\\\"NONLASSETNETLOSS_YOY\\\":\\\"\\\",\\\"SUMPROFIT\\\":\\\"18256684.57\\\",\\\"SUMPROFIT_YOY\\\":\\\"\\\",\\\"INCOMETAX\\\":\\\"2976608.77\\\",\\\"INCOMETAX_YOY\\\":\\\"\\\",\\\"NETPROFIT\\\":\\\"15280075.8\\\",\\\"NETPROFIT_YOY\\\":\\\"\\\",\\\"COMBINEDNETPROFITB\\\":\\\"\\\",\\\"COMBINEDNETPROFITB_YOY\\\":\\\"\\\",\\\"PARENTNETPROFIT\\\":\\\"15280075.8\\\",\\\"PARENTNETPROFIT_YOY\\\":\\\"\\\",\\\"MINORITYINCOME\\\":\\\"\\\",\\\"MINORITYINCOME_YOY\\\":\\\"\\\",\\\"BASICEPS\\\":\\\"0.29\\\",\\\"BASICEPS_YOY\\\":\\\"\\\",\\\"DILUTEDEPS\\\":\\\"\\\",\\\"DILUTEDEPS_YOY\\\":\\\"\\\",\\\"OTHERCINCOME\\\":\\\"\\\",\\\"OTHERCINCOME_YOY\\\":\\\"\\\",\\\"PARENTOTHERCINCOME\\\":\\\"\\\",\\\"PARENTOTHERCINCOME_YOY\\\":\\\"\\\",\\\"MINORITYOTHERCINCOME\\\":\\\"\\\",\\\"MINORITYOTHERCINCOME_YOY\\\":\\\"\\\",\\\"SUMCINCOME\\\":\\\"15280075.8\\\",\\\"SUMCINCOME_YOY\\\":\\\"\\\",\\\"PARENTCINCOME\\\":\\\"15280075.8\\\",\\\"PARENTCINCOME_YOY\\\":\\\"\\\",\\\"MINORITYCINCOME\\\":\\\"\\\",\\\"MINORITYCINCOME_YOY\\\":\\\"\\\"},{\\\"SECURITYCODE\\\":\\\"605168.SH\\\",\\\"SECURITYSHORTNAME\\\":\\\"三人行\\\",\\\"REPORTTYPE\\\":\\\"1\\\",\\\"TYPE\\\":\\\"4\\\",\\\"REPORTDATE\\\":\\\"2018/12/31 0:00:00\\\",\\\"CURRENCY\\\":\\\"人民币\\\",\\\"KCFJCXSYJLR\\\":\\\"120611694.58\\\",\\\"KCFJCXSYJLR_YOY\\\":\\\"29.3295260825616\\\",\\\"TOTALOPERATEREVE\\\":\\\"1099511210.29\\\",\\\"TOTALOPERATEREVE_YOY\\\":\\\"44.9740739610369\\\",\\\"OPERATEREVE\\\":\\\"1099511210.29\\\",\\\"OPERATEREVE_YOY\\\":\\\"44.9740739610369\\\",\\\"INTREVE\\\":\\\"\\\",\\\"INTREVE_YOY\\\":\\\"\\\",\\\"PREMIUMEARNED\\\":\\\"\\\",\\\"PREMIUMEARNED_YOY\\\":\\\"\\\",\\\"COMMREVE\\\":\\\"\\\",\\\"COMMREVE_YOY\\\":\\\"\\\",\\\"OTHERREVE\\\":\\\"\\\",\\\"OTHERREVE_YOY\\\":\\\"\\\",\\\"TOTALOPERATEEXP\\\":\\\"954894862.45\\\",\\\"TOTALOPERATEEXP_YOY\\\":\\\"48.6164027587101\\\",\\\"OPERATEEXP\\\":\\\"820944672.92\\\",\\\"OPERATEEXP_YOY\\\":\\\"48.7771925705588\\\",\\\"INTEXP\\\":\\\"\\\",\\\"INTEXP_YOY\\\":\\\"\\\",\\\"COMMEXP\\\":\\\"\\\",\\\"COMMEXP_YOY\\\":\\\"\\\",\\\"RDEXP\\\":\\\"3513813.16\\\",\\\"RDEXP_YOY\\\":\\\"89.3389957288854\\\",\\\"SURRENDERPREMIUM\\\":\\\"\\\",\\\"SURRENDERPREMIUM_YOY\\\":\\\"\\\",\\\"NETINDEMNITYEXP\\\":\\\"\\\",\\\"NETINDEMNITYEXP_YOY\\\":\\\"\\\",\\\"NETCONTACTRESERVE\\\":\\\"\\\",\\\"NETCONTACTRESERVE_YOY\\\":\\\"\\\",\\\"POLICYDIVIEXP\\\":\\\"\\\",\\\"POLICYDIVIEXP_YOY\\\":\\\"\\\",\\\"RIEXP\\\":\\\"\\\",\\\"RIEXP_YOY\\\":\\\"\\\",\\\"OTHEREXP\\\":\\\"\\\",\\\"OTHEREXP_YOY\\\":\\\"\\\",\\\"OPERATETAX\\\":\\\"7972559.48\\\",\\\"OPERATETAX_YOY\\\":\\\"-26.4185940225771\\\",\\\"SALEEXP\\\":\\\"89250915.22\\\",\\\"SALEEXP_YOY\\\":\\\"63.6940360812323\\\",\\\"MANAGEEXP\\\":\\\"30986631.47\\\",\\\"MANAGEEXP_YOY\\\":\\\"46.9736670878841\\\",\\\"FINANCEEXP\\\":\\\"2226270.2\\\",\\\"FINANCEEXP_YOY\\\":\\\"-8.44091926366579\\\",\\\"ASSETDEVALUELOSS\\\":\\\"\\\",\\\"ASSETDEVALUELOSS_YOY\\\":\\\"\\\",\\\"FVALUEINCOME\\\":\\\"\\\",\\\"FVALUEINCOME_YOY\\\":\\\"\\\",\\\"INVESTINCOME\\\":\\\"\\\",\\\"INVESTINCOME_YOY\\\":\\\"\\\",\\\"INVESTJOINTINCOME\\\":\\\"\\\",\\\"INVESTJOINTINCOME_YOY\\\":\\\"\\\",\\\"EXCHANGEINCOME\\\":\\\"\\\",\\\"EXCHANGEINCOME_YOY\\\":\\\"\\\",\\\"OPERATEPROFIT\\\":\\\"147978701.88\\\",\\\"OPERATEPROFIT_YOY\\\":\\\"25.2672264816477\\\",\\\"NONOPERATEREVE\\\":\\\"\\\",\\\"NONOPERATEREVE_YOY\\\":\\\"\\\",\\\"NONLASSETREVE\\\":\\\"\\\",\\\"NONLASSETREVE_YOY\\\":\\\"\\\",\\\"NONOPERATEEXP\\\":\\\"541343.83\\\",\\\"NONOPERATEEXP_YOY\\\":\\\"5313.4383\\\",\\\"NONLASSETNETLOSS\\\":\\\"\\\",\\\"NONLASSETNETLOSS_YOY\\\":\\\"\\\",\\\"SUMPROFIT\\\":\\\"147437358.05\\\",\\\"SUMPROFIT_YOY\\\":\\\"24.8185130617543\\\",\\\"INCOMETAX\\\":\\\"23406808.09\\\",\\\"INCOMETAX_YOY\\\":\\\"14.9719763198756\\\",\\\"NETPROFIT\\\":\\\"124030549.96\\\",\\\"NETPROFIT_YOY\\\":\\\"26.8690170057818\\\",\\\"COMBINEDNETPROFITB\\\":\\\"\\\",\\\"COMBINEDNETPROFITB_YOY\\\":\\\"\\\",\\\"PARENTNETPROFIT\\\":\\\"124030549.96\\\",\\\"PARENTNETPROFIT_YOY\\\":\\\"26.8690170057818\\\",\\\"MINORITYINCOME\\\":\\\"\\\",\\\"MINORITYINCOME_YOY\\\":\\\"\\\",\\\"BASICEPS\\\":\\\"2.39\\\",\\\"BASICEPS_YOY\\\":\\\"26.4550264550265\\\",\\\"DILUTEDEPS\\\":\\\"2.39\\\",\\\"DILUTEDEPS_YOY\\\":\\\"26.4550264550265\\\",\\\"OTHERCINCOME\\\":\\\"\\\",\\\"OTHERCINCOME_YOY\\\":\\\"\\\",\\\"PARENTOTHERCINCOME\\\":\\\"\\\",\\\"PARENTOTHERCINCOME_YOY\\\":\\\"\\\",\\\"MINORITYOTHERCINCOME\\\":\\\"\\\",\\\"MINORITYOTHERCINCOME_YOY\\\":\\\"\\\",\\\"SUMCINCOME\\\":\\\"124030549.96\\\",\\\"SUMCINCOME_YOY\\\":\\\"26.8690170057818\\\",\\\"PARENTCINCOME\\\":\\\"124030549.96\\\",\\\"PARENTCINCOME_YOY\\\":\\\"26.8690170057818\\\",\\\"MINORITYCINCOME\\\":\\\"\\\",\\\"MINORITYCINCOME_YOY\\\":\\\"\\\"}]\"";
//        logger.info("get data = {}", data);
        data = StringEscapeUtils.unescapeJava(data);
        logger.info("get data unescapeJava = {}", data);
        if (data.startsWith("\"")) {
            data = data.substring(1, data.length() - 1);
            logger.info("get data unescapeJava2 = {}", data);
        }

        JSONObject origJsonObject = JSONObject.parseObject(data);
        JSONArray jsonArray = origJsonObject.getJSONArray("lr0");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            BfbVo bfbVo = new BfbVo();

            {
                String pStr = jsonObject.getString("date");
                List<String> date = StringUtil.getmutiString("([0-9]{4})-([0-9]{1,2})", pStr);
                bfbVo.setDate(String.format("%s%02d", date.get(0), Long.valueOf(date.get(1))));
            }
            {
                String pStr = jsonObject.getString("yysr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setYysr(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setYysr(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setYysr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("yysjjfj");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setYysjjfj(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setYysjjfj(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setYysjjfj(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("qjfy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setQjfy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setQjfy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setQjfy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("xsfy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setXsfy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setXsfy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setXsfy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("glfy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setGlfy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setGlfy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setGlfy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("cwfy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setCwfy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setCwfy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setCwfy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("zcjzss");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setZcjzss(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setZcjzss(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setZcjzss(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("qtjysy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setQtjysy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setQtjysy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setQtjysy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("gyjzbdsy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setGyjzbdsy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setGyjzbdsy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setGyjzbdsy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("tzsy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setTzsy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setTzsy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setTzsy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("yylr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setYylr(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setYylr(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setYylr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("yywsr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setYywsr(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setYywsr(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setYywsr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("btsr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setBtsr(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setBtsr(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setBtsr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("yywzc");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setYywzc(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setYywzc(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setYywzc(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("lrze");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setLrze(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setLrze(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setLrze(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("sds");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setSds(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setSds(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setSds(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("jlr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setJlr(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setJlr(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setJlr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }


            bfbVoList.add(bfbVo);

        }

        bfbVoList = bfbVoList.stream().sorted(Comparator.comparing(BfbVo::getDate).reversed()).collect(Collectors.toList());
        gloableData.allStockList.get(code).setBfbVoList(bfbVoList);
    }
}
