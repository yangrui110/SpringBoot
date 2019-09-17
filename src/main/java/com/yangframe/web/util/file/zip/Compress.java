package com.yangframe.web.util.file.zip;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @autor 杨瑞
 * @date 2019/6/24 19:55
 */
public class Compress {

    /**
     * 压缩某个输入流
     * @param inputStream 输入流
     * @param zipOutputStream 添加到的压缩文件
     * @param pathFileName 压缩的文件路径
     * */
    public static void compressInputStream(InputStream inputStream, ZipOutputStream zipOutputStream, String pathFileName) throws IOException {
        ZipEntry zipEntry = new ZipEntry(pathFileName);
        zipOutputStream.putNextEntry(zipEntry);
        byte[] bys=new byte[1024];
        int len=-1;
        while ((len=inputStream.read(bys))!=-1){
            zipOutputStream.write(bys, 0, len);
        }
        zipOutputStream.closeEntry();
    }

    public static void  compressString(String str,ZipOutputStream zipOutputStream,String path) throws IOException {
        ByteArrayInputStream inputStream=new ByteArrayInputStream(str.getBytes());
        compressInputStream(inputStream, zipOutputStream,path );
    }
}
