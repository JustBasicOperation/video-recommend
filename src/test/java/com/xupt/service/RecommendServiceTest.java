package com.xupt.service;

import com.xupt.entity.Article;
import com.xupt.mapper.ArticleMapper;
import com.xupt.util.HDFSUtils;
import com.xupt.util.HttpUtil;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecommendServiceTest {

    @Resource
    public RecommendService recommendService;

    @Resource
    public ArticleMapper articleMapper;

    @Test
    public void test01(){
        try {
            recommendService.recommend();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test02(){
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://8.131.121.224:8082");
        HDFSUtils.uploadFile(conf,"D:\\HDFSTest\\item.csv","/videoRec");
    }

    @Test
    public void test03(){
        String s = HttpUtil.get("https://www.toutiao.com/");
        System.out.println(s);
    }

    @Test
    public void test04(){
        List<Article> articles = articleMapper.selectList(null);
        System.out.println(articles);
    }
}