package com.demo.config.index;

import com.demo.config.properties.UploadFileProperties;
import com.demo.config.util.Util;
import com.demo.web.util.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/7/13 21:56
 */
@Controller
@RequestMapping("file")
public class FileController {

    Logger logger=LoggerFactory.getLogger(FileController.class);

    @Autowired
    private UploadFileProperties uploadFileProperties;

    @ResponseBody
    @PostMapping("upload")
    public Map uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        String originName=file.getOriginalFilename();
        String fileName=ensureFile(originName);

        BufferedInputStream inputStream=new BufferedInputStream(file.getInputStream());
        BufferedOutputStream outputStream=new BufferedOutputStream(new FileOutputStream(new File(uploadFileProperties.getFilePath()+fileName)));
        byte[] bys=new byte[1024];
        int len=-1;
        while((len=inputStream.read(bys))!=-1) {
            outputStream.write(bys,0,len);
        }
        outputStream.close();
        inputStream.close();
        Map<String,Object> map=new HashMap<>();
        map.put("filePath","file/"+fileName);
        map.put("fileOriginalName", originName);
        return map;
    }

    private String ensureFile(String originName) throws Exception {
        int a=originName.lastIndexOf(".");
        String sufix="";
        if(a!=-1)
            sufix=originName.substring(a);
        else
            sufix=originName;
        sufix= Util.getRandUUID()+sufix;
        String basePath=uploadFileProperties.getFilePath();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String format = dateFormat.format(new Date());
        File base=new File(basePath+format+"\\");
        if(!base.exists())
            base.mkdirs();
        String fileName=basePath+sufix;
        File files=new File(fileName);
        if(!files.exists())
            files.createNewFile();
        return format+"/"+sufix;
    }

    @ResponseBody
    @GetMapping("download/{date}/{path}")
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.info("pathInfo="+request.getServletPath());
        String path=request.getServletPath().replaceAll("/file/download","");
        String pathname=uploadFileProperties.getFilePath()+path;
        InputStream inputStream= new FileInputStream(new File(pathname));
        byte[] bs=FileUtil.readFileToByte(inputStream);
        int d=path.lastIndexOf(".");
        String type=path.substring(d);
        //文件字节数组
        response.setHeader("Content-Disposition","attachment;filename="+ Util.getRandUUID()+"."+type);
        response.setHeader("Content-Length", "" + bs.length);
        // 获取响应的对象流
        OutputStream outputStream1 = response.getOutputStream();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        BufferedOutputStream toOut = new BufferedOutputStream(outputStream1);
        toOut.write(bs);
        toOut.flush();
        toOut.close();
    }
}
