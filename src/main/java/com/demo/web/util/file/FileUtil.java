package com.demo.web.util.file;

import java.io.*;

/**
 * @autor 杨瑞
 * @date 2019/6/26 12:56
 */
public class FileUtil {

    public static ByteArrayOutputStream readFile(File file) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] bys=new byte[1024];
        int len=-1;
        while ((len=inputStream.read(bys))!=-1){
            out.write(bys, 0, len);
        }
        return out;
    }

    public static String getFileString(File file) throws IOException {
        ByteArrayOutputStream out= readFile(file);

        String s = new String(out.toByteArray(), "utf-8");
        out.close();
        return s;
    }
}
