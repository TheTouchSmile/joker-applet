package com.joker.common.util;

import java.util.Random;
import java.util.UUID;

/**
 * 生成相应的UUID并去掉-
 *
 * @author xu
 * @date 2017.12.21
 */
public class UUIDUtils {
    /**
     * 默认32位的随机谁
     *
     * @return 32位随机字符串
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * StringUtils工具类方法(指定长度的随机数)
     * 获取一定长度的随机字符串，范围0-9，a-z
     *
     * @param length：指定字符串长度
     * @return 一定长度的随机字符串
     */
    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 解决文件重名传递
     *
     * @param fileName 文件名称
     * @return 32位随机数加文件的名称
     */
    public static String getUUIDFileName(String fileName) {
        return UUID.randomUUID().toString().replace("-", "") + "_" + fileName;
    }
}
