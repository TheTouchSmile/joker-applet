package com.joker.common.util;

import com.joker.common.config.Configure;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * 登录
 * @author xu
 * @date 2018/3/22
 */
public class LoginUtils {
    /**
     * 将URL和参数进行拼接
     */
    public static String getWebAccess(String code) {
       String token = String.format(Configure.WEB_ACCESS_TO_KEN_HTTPS, Configure.APP_ID, Configure.APP_SECRET, code);
        return httpGet(token);
    }

    /**
     *
     * @param path 拼接好的小程序URL
     * @return 微信返回的数据结果
     */
    public static String httpGet(String path) {
        //判断传递是否为空
        if (path == null)
             return null;
        //定义rec接收微信的返回值
        String rec = null;
        HttpGet get = new HttpGet(path);
        try {
            HttpResponse response = HttpClients.createDefault().execute(get);
            HttpEntity entity = response.getEntity();
            rec = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rec;
    }

}
