package com.xupt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * HDFS配置类
 */
@Configuration
public class HDFSConfig {

    @Value("${fs.defaultFS}")
    public String DefaultFS;

    public String getDefaultFS() {
        return DefaultFS;
    }
}
