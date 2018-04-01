package com.joker.applet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.joker.common.util.ChrysanthemumCodeUtils;
import com.joker.common.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author xu
 * @date 2018/4/1
 */
@Slf4j
@Controller
@RequestMapping("/applet")
public class AppletChrysanthemumCode {

    /**
     * 获取小程序的菊花码并保存到指定位置
     * @param id 二维码携带的参数
     * @param path 二维码访问的小程序路径(如果固定的话也可以不用前端传递后端直接配置)
     */
    @RequestMapping(value = "/chrysanthemumCode", method = RequestMethod.GET)
    @ResponseBody
    public void chrysanthemumCode (String id,String path){
        //获取tokenURL
        String tokenURL = ChrysanthemumCodeUtils.accessToken();
        //获取获取到access_token(Get请求可以直接获取可以直接调用login里的httpGet方法,如果感觉不美观的话,可以静此方法提取到一个公共的util中)
        String token = LoginUtils.httpGet(tokenURL);
        //使用json解析获取accessToken
        JSONObject json = JSON.parseObject(token);
        String accessToken = (String) json.get("access_token");
        //获取菊花码的URL
        String codeURL = ChrysanthemumCodeUtils.wxCodeURL(accessToken);
        //获取菊花码保存到指定路径
        ChrysanthemumCodeUtils.chrysanthemumCode(codeURL,id,path);
    }
}
