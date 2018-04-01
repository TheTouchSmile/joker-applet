package com.joker.common.config;

/**
 * @author xu
 * @date 2018/3/24
 */
public class Configure {

    //小程序ID
    public static final String APP_ID = "小程序ID";
    //商户号
    public static final String MCH_ID = "商户ID";
    //key
    public static final String MCH_SECRET = "商户号KEY";
    //小程序的秘钥
    public static final String APP_SECRET = "小程序秘钥";
    //获取openID的json的接口
    public static final String WEB_ACCESS_TO_KEN_HTTPS = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    //支付成功后的服务器回调url
    public static final String NOTIFY_URL = "https://www.";
    //签名方式，固定值
    public static final String SIGNTYPE = "MD5";
    //交易类型，小程序支付的固定值为JSAPI
    public static final String TRADETYPE = "JSAPI";
    //微信统一下单接口地址
    public static final String PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //提现的接口
    public static final String WITHDRAWALS_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    //获取菊花码生成access_token生成接口
    public static final String WX_ACCESS_TOKEN="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    //获取菊花码本人用的是B,传值
    public static final String WX_CODE = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=%s";
}
