package com.joker.applet;

import com.joker.common.util.UUIDUtils;
import com.joker.common.util.WithdrawalsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/applet")
public class AppletWithdrawals {
    /**
     * 提现
     * @param money 提现金额
     */
    @RequestMapping(value = "/withdrawals", method = RequestMethod.POST)
    @ResponseBody
    public void withdrawals(double money, HttpServletRequest request) {
        //提现订单号此处我直接用了UUID,感觉当前时间的毫秒数然后加随机数好点
        String partnerTradeNo = UUIDUtils.getUUID();
        //用户的openId
        String openId = "用户的openId";
        //将提现金额转换为string
        String withdrawMoneys = String.valueOf((int) (money * 100));
        //调用提现方法并接收返回值
        Map<String, Object> map = WithdrawalsUtils.withdrawal(request, withdrawMoneys, partnerTradeNo, openId);
        if ("SUCCESS".equals(map.get("return_code"))) {
            if ("FAIL".equals(map.get("result_code"))) {
                log.info(map.get("err_code_des") + "!");
            } else if ("SUCCESS".equalsIgnoreCase((String) map.get("result_code"))) {
                map.put("suc", "提现成功!");
            }
        }
    }
}
