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
public class LrbUtil {
    private static Logger logger = LoggerFactory.getLogger(LrbUtil.class);

    @Autowired
    private RestTemplate restTemplate;

    public List<LrbVo> getData(String code) {
        List<LrbVo> lrbVoList = new ArrayList<>(10);
        String url = "http://f10.eastmoney.com/NewFinanceAnalysis/lrbAjax?companyType=4&reportDateType=0&reportType=1&endDate=&code=" + StringUtil.getSCByCode(code);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();
        data = StringEscapeUtils.unescapeJava(data);
        if (data.startsWith("\"")) {
            data = data.substring(1, data.length() - 1);
        }

        JSONArray jsonArray = JSONObject.parseArray(data);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            LrbVo lrbVo = new LrbVo();

            lrbVo.setCode(StringUtil.getCode(jsonObject.getString("SECURITYCODE")));
            lrbVo.setSECURITYSHORTNAME(jsonObject.getString("SECURITYSHORTNAME"));
            lrbVo.setTime(StringUtil.getTime(jsonObject.getString("REPORTDATE")));
            lrbVo.setTOTALOPERATEREVE(StringUtil.getDouble(jsonObject.getString("TOTALOPERATEREVE")));
            lrbVo.setOPERATEREVE(StringUtil.getDouble(jsonObject.getString("OPERATEREVE")));
            lrbVo.setTOTALOPERATEEXP(StringUtil.getDouble(jsonObject.getString("TOTALOPERATEEXP")));
            lrbVo.setOPERATEEXP(StringUtil.getDouble(jsonObject.getString("OPERATEEXP")));
            lrbVo.setRDEXP(StringUtil.getDouble(jsonObject.getString("RDEXP")));
            lrbVo.setOPERATETAX(StringUtil.getDouble(jsonObject.getString("OPERATETAX")));
            lrbVo.setSALEEXP(StringUtil.getDouble(jsonObject.getString("SALEEXP")));
            lrbVo.setMANAGEEXP(StringUtil.getDouble(jsonObject.getString("MANAGEEXP")));
            lrbVo.setFINANCEEXP(StringUtil.getDouble(jsonObject.getString("FINANCEEXP")));
            lrbVo.setOPERATEPROFIT(StringUtil.getDouble(jsonObject.getString("OPERATEPROFIT")));
            lrbVo.setNONOPERATEREVE(StringUtil.getDouble(jsonObject.getString("NONOPERATEREVE")));
            lrbVo.setNONOPERATEEXP(StringUtil.getDouble(jsonObject.getString("NONOPERATEEXP")));
            lrbVo.setSUMPROFIT(StringUtil.getDouble(jsonObject.getString("SUMPROFIT")));
            lrbVo.setINCOMETAX(StringUtil.getDouble(jsonObject.getString("INCOMETAX")));
            lrbVo.setNETPROFIT(StringUtil.getDouble(jsonObject.getString("NETPROFIT")));
            lrbVo.setPARENTNETPROFIT(StringUtil.getDouble(jsonObject.getString("PARENTNETPROFIT")));
            lrbVo.setKCFJCXSYJLR(StringUtil.getDouble(jsonObject.getString("KCFJCXSYJLR")));
            lrbVo.setBASICEPS(StringUtil.getDouble(jsonObject.getString("BASICEPS")));
            lrbVo.setDILUTEDEPS(StringUtil.getDouble(jsonObject.getString("DILUTEDEPS")));
            lrbVo.setSUMCINCOME(StringUtil.getDouble(jsonObject.getString("SUMCINCOME")));
            lrbVo.setPARENTCINCOME(StringUtil.getDouble(jsonObject.getString("PARENTCINCOME")));

            lrbVoList.add(lrbVo);

        }

        return lrbVoList;
    }
}
