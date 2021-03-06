package com.xupt.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xupt.constant.Constant;
import com.xupt.entity.HistoryEntity;
import com.xupt.entity.PreferenceEntity;
import com.xupt.entity.User;
import com.xupt.entity.Video;
import com.xupt.mapper.UserMapper;
import com.xupt.mapper.VideoMapper;
import com.xupt.offline.HDFSDataModel;
import com.xupt.offline.ItemSimilarityToRedis;
import com.xupt.offline.UserItemSimilarityToRedis;
import com.xupt.service.impl.HistoryServiceImpl;
import com.xupt.service.impl.PreferenceServiceImpl;
import com.xupt.util.JedisUtil;
import com.xupt.vo.ClickReportVO;
import com.xupt.vo.PreferenceVO;
import com.xupt.vo.VideoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.precompute.MultithreadedBatchItemSimilarities;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
    HistoryServiceImpl historyService;

    @Resource
    KafkaTemplate kafkaTemplate;

    @Resource
    JedisUtil jedisUtil;

    @Resource
    UserMapper userMapper;

    public void recommend(String filePath) {
//        String filePath = "D:\\HDFSTest\\item.csv";
        File file = new File(filePath);
        HDFSDataModel dataModel = null;
        try {
            dataModel = new HDFSDataModel(file);
            userItemSimilarityToRedis.redisStorage(dataModel);

            GenericItemBasedRecommender recommender = null;
            try {
                recommender = new GenericItemBasedRecommender(dataModel,
                        new PearsonCorrelationSimilarity(dataModel));
            } catch (Exception e) {
                e.printStackTrace();
            }
            MultithreadedBatchItemSimilarities threadDeal =
                    new MultithreadedBatchItemSimilarities(recommender, 50,30);
            threadDeal.computeItemSimilarities(3, 1,
                    itemSimilarityToRedis);
            userItemSimilarityToRedis.waitUtilDone();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<VideoVO> getRecommendList(String userID) {
        //???????????????????????????????????????50?????????
        String key = userID + "-" + Constant.getTodayString();
        List<Video> list = null;
        if (!jedisUtil.exists(key)) {
            int start = (int) (Math.random()*10000);
            list = videoMapper.selectByLimit(start,50);
        } else {
            //???redis????????????50???????????????set?????????
            Set<String> set = jedisUtil.zRevRange(key, 0L, 50L);
            String[] strings = set.toArray(new String[set.size()]);
            jedisUtil.zRemove(key,strings);
            LinkedList<String> ids = new LinkedList<>(set);
            if (ids.size() > 50) {
                list =  videoMapper.selectBatchIds(ids);
            } else {
                //????????????????????????????????????50?????????
                String hotspotKey = Constant.getHotspotKey();
                Long end = 50L - ids.size();
                Set<String> hotspot = jedisUtil.zRevRange(hotspotKey, 0L, end);
                ids.addAll(hotspot);
                list = videoMapper.selectBatchIds(ids);
            }
        }
        LinkedList<VideoVO> res = new LinkedList<>();
        for (Video video : list) {
            VideoVO videoVO = new VideoVO();
            videoVO.setVideoId(video.getVideoId());
            videoVO.setCover_address(video.getCoverAddress());
            videoVO.setTitle(video.getTitle());
            res.add(videoVO);
        }
        return res;
    }

    public void reportClick(List<ClickReportVO> list) {
        //1.??????????????????kafka???kafka????????????????????????????????????
        String json = JSONObject.toJSONString(list);
        kafkaTemplate.send("xupt-video-recommend", json);
        //2.??????????????????
        LinkedList<HistoryEntity> entities = new LinkedList<>();
        for (ClickReportVO vo : list) {
            HistoryEntity historyEntity = new HistoryEntity();
            historyEntity.setUserId(vo.getUserId());
            historyEntity.setItemId(vo.getItemId());
            historyEntity.setCreated(new Date());
            entities.add(historyEntity);
        }
        historyService.saveBatch(entities);
    }

    public void reportPreference(List<PreferenceVO> vos) {
        //1.??????????????????????????????????????????????????????
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
        String path = Constant.FILE_PREFIX + Constant.getTodayString() + ".csv";
//        String path = "D:\\HDFSTest\\item.csv";
        //2.???????????????????????????csv??????
        appendCsv(path,list);
        //3.???????????????
        recommend(path);
    }

    public void appendCsv(String filePath,List<PreferenceEntity> entityList) {
        //???????????????????????????csv??????
        FileOutputStream output = null;
        OutputStreamWriter writer = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                boolean isSuccess = file.createNewFile();
                log.info("appendCSv file not exist,create new File result:" +  isSuccess);
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

    public Video getVideo(String videoID) {
        //??????????????????
        Video video = videoMapper.selectById(videoID);
        //??????????????????
        String key = Constant.getHotspotKey();
        jedisUtil.zIncrementBy(key,1,videoID);
        return video;
    }

    public List<VideoVO> getHotspot() {
        Set<String> strings = jedisUtil.zRevRange(Constant.getHotspotKey(), 0L, 50L);
        List<Video> videos = videoMapper.selectBatchIds(strings);
        List<VideoVO> res = videos.stream().map(video -> {
            VideoVO videoVO = new VideoVO();
            videoVO.setVideoId(video.getVideoId());
            videoVO.setCover_address(video.getCoverAddress());
            videoVO.setTitle(video.getTitle());
            return videoVO;
        }).collect(Collectors.toList());
        return res;
    }

    public void displayDataInit() {
        //1.?????????????????????????????????????????????????????????????????????????????????5-8?????????
        List<User> users = userMapper.selectList(new QueryWrapper<>());
        List<Video> videos = videoMapper.selectList(
                new QueryWrapper<Video>().lambda().like(Video::getTitle, "%????????????%"));
        LinkedList<PreferenceVO> list = new LinkedList<>();
        for (User user : users) {
            for (Video video : videos) {
                PreferenceVO vo = new PreferenceVO();
                vo.setItemId(video.getVideoId());
                vo.setUserId(user.getUserId());
                vo.setStatus(Math.random() < 0.5 ? 0 : 1);
                list.add(vo);
            }
            this.reportPreference(list);
            list.clear();
        }
    }
}
