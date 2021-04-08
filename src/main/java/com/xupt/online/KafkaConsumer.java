package com.xupt.online;

import com.alibaba.fastjson.JSONObject;
import com.xupt.util.JedisUtil;
import com.xupt.util.VideoSimilarity;
import com.xupt.vo.ClickReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KafkaConsumer {

    @Resource
    JedisUtil jedisUtil;

    @KafkaListener(topics = {"xupt-video-recommend"})
    public void onMessage(ClickReportVO message){
        String key = message.getUserID();
        if (!jedisUtil.exists(key)) {
            log.error("key do not exists！");
        }
        //1.计算推荐列表并保存到redis中：根据itemId获取相似视频id列表，将该id列表保存到redis的list中
        String IIkey = "II" + message.getItemID();
        byte[] bytes = jedisUtil.get(IIkey.getBytes());
        List<VideoSimilarity> list = JSONObject.parseArray(Arrays.toString(bytes), VideoSimilarity.class);
        List<VideoSimilarity> collect = list.stream().filter(ele -> ele.getSimilarity() > 0.0).collect(Collectors.toList());
        List<Long> ids = collect.stream().map(VideoSimilarity::getVideoId).collect(Collectors.toList());
        String[] strings = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            strings[i] = String.valueOf(ids.get(i));
        }
        jedisUtil.lpush(key,strings);
        //2.去重：移除用户已经看过的视频id
        jedisUtil.lrem(key,0L,message.itemID);
    }
}
