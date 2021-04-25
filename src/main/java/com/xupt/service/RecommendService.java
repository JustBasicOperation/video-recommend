package com.xupt.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xupt.constant.Constant;
import com.xupt.entity.Article;
import com.xupt.entity.PreferenceEntity;
import com.xupt.entity.User;
import com.xupt.mapper.ArticleMapper;
import com.xupt.mapper.PreferenceMapper;
import com.xupt.mapper.UserMapper;
import com.xupt.offline.HDFSDataModel;
import com.xupt.offline.ItemSimilarityToRedis;
import com.xupt.offline.UserItemSimilarityToRedis;
import com.xupt.util.JedisUtil;
import com.xupt.util.SnowFlake;
import com.xupt.vo.ClickReportVO;
import com.xupt.vo.PreferenceVO;
import com.xupt.vo.SourceVO;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.precompute.MultithreadedBatchItemSimilarities;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendService {

    @Resource
    ItemSimilarityToRedis itemSimilarityToRedis;

    @Resource
    UserItemSimilarityToRedis userItemSimilarityToRedis;

    @Resource
    ArticleMapper articleMapper;

    @Resource
    PreferenceMapper preferenceMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    KafkaTemplate kafkaTemplate;

    @Resource
    JedisUtil jedisUtil;

    public void recommend() throws IOException {
//        String filePath = "D:\\HDFSTest\\item.csv";
        File file = new File(Constant.FILE_PATH);
        HDFSDataModel dataModel = new HDFSDataModel(file);
        userItemSimilarityToRedis.redisStorage(dataModel);

        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel,
                new LogLikelihoodSimilarity(dataModel));
        MultithreadedBatchItemSimilarities threadDeal =
                new MultithreadedBatchItemSimilarities(recommender, 5);
        threadDeal.computeItemSimilarities(1, 1,
                itemSimilarityToRedis);

        try {
            userItemSimilarityToRedis.waitUtilDone();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Article> getRecommendList(String userID) {
        //如果没有推荐列表，默认取前十条数据
        if (!jedisUtil.exists(userID)) {
            List<Article> articles =
                    articleMapper.selectList(new QueryWrapper<Article>());
            return urlDecoder(articles);
        } else {
            //从redis中获取前十条数据
            Set<String> set = jedisUtil.zRevRange(userID, 0L, 10L);
            LinkedList<String> list = new LinkedList<>(set);
            List<Long> ids = list.stream().map(Long::parseLong).collect(Collectors.toList());
            List<Article> articles = articleMapper.selectBatchIds(ids);
            return urlDecoder(articles);
        }
    }

    public List<Article> urlDecoder(List<Article> articles){
        return articles.stream().map(article -> {
            Article art = new Article();
            try {
                art.setUrl(URLDecoder.decode(article.getUrl(),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            art.setId(article.getId());
            art.setTitle(article.getTitle());
            art.setCreated(article.getCreated());
            return art;
        }).collect(Collectors.toList());
    }

    public void reportHistory(ClickReportVO vo) {
        //1.点击行为上报kafka，kafka消费数据动态更新推荐列表
        kafkaTemplate.send("xupt-video-recommend", JSONObject.toJSONString(vo));
    }

    public void reportPreference(PreferenceVO vo) {
        //1.用户偏好数据入库
        PreferenceEntity entity = new PreferenceEntity();
        entity.userId = vo.userId;
        entity.itemId = vo.itemId;
        int score = 0;
        if (vo.status == 2) {
            score = 5;
        } else {
            score = vo.status == 0 ? 8 : 0;
        }
        entity.score = score;
        entity.created = new Date();
        Integer count = preferenceMapper.selectCount(
                new QueryWrapper<PreferenceEntity>().lambda().select().eq(PreferenceEntity::getUserId, vo.userId));
        if (count < 1) {
            preferenceMapper.insert(entity);
        } else {
            preferenceMapper.updateById(entity);
        }
        //2.用户偏好数据追加到csv文件
        FileOutputStream output = null;
        OutputStreamWriter writer = null;
        try {
//            String filePath = "D:\\HDFSTest\\item.csv";
            File file = new File(Constant.FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
                output = new FileOutputStream(file);
            } else {
                output = new FileOutputStream(file,true);
            }
            writer = new OutputStreamWriter(output);
            String str = vo.userId.substring(1) + "," + vo.itemId + "," + score + "\r\n";
            writer.write(str);
            recommend();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String registerUser() {
        long id = SnowFlake.nextId();
        String userId = "u" + id;
        User user = new User();
        user.userName = "admin";
        user.userPassword = "123456";
        user.userId = userId;
        userMapper.insert(user);
        return userId;
    }

    public String reportSource(SourceVO vo) {
        Article article = new Article();
        long id = SnowFlake.nextId();
        try {
            article.setId(String.valueOf(id));
            article.setUrl(URLEncoder.encode(vo.getUrl(),"UTF-8"));
            article.setTitle(vo.getTitle());
            article.setCreated(new Date());
            articleMapper.insert(article);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.valueOf(id);
    }
}
