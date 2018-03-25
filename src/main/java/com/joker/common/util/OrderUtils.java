package com.joker.common.util;

import com.joker.common.config.Configure;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 下单
 * @author xu
 * @date 2018/3/24
 */
@Slf4j
public class OrderUtils {
    /**
     *
     * @param openID 支付用户的openID
     * @param payMoney 支付的金额
     * @param body 商品名称
     * @param outTradeNo 商户订单编号
     * @return 下单结果
     */
    public static Map<String ,Object> getOrder(String outTradeNo,String openID, String payMoney, String body, HttpServletRequest request) throws Exception {
        //生成随机数
        String noneceStr = UUIDUtils.getUUID();
        //获取客户端的ip地址(当时测试时,好像是客户ip和主机ip都可以)
        String spbillCreateIp = IpUtils.getIpAddr(request);
        //组装参数,用户生成统一下单的接口签名
        Map<String, String> packageParams = new HashMap<String, String>();
        packageParams.put("appid", Configure.APP_ID);//小程序id
        packageParams.put("body", body);//商品名称
        packageParams.put("mch_id", Configure.MCH_ID);//商户id
        packageParams.put("nonce_str", noneceStr);//随机数
        packageParams.put("notify_url", Configure.NOTIFY_URL);//支付成功后的回调地址
        packageParams.put("openid", openID);//本小程序用户的唯一id
        packageParams.put("out_trade_no", outTradeNo);//商户订单号
        packageParams.put("spbill_create_ip", spbillCreateIp);//IP地址
        packageParams.put("total_fee", payMoney);//支付金额，这边需要转成字符串类型，否则后面的签名会失败
        packageParams.put("trade_type", Configure.TRADETYPE);//支付方式
        //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String prestr = PayUtil.createLinkString(packageParams);
        //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
        String mysign = PayUtil.sign(prestr, Configure.MCH_SECRET, "utf-8").toUpperCase();
        //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
        String xml = "<xml>" + "<appid>" + Configure.APP_ID + "</appid>"
                + "<body><![CDATA[" + body + "]]></body>"
                + "<mch_id>" + Configure.MCH_ID + "</mch_id>"
                + "<nonce_str>" + noneceStr + "</nonce_str>"
                + "<notify_url>" + Configure.NOTIFY_URL + "</notify_url>"
                + "<openid>" + openID + "</openid>"
                + "<out_trade_no>" + outTradeNo + "</out_trade_no>"
                + "<spbill_create_ip>" + spbillCreateIp + "</spbill_create_ip>"
                + "<total_fee>" + payMoney + "</total_fee>"//这个地方的支付不知道不要不要转换成int(上一个小程序没哟转可以正常使用)
                + "<trade_type>" + Configure.TRADETYPE + "</trade_type>"
                + "<sign>" + mysign + "</sign>"
                + "</xml>";
        log.info("统一下单接口 请求XML数据:"+xml);
        //调用统一下单接口，并接受返回的结果
        String result = PayUtil.httpRequest(Configure.PAY_URL, "POST", xml);
        log.info("统一下单接口 换回XML数据:"+result);
        //将解析结果存储到HashMap中
        Map map = PayUtil.doXMLParse(result);
        //返回状态码
        String returnCode = (String) map.get("return_code");
        //返回给小程序端需要的参数
        Map<String, Object> response = new HashMap<String, Object>();
        //返回成功
        String success ="SUCCESS";
        //判断状态码是否正确
        if(success == returnCode || success.equalsIgnoreCase(returnCode)){
            //返回的预付单信息
            String prepayId = (String) map.get("prepay_id");
            response.put("nonceStr", noneceStr);
            response.put("package", "prepay_id=" + prepayId);
            //这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
            Long timeStamp = System.currentTimeMillis() / 1000;
            response.put("timeStamp", timeStamp + "");
            //拼接签名需要的参数
            String stringSignTemp = "appId=" + Configure.APP_ID + "&nonceStr=" + noneceStr + "&package=prepay_id=" + prepayId + "&signType=MD5&timeStamp=" + timeStamp;
            //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
            String paySign = PayUtil.sign(stringSignTemp, Configure.MCH_SECRET, "utf-8").toUpperCase();
            response.put("paySign", paySign);
        }
        response.put("appid", Configure.APP_ID);
        return response;
    }
}
