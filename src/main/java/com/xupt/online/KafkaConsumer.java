package com.xupt.online;

import com.alibaba.fastjson.JSONObject;
import com.xupt.util.JedisUtil;
import com.xupt.util.VideoSimilarity;
import com.xupt.vo.ClickReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KafkaConsumer {

    @Resource
    JedisUtil jedisUtil;

    @KafkaListener(topics = {"xupt-video-recommend"})
    public void onMessage(ConsumerRecord<String,String> message){
        ClickReportVO vo = JSONObject.parseObject(message.value(),ClickReportVO.class);
        String key = vo.getUserId();
        //1.计算推荐列表并保存到redis中：根据itemId获取相似视频id列表，将该id列表保存到redis的list中
        String IIkey = "II:" + vo.getItemId();
        if (!jedisUtil.exists(IIkey)) {
            log.error("IIkey does not exists!");
        }
        byte[] bytes = jedisUtil.get(IIkey.getBytes());
        String jsonString = new String(bytes);
        List<VideoSimilarity> list = JSONObject.parseArray(jsonString, VideoSimilarity.class);
//        List<VideoSimilarity> collect = list.stream().filter(ele -> ele.getSimilarity() > 0.0).collect(Collectors.toList());
//        List<Long> ids = collect.stream().map(VideoSimilarity::getVideoId).collect(Collectors.toList());
//        String[] strings = new String[ids.size()];
//        for (int i = 0; i < ids.size(); i++) {
//            strings[i] = String.valueOf(ids.get(i));
//        }
//        jedisUtil.lpush(key,strings);
//        2.去重：移除用户已经看过的视频id
//        jedisUtil.lrem(key,0L,vo.itemId);
        //使用sorted set集合存储
        HashMap<String, Double> map = new HashMap<>();
        for (VideoSimilarity similarity : list) {
            map.put(String.valueOf(similarity.getVideoId()),similarity.getSimilarity());
        }
        jedisUtil.zadd(vo.getUserId(),map);
    }
}
