package com.joker.applet;

import com.joker.common.util.OrderUtils;
import com.joker.common.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author xu
 * @date 2018/3/25
 */
@Slf4j
@Controller
@RequestMapping("/applet")
public class AppletPay {

    /**
     * 下单接口
     * @param pay 支付金额
     * @return 前端调用支付需要的数据
     */
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    @ResponseBody
    public Map<String ,Object> getPay(double pay, HttpServletRequest request) throws Exception {
        String openID = "登录的时候获取的用户的openID";
        //应为当时做的是红包系统,所以定义为口令费用
        String body = "红包费用";
        //将需要支付的费用转化为以分为单位的字符串
        String payMongy = Integer.toString((int)(pay * 100));
        //订单编号(暂时用的是UUID随机获取的字符串,其实可以以当前时间的毫秒值加随机字符串就跟不会重复了)
        String outTradeNo = UUIDUtils.getUUID();
        Map<String ,Object> map =OrderUtils.getOrder(outTradeNo,openID,payMongy,body,request);
        return map;
    }
}
