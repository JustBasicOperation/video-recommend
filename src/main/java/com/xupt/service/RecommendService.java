package com.xupt.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xupt.entity.Article;
import com.xupt.entity.PreferenceEntity;
import com.xupt.entity.Video;
import com.xupt.mapper.UserMapper;
import com.xupt.mapper.VideoMapper;
import com.xupt.offline.HDFSDataModel;
import com.xupt.offline.ItemSimilarityToRedis;
import com.xupt.offline.UserItemSimilarityToRedis;
import com.xupt.service.impl.PreferenceServiceImpl;
import com.xupt.util.JedisUtil;
import com.xupt.vo.ClickReportVO;
import com.xupt.vo.PreferenceVO;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.precompute.MultithreadedBatchItemSimilarities;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendService {

    @Resource
    ItemSimilarityToRedis itemSimilarityToRedis;

    @Resource
    UserItemSimilarityToRedis userItemSimilarityToRedis;

    @Resource
    VideoMapper videoMapper;

    @Resource
    PreferenceServiceImpl preferenceService;

    @Resource
    UserMapper userMapper;

    @Resource
    KafkaTemplate kafkaTemplate;

    @Resource
    JedisUtil jedisUtil;

    public void recommend() {
        String filePath = "D:\\HDFSTest\\item.csv";
        File file = new File(filePath);
        HDFSDataModel dataModel = null;
        try {
            dataModel = new HDFSDataModel(file);
            userItemSimilarityToRedis.redisStorage(dataModel);

            GenericItemBasedRecommender recommender = null;
            try {
                recommender = new GenericItemBasedRecommender(dataModel,
                        new PearsonCorrelationSimilarity(dataModel));
            } catch (TasteException e) {
                e.printStackTrace();
            }
            MultithreadedBatchItemSimilarities threadDeal =
                    new MultithreadedBatchItemSimilarities(recommender, 5);
            threadDeal.computeItemSimilarities(1, 1,
                    itemSimilarityToRedis);
            userItemSimilarityToRedis.waitUtilDone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Video> getRecommendList(String userID) {
        //如果没有推荐列表，默认取前十条数据
        if (!jedisUtil.exists(userID)) {
            return videoMapper.selectList(new QueryWrapper<Video>().lambda().orderByDesc(Video::getCreated).last("limit 0,10"));
        } else {
            //从redis中获取前十条数据
            Set<String> set = jedisUtil.zRevRange(userID, 0L, 10L);
            LinkedList<String> list = new LinkedList<>(set);
            List<Long> ids = list.stream().map(Long::parseLong).collect(Collectors.toList());
            return videoMapper.selectBatchIds(ids);
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

    public void reportClick(List<ClickReportVO> list) {
        //1.点击行为上报kafka，kafka消费数据动态更新推荐列表
        String json = JSONObject.toJSONString(list);
        kafkaTemplate.send("xupt-video-recommend", json);
    }

    public void reportPreference(List<PreferenceVO> vos) {
        //1.用户偏好数据入库
        LinkedList<PreferenceEntity> list = new LinkedList<>();
        for (PreferenceVO vo : vos) {
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
            list.add(entity);
        }
        preferenceService.saveBatch(list);
        //2.用户偏好数据追加到csv文件
        appendCsv(list);
        //计算相似度
        recommend();
    }

    public void appendCsv(List<PreferenceEntity> entityList) {
        //用户偏好数据追加到csv文件
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
            for (PreferenceEntity entity : entityList) {
                String str = entity.userId.substring(1) + "," + entity.itemId + "," + entity.score + "\r\n";
                writer.write(str);
            }
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
}
