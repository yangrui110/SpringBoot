package com.demo.web.core.xmlUtil;

import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @autor 杨瑞
 * @date 2019/5/11 17:23
 */
public class XmlUtil {
    /**
     * <p>读取实体定义文件</p>
     * */
    public static String getXml(Resource resource) throws IOException {
        InputStream inputStream=resource.getInputStream();
        byte[] bys=new byte[1024];
        int len=-1;
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        while ((len=inputStream.read(bys))!=-1){
            out.write(bys, 0, len);
        }
        byte[] result=out.toByteArray();
        return new String(result,"utf-8");
    }


}
