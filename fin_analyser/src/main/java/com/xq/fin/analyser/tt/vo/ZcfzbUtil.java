package com.xq.fin.analyser.tt.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.fin.analyser.util.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class ZcfzbUtil {
    private static Logger logger = LoggerFactory.getLogger(ZcfzbUtil.class);

    @Autowired
    private RestTemplate restTemplate;


    public List<ZcfzVo> getData(String code) {
        logger.info("code=[{}]", code);
        List<ZcfzVo> zcfzVoList = new ArrayList<>(10);
        String url = "http://f10.eastmoney.com/NewFinanceAnalysis/zcfzbAjax?companyType=4&reportDateType=0&reportType=1&endDate=&code=" + StringUtil.getSCByCode(code);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();
        data = StringEscapeUtils.unescapeJava(data);
        if (data.startsWith("\"")) {
            data = data.substring(1, data.length() - 1);
        }

        JSONArray jsonArray = JSONObject.parseArray(data);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ZcfzVo zcfzVo = new ZcfzVo();

            zcfzVo.setCode(StringUtil.getCode(jsonObject.getString("SECURITYCODE")));
            zcfzVo.setTime(StringUtil.getTime(jsonObject.getString("REPORTDATE")));
            zcfzVo.setSUMLASSET(StringUtil.getDouble(jsonObject.getString("SUMLASSET")));
            zcfzVo.setMONETARYFUND(StringUtil.getDouble(jsonObject.getString("MONETARYFUND")));
            zcfzVo.setACCOUNTBILLREC(StringUtil.getDouble(jsonObject.getString("ACCOUNTBILLREC")));
            zcfzVo.setBILLREC(StringUtil.getDouble(jsonObject.getString("BILLREC")));
            zcfzVo.setACCOUNTREC(StringUtil.getDouble(jsonObject.getString("ACCOUNTREC")));
            zcfzVo.setADVANCEPAY(StringUtil.getDouble(jsonObject.getString("ADVANCEPAY")));
            zcfzVo.setTOTAL_OTHER_RECE(StringUtil.getDouble(jsonObject.getString("TOTAL_OTHER_RECE")));
            zcfzVo.setDIVIDENDREC(StringUtil.getDouble(jsonObject.getString("DIVIDENDREC")));
            zcfzVo.setOTHERREC(StringUtil.getDouble(jsonObject.getString("OTHERREC")));
            zcfzVo.setINVENTORY(StringUtil.getDouble(jsonObject.getString("INVENTORY")));
            zcfzVo.setOTHERLASSET(StringUtil.getDouble(jsonObject.getString("OTHERLASSET")));
            zcfzVo.setSUMNONLASSET(StringUtil.getDouble(jsonObject.getString("SUMNONLASSET")));
            zcfzVo.setLTREC(StringUtil.getDouble(jsonObject.getString("LTREC")));
            zcfzVo.setFIXEDASSET(StringUtil.getDouble(jsonObject.getString("FIXEDASSET")));
            zcfzVo.setINTANGIBLEASSET(StringUtil.getDouble(jsonObject.getString("INTANGIBLEASSET")));
            zcfzVo.setLTDEFERASSET(StringUtil.getDouble(jsonObject.getString("LTDEFERASSET")));
            zcfzVo.setDEFERINCOMETAXASSET(StringUtil.getDouble(jsonObject.getString("DEFERINCOMETAXASSET")));
            zcfzVo.setGOODWILL(StringUtil.getDouble(jsonObject.getString("GOODWILL")));
            zcfzVo.setSUMASSET(StringUtil.getDouble(jsonObject.getString("SUMASSET")));
            zcfzVo.setSUMLLIAB(StringUtil.getDouble(jsonObject.getString("SUMLLIAB")));
            zcfzVo.setSTBORROW(StringUtil.getDouble(jsonObject.getString("STBORROW")));
            zcfzVo.setACCOUNTPAY(StringUtil.getDouble(jsonObject.getString("ACCOUNTPAY")));
            zcfzVo.setACCOUNTBILLPAY(StringUtil.getDouble(jsonObject.getString("ACCOUNTBILLPAY")));
            zcfzVo.setSALARYPAY(StringUtil.getDouble(jsonObject.getString("SALARYPAY")));
            zcfzVo.setTAXPAY(StringUtil.getDouble(jsonObject.getString("TAXPAY")));
            zcfzVo.setTOTAL_OTHER_PAYABLE(StringUtil.getDouble(jsonObject.getString("TOTAL_OTHER_PAYABLE")));
            zcfzVo.setINTERESTPAY(StringUtil.getDouble(jsonObject.getString("INTERESTPAY")));
            zcfzVo.setDIVIDENDPAY(StringUtil.getDouble(jsonObject.getString("DIVIDENDPAY")));
            zcfzVo.setOTHERPAY(StringUtil.getDouble(jsonObject.getString("OTHERPAY")));
            zcfzVo.setNONLLIABONEYEAR(StringUtil.getDouble(jsonObject.getString("NONLLIABONEYEAR")));
            zcfzVo.setSUMNONLLIAB(StringUtil.getDouble(jsonObject.getString("SUMNONLLIAB")));
            zcfzVo.setLTACCOUNTPAY(StringUtil.getDouble(jsonObject.getString("LTACCOUNTPAY")));
            zcfzVo.setSUMLIAB(StringUtil.getDouble(jsonObject.getString("SUMLIAB")));
            zcfzVo.setSUMSHEQUITY(StringUtil.getDouble(jsonObject.getString("SUMSHEQUITY")));
            zcfzVo.setSHARECAPITAL(StringUtil.getDouble(jsonObject.getString("SHARECAPITAL")));
            zcfzVo.setCAPITALRESERVE(StringUtil.getDouble(jsonObject.getString("CAPITALRESERVE")));
            zcfzVo.setSURPLUSRESERVE(StringUtil.getDouble(jsonObject.getString("SURPLUSRESERVE")));
            zcfzVo.setRETAINEDEARNING(StringUtil.getDouble(jsonObject.getString("RETAINEDEARNING")));
            zcfzVo.setSUMPARENTEQUITY(StringUtil.getDouble(jsonObject.getString("SUMPARENTEQUITY")));
            zcfzVo.setSUMLIABSHEQUITY(StringUtil.getDouble(jsonObject.getString("SUMLIABSHEQUITY")));

            zcfzVoList.add(zcfzVo);
        }

        return zcfzVoList;
    }
}
