package com.xupt.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

/**
 * HDFS工具类
 */
public class HDFSUtils {
    public static final String PREFIX = "/videoRec";

    /**
      * 从hdfs将指定文件copy到本地文件夹
      * @param conf
      */
    public static void copyFileToLocal(Configuration conf, String src,String dest) {
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
            fs.copyToLocalFile(false,new Path(src),new Path(dest));
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                fs.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * 上传本地文件
     * @param conf
     */
    public static void uploadFile(Configuration conf,String src,String dest){
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
            fs.copyFromLocalFile(new Path(src),new Path(dest));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建文件夹
     * @param conf conf
     * @param path path
     */
    public static void creatFolder(Configuration conf,String path){
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
            if (!fs.exists(new Path(path))){
                fs.mkdirs(new Path(path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
