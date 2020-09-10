package com.xq.fin.analyser.model.po;

import com.xq.fin.analyser.model.po.key.CodeTimeKey;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@ToString
@Data
@Table(name = "gp_xjll")
@IdClass(CodeTimeKey.class)
public class XjllPo implements Serializable {
    private static final long serialVersionUID = 2L;
    @Id
    private String code;    //代码
    @Id
    private String time;    //时间：202003，202006

    double SALEGOODSSERVICEREC;         //销售商品、提供劳务收到的现金
    double OTHEROPERATEREC;             //收到其他与经营活动有关的现金
    double SUMOPERATEFLOWIN;        //经营活动现金流入小计
    double BUYGOODSSERVICEPAY;          //购买商品、接受劳务支付的现金
    double EMPLOYEEPAY;                 //支付给职工以及为职工支付的现金
    double TAXPAY;                      //支付的各项税费
    double OTHEROPERATEPAY;             //支付其他与经营活动有关的现金
    double SUMOPERATEFLOWOUT;       //经营活动现金流出小计;
    double NETOPERATECASHFLOW;      //经营活动产生的现金流量净额

    double DISPFILASSETREC;             //处置固定资产、无形资产和其他长期资产收回的现金净额
    double SUMINVFLOWIN;            //投资活动现金流入小计
    double BUYFILASSETPAY;              //购建固定资产、无形资产和其他长期资产支付的现金
    double INVPAY;                      //投资支付的现金
    double SUMINVFLOWOUT;           //投资活动现金流出小计
    double NETINVCASHFLOW;          //投资活动产生的现金流量净额

    double ACCEPTINVREC;                //吸收投资收到的现金
    double LOANREC;                     //取得借款收到的现金
    double SUMFINAFLOWIN;           //筹资活动现金流入小计
    double REPAYDEBTPAY;                //偿还债务支付的现金
    double DIVIPROFITORINTPAY;          //分配股利、利润或偿付利息支付的现金
    double OTHERFINAPAY;                //支付其他与筹资活动有关的现金
    double SUMFINAFLOWOUT;          //筹资活动现金流出小计
    double NETFINACASHFLOW;         //筹资活动产生的现金流量净额

    double NICASHEQUI;              //现金及现金等价物净增加额
    double CASHEQUIBEGINNING;       //加:期初现金及现金等价物余额
    double CASHEQUIENDING;          //期末现金及现金等价物余额
}
