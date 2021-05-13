package com.xupt.util;

import org.apache.hadoop.conf.Configuration;
import org.junit.Test;

import java.util.TimeZone;

public class HDFSUtilsTest {

    @Test
    public void testUpload() {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://8.131.121.224:8082");
        HDFSUtils.uploadFile(conf,"D:\\HDFSTest","/videoRec");
    }

    @Test
    public void testDownload() {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://8.131.121.224:8082");
        HDFSUtils.copyFileToLocal(conf,"/videoRec/HDFSTest","D:\\HDFSTest");
    }

    @Test
    public void test01() {
        String property = System.getProperty("user.timezone");
        System.out.println(TimeZone.getDefault());
    }

    @Test
    public void test02() {
        for (int i = 0;i < 10;i++) {
            double random = Math.random();
            System.out.println((int)(random*10));
        }
    }

}