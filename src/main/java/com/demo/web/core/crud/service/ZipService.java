package com.demo.web.core.crud.service;

import com.demo.web.core.xmlUtil.XmlUtil;
import com.demo.web.util.file.FileUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @autor 杨瑞
 * @date 2019/6/24 20:17
 */
public class ZipService {

    /**
     * 1.读取路径下的样板文件为string字符串
     * 2.替换其中的通用字符
     * 3.生成byteArrayOutputstream,并压缩
     * */

    public static void readFile() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        ZipOutputStream zipOutputStream=new ZipOutputStream(new FileOutputStream(new File("C:\\所有项目\\base.zip")));
        try {
            Resource[] resources = resolver.getResources("classpath:htmlTemplates/*");
            for (Resource resource:resources){
                File resourceFile = resource.getFile();
                if (resourceFile.isDirectory()&&resourceFile.getName().equals("template")){
                    File[] files = resourceFile.listFiles();
                    for(File file:files){
                        if(file.isDirectory()){
                            File[] files1 = file.listFiles();
                            for(File f:files1){
                                ByteArrayInputStream inputStream=new ByteArrayInputStream(FileUtil.getFileString(f).getBytes());
                                zipOutputStream.putNextEntry(new ZipEntry(resourceFile.getName()+"/"+file.getName()+"/"+f.getName()));
                                int len=-1;
                                byte[] bys=new byte[1024];
                                while((len=inputStream.read(bys))!=-1){
                                    zipOutputStream.write(bys, 0, len);
                                }
                                inputStream.close();
                                zipOutputStream.closeEntry();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        zipOutputStream.close();
    }

    public static void main(String[] args) throws IOException {
        readFile();
    }
}
