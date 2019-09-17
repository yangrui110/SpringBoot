package com.yangframe.web.util.file;

import java.io.*;

/**
 * @autor 杨瑞
 * @date 2019/6/26 12:56
 */
public class FileUtil {

    public static ByteArrayOutputStream readFile(File file) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        byte[] bys = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(bys)) != -1) {
            out.write(bys, 0, len);
        }
        return out;
    }

    public static byte[] readFileToByte(InputStream inputStream) throws IOException {
        BufferedInputStream inputStream1 = new BufferedInputStream(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bys = new byte[1024];
        while (inputStream1.read(bys) != -1) {
            outputStream.write(bys, 0, bys.length);
        }
        byte[] bs = outputStream.toByteArray();
        //文件字节数组
        outputStream.close();
        inputStream1.close();
        return bs;
    }

    public static String readCharactor(Reader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        char[] charBuf = new char[4096];
        for(int i = reader.read(charBuf);i>0;i = reader.read(charBuf)){
            builder.append(charBuf,0,i);
        }
        return builder.toString();
    }

    public static String getFileString(File file) throws IOException {
        ByteArrayOutputStream out = readFile(file);

        String s = new String(out.toByteArray(), "utf-8");
        out.close();
        return s;
    }

}
