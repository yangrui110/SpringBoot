package com.demo.web.util.http;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * @autor 杨瑞
 * @date 2019/5/14 12:42
 */
public class HeaderType {

    /**
     * 设置response的头部信息为可直接下载的数据流信息
     * */
    public static void setResponseFile(String name,HttpServletResponse response) throws UnsupportedEncodingException {
        response.setContentType("application/octet-stream;charset=ISO8859-1");
        response.setHeader("Content-Disposition", "attachment;filename="+new String(name.getBytes(),"iso-8859-1"));
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
    }
}
