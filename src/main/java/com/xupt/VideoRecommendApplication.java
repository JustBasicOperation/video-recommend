package com.xupt;

import com.xupt.util.HDFSUtils;
import org.apache.hadoop.conf.Configuration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xupt.mapper")
public class VideoRecommendApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoRecommendApplication.class,args);
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://8.131.121.224:8082");
        HDFSUtils.uploadFile(conf,"D:\\HDFSTest","/videoRec");
//        HDFSUtils.copyFileToLocal(conf,"/videoRec/HDFSTest","D:\\HDFSTest");
    }
}
