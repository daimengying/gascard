package com.lirong.gascard.utils;

import cn.hutool.core.io.IoUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: daimengying
 * @Date: 2018/6/6 09:27
 * @Description:
 */
public class HttpUtil {

    public static String getJsonString(HttpServletRequest request)
            throws IOException {
        request.setCharacterEncoding("utf-8");
        InputStream in = request.getInputStream();
        BufferedReader buff = IoUtil.getReader(in, "UTF-8");
        StringBuffer sb = new StringBuffer();
        String s = null;
        while ((s = buff.readLine()) != null) {
            sb.append(s);
        }
        buff.close();
        in.close();
        return sb.toString();
    }
}
