package com.yangframe.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @autor 杨瑞
 * @date 2019/7/13 22:07
 */
@Data
@Configuration
public class UploadFileProperties {

    @Value("${uploadFile.path}")
    private String filePath;

    @Override
    public String toString() {
        return "UploadFileProperties{" +
                "filePath='" + filePath + '\'' +
                '}';
    }
}
