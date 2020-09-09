package com.xq.fin.analyser.tt.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.fin.analyser.data.GloableData;
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
public class XjllUtil {
    private static Logger logger = LoggerFactory.getLogger(XjllUtil.class);

    @Autowired
    private GloableData gloableData;
    @Autowired
    private RestTemplate restTemplate;

    public List<XjllVo> getData(String code) {
        List<XjllVo> xjllVoList = new ArrayList<>(10);
        String url = "http://f10.eastmoney.com/NewFinanceAnalysis/xjllbAjax?companyType=4&reportDateType=0&reportType=1&endDate=&code=" + StringUtil.getSCByCode(code);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();
        data = StringEscapeUtils.unescapeJava(data);
        if (data.startsWith("\"")) {
            data = data.substring(1, data.length() - 1);
        }

        JSONArray jsonArray = JSONObject.parseArray(data);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            XjllVo xjllVo = new XjllVo();

            xjllVo.setCode(StringUtil.getCode(jsonObject.getString("SECURITYCODE")));
            xjllVo.setTime(StringUtil.getTime(jsonObject.getString("REPORTDATE")));

            xjllVo.setSALEGOODSSERVICEREC(StringUtil.getDouble(jsonObject.getString("SALEGOODSSERVICEREC")));
            xjllVo.setOTHEROPERATEREC(StringUtil.getDouble(jsonObject.getString("OTHEROPERATEREC")));
            xjllVo.setSUMOPERATEFLOWIN(StringUtil.getDouble(jsonObject.getString("SUMOPERATEFLOWIN")));
            xjllVo.setBUYGOODSSERVICEPAY(StringUtil.getDouble(jsonObject.getString("BUYGOODSSERVICEPAY")));
            xjllVo.setEMPLOYEEPAY(StringUtil.getDouble(jsonObject.getString("EMPLOYEEPAY")));
            xjllVo.setTAXPAY(StringUtil.getDouble(jsonObject.getString("TAXPAY")));
            xjllVo.setOTHEROPERATEPAY(StringUtil.getDouble(jsonObject.getString("OTHEROPERATEPAY")));
            xjllVo.setSUMOPERATEFLOWOUT(StringUtil.getDouble(jsonObject.getString("SUMOPERATEFLOWOUT")));
            xjllVo.setNETOPERATECASHFLOW(StringUtil.getDouble(jsonObject.getString("NETOPERATECASHFLOW")));
            xjllVo.setDISPFILASSETREC(StringUtil.getDouble(jsonObject.getString("DISPFILASSETREC")));
            xjllVo.setSUMINVFLOWIN(StringUtil.getDouble(jsonObject.getString("SUMINVFLOWIN")));
            xjllVo.setBUYFILASSETPAY(StringUtil.getDouble(jsonObject.getString("BUYFILASSETPAY")));
            xjllVo.setINVPAY(StringUtil.getDouble(jsonObject.getString("INVPAY")));
            xjllVo.setSUMINVFLOWOUT(StringUtil.getDouble(jsonObject.getString("SUMINVFLOWOUT")));
            xjllVo.setNETINVCASHFLOW(StringUtil.getDouble(jsonObject.getString("NETINVCASHFLOW")));
            xjllVo.setACCEPTINVREC(StringUtil.getDouble(jsonObject.getString("ACCEPTINVREC")));
            xjllVo.setLOANREC(StringUtil.getDouble(jsonObject.getString("LOANREC")));
            xjllVo.setSUMFINAFLOWIN(StringUtil.getDouble(jsonObject.getString("SUMFINAFLOWIN")));
            xjllVo.setREPAYDEBTPAY(StringUtil.getDouble(jsonObject.getString("REPAYDEBTPAY")));
            xjllVo.setDIVIPROFITORINTPAY(StringUtil.getDouble(jsonObject.getString("DIVIPROFITORINTPAY")));
            xjllVo.setOTHERFINAPAY(StringUtil.getDouble(jsonObject.getString("OTHERFINAPAY")));
            xjllVo.setSUMFINAFLOWOUT(StringUtil.getDouble(jsonObject.getString("SUMFINAFLOWOUT")));
            xjllVo.setNETFINACASHFLOW(StringUtil.getDouble(jsonObject.getString("NETFINACASHFLOW")));
            xjllVo.setNICASHEQUI(StringUtil.getDouble(jsonObject.getString("NICASHEQUI")));
            xjllVo.setCASHEQUIBEGINNING(StringUtil.getDouble(jsonObject.getString("CASHEQUIBEGINNING")));
            xjllVo.setCASHEQUIENDING(StringUtil.getDouble(jsonObject.getString("CASHEQUIENDING")));

            xjllVoList.add(xjllVo);
        }

        return xjllVoList;
    }
}
