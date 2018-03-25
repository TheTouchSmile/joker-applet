package com.joker.applet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.joker.common.util.LoginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author xu
 * @date 2018/3/22
 */
@Slf4j
@Controller
@RequestMapping("/applet")
public class AppletLogin {
    /**
     * 微信小程序的登录获取用户唯一的openID和session
     * @param code 小程序前端获取用户表示
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public void getLogin(String code){
        //传递相应的参数获取用户登录信息
        String rec = LoginUtils.getWebAccess(code);
        //使用json解析结果
        JSONObject json = JSON.parseObject(rec);
        //判断结果是否为空
        if (json != null){
            String openID = (String) json.get("openid");
            log.info("获取的openID为:"+openID);
        }
    }
}
