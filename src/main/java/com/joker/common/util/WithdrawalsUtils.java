package com.joker.common.util;

import com.joker.common.config.Configure;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

/**
 * 企业付款到零钱(提现)
 *
 * @author xu
 * @date 2018/4/1
 */
@Slf4j
public class WithdrawalsUtils {

    /**
     * 提现方法
     * @param withdrawalsMoney 提现金额
     * @param partnerTradeNo 商户订单号
     * @param openId 用户的唯一openId
     * @return
     */
    public static Map<String, Object> withdrawal(HttpServletRequest request,String withdrawalsMoney,String partnerTradeNo,String openId){
        //商品名称(因为是固定的所以我直接定义了,如果不是固定的可以通过前台传)
        String desc = "企业付款,零钱转入";
        //获取用户ip地址
        String spbillCreateIp = IpUtils.getIpAddr(request);
        //随机字符
        String nonceStr = UUIDUtils.getUUID();
        //组装参数
        Map<String, String> packageParams = new HashMap<String, String>();
        packageParams.put("amount", withdrawalsMoney);//支付金额，这边需要转成字符串类型，否则后面的签名会失败
        packageParams.put("check_name", "NO_CHECK");//是否强效验用户真实姓名NO_CHECK为否
        packageParams.put("desc", desc);//商品名
        packageParams.put("mch_appid", Configure.APP_ID);//小程序ID
        packageParams.put("mchid", Configure.MCH_ID);//商户id
        packageParams.put("nonce_str", nonceStr);//随机数
        packageParams.put("openid", openId);//确定用户唯一的openID
        packageParams.put("partner_trade_no", partnerTradeNo);//商户订单号
        packageParams.put("spbill_create_ip", spbillCreateIp);//用户的ip地址
        // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String prestr = PayUtil.createLinkString(packageParams);
        //用户生成统一下单接口的签名
        String mysign = PayUtil.sign(prestr, Configure.MCH_SECRET, "utf-8").toUpperCase();
        //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
        String xml = "<xml>" + "<amount>" + withdrawalsMoney + "</amount>"
                + "<check_name><![CDATA[" + "NO_CHECK" + "]]></check_name>"
                + "<desc>" + desc + "</desc>"
                + "<mch_appid>" + Configure.APP_ID + "</mch_appid>"
                + "<mchid>" + Configure.MCH_ID + "</mchid>"
                + "<nonce_str>" + nonceStr + "</nonce_str>"
                + "<openid>" + openId + "</openid>"
                + "<partner_trade_no>" + partnerTradeNo + "</partner_trade_no>"
                + "<spbill_create_ip>" + spbillCreateIp + "</spbill_create_ip>"//这个地方的支付不知道不要不要转换成int
                + "<sign>" + mysign + "</sign>"
                + "</xml>";
        log.info("提现统一接口请求的XML数据:"+xml);
        //调用统一下单接口，并接受返回的结果
        String result = httpRequest(Configure.WITHDRAWALS_URL, "POST", xml);
        log.info("提现统一接口返回的XML数据:"+result);
        //将接续结果储存到map中
        try {
            Map<String, Object> map = PayUtil.doXMLParse(result);
            return map;
        } catch (Exception e) {
            log.error("返回数据转换错误:",e);
            return null;
        }
    }

    /**
     * 发送提现请求并接收参数
     * @param requestUrl 请求的URL
     * @param requestMethod 请求方式
     * @param outputStr 请求数据
     * @return
     */
    public static String httpRequest(String requestUrl, String requestMethod, String outputStr) {
        // 创建SSLContext
        StringBuffer buffer = null;
        try {
            //创建一个keystore来管理密钥库
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //商户的CA证书
            FileInputStream instream = new FileInputStream(new File("CA证书的路径"));
            //创建PKCS12密钥访问库
            keyStore.load(instream, Configure.MCH_ID.toCharArray());
            // 构建一个SSLContext 环境
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, Configure.MCH_ID.toCharArray())
                    .build();
            SSLSocketFactory sslf = sslcontext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            //指定请求方式
            conn.setRequestMethod(requestMethod);
            //使用指定的证书
            conn.setSSLSocketFactory(sslf);
            //开启输入和输出
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            //往服务器端写内容
            if (null != outputStr) {
                OutputStream os = conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }
            // 读取服务器端返回的内容
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            log.error("提现报错:",e);
        }
        return buffer.toString();
    }
}
