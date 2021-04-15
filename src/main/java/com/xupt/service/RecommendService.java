package com.xupt.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.precompute.MultithreadedBatchItemSimilarities;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
        String filePath = "D:\\HDFSTest\\item.csv";
        File file = new File(filePath);
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
        //获取前十个推荐视频
        Set<String> set = jedisUtil.zRevRange(userID, 0L, 10L);
        LinkedList<String> list = new LinkedList<>(set);
        List<Long> ids = list.stream().map(Long::parseLong).collect(Collectors.toList());
        return articleMapper.selectBatchIds(ids);
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
            String filePath = "D:\\HDFSTest\\item.csv";
            File file = new File(filePath);
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
}
