package com.joker.common.util;

import com.joker.common.config.Configure;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xu
 * @date 2018/4/1
 */
@Slf4j
public class ChrysanthemumCodeUtils {
    /**
     * 获取菊花token接口URL
     */
    public static String accessToken(){
        return String.format(Configure.WX_ACCESS_TOKEN,Configure.APP_ID,Configure.APP_SECRET);
    }
    /**
     * 获取微信小程序菊花码接口URL
     * @param accessToken 生成二维码所需要的token值
     */
    public static String wxCodeURL(String accessToken){
        return String.format(Configure.WX_CODE,accessToken);
    }

    /**
     *
     * @param codeURL 获取菊花码的RUL
     * @param id 带参二维码所带的参数
     * @param path 路径
     */
    public static void chrysanthemumCode(String codeURL, String id, String path) {
        //将参数放到map中然后转化为json
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("scene", id);
        map.put("path", path);
        map.put("width", 430);
        JSONObject jsons = JSONObject.fromObject(map);
        try {
            //发送post请求,指定格式为UTF-8
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(codeURL);
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
            StringEntity se = new StringEntity(jsons.toString());
            se.setContentType("application/json");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "UTF-8"));
            httpPost.setEntity(se);
            //接收返回数据
            HttpResponse response = httpClient.execute(httpPost);
            //判断接收到的返回值是否为空
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    InputStream instreams = resEntity.getContent();
                    //指定图片路径名称为传递过来的值
                    String uploadSysUrl = "E:/";
                    File saveFile = new File(uploadSysUrl + id + ".jpg");
                    // 判断这个文件（saveFile）是否存在
                    if (!saveFile.getParentFile().exists()) {
                        // 如果不存在就创建这个文件夹
                        saveFile.getParentFile().mkdirs();
                    }
                    saveToImgByInputStream(instreams, uploadSysUrl, id + ".jpg");
                }
            }
        } catch (Exception e) {
            log.error("获取菊花码错误:",e);
        }
    }

    /**
     * 图片保存到指定的位置
     * @param instreams 流数据
     * @param imgPath 图片位置
     * @param imgName 图片名称
     */
    public static void saveToImgByInputStream(InputStream instreams, String imgPath, String imgName) throws FileNotFoundException {
        File file = new File(imgPath, imgName);//可以是任何图片格式.jpg,.png等
        FileOutputStream fos = new FileOutputStream(file);
        //判断流是否为空
        if (instreams != null) {
            try {
                byte[] b = new byte[1024];
                int nRead = 0;
                while ((nRead = instreams.read(b)) != -1) {
                    fos.write(b, 0, nRead);
                }
            } catch (Exception e) {
                log.error("写入图片报错:",e);
            } finally {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
