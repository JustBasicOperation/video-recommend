package com.xupt.util;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

public class HDFSUtilsTest {

    @Test
    public void testUpload(){
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://8.131.121.224:8082");
        HDFSUtils.uploadFile(conf,"D:\\HDFSTest","/videoRec");
    }

    @Test
    public void testDownload(){
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://8.131.121.224:8082");
        HDFSUtils.copyFileToLocal(conf,"/videoRec/HDFSTest","D:\\HDFSTest");
    }


}