package com.yangframe.config.whiteList;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WhiteList {
    private static List<String> pathWhiteLists;

    private static List<String> ipWhiteLists;

    public static List<String> getPathWhiteLists() {
        return pathWhiteLists;
    }

    public static void setPathWhiteLists(List<String> pathWhiteLists) {
        WhiteList.pathWhiteLists = pathWhiteLists;
    }

    public static List<String> getIpWhiteLists() {
        return ipWhiteLists;
    }

    public static void setIpWhiteLists(List<String> ipWhiteLists) {
        WhiteList.ipWhiteLists = ipWhiteLists;
    }

    public static void initWhiteLists() throws IOException {
        pathWhiteLists = readPathWhiteList();
        ipWhiteLists=readIpWhiteList();
    }

    /**
     * 读取路径白名单
     * */
    private static List<String> readPathWhiteList() throws IOException {
        ClassPathResource pathResource = new ClassPathResource("interceptor/pathWhiteList");
        return readSource(pathResource);
    }

    /**
     * 读取路径白名单
     * */
    private static List<String> readIpWhiteList() throws IOException {
        ClassPathResource pathResource = new ClassPathResource("interceptor/ipBlackList");
        return readSource(pathResource);
    }

    /**
     * 读取资源
     * */
    private static List<String> readSource(Resource resource) throws IOException {
        List<String> result =new ArrayList<>();
        BufferedReader reader =new BufferedReader(new FileReader(resource.getFile()));
        String bs = null;
        while((bs=reader.readLine())!=null){
            //剪切掉#到行结尾的数据
            String substring = bs.substring(0, bs.indexOf("#")==-1?bs.length():bs.lastIndexOf("#"));
            if(!"".equals(substring)){
                result.add(bs);
            }
        }
        return result;
    }

}
