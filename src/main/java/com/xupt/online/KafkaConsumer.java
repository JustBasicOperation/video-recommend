package com.xupt.online;

import com.alibaba.fastjson.JSONObject;
import com.xupt.util.JedisUtil;
import com.xupt.util.VideoSimilarity;
import com.xupt.vo.ClickReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class KafkaConsumer {

    @Resource
    JedisUtil jedisUtil;

    @Resource
    JavaSparkContext context;

    @KafkaListener(topics = {"xupt-video-recommend"})
    public void onMessage(ConsumerRecord<String,String> message){
        List<ClickReportVO> list = JSONObject.parseArray(message.value(),ClickReportVO.class);
        //1.取出用户点击过的所有视频id并进行去重
        JavaRDD<ClickReportVO> rdd = context.parallelize(list);
        //TODO 去重逻辑等待验证
        List<String> itemIds = rdd.map(ClickReportVO::getItemId).distinct().collect();
        List<VideoSimilarity> similarityList = new LinkedList<>();
        //2.遍历用户点击过的每个itemId，从Redis得到其相似视频列表，并将所有列表合并
        for (String itemId : itemIds) {
            String IIkey = "II:" + itemId;
            if (!jedisUtil.exists(IIkey)) {
            log.error("IIkey does not exists!");
            }
            byte[] bytes = jedisUtil.get(IIkey.getBytes());
            String jsonString = new String(bytes);
            List<VideoSimilarity> similarities = JSONObject.parseArray(jsonString, VideoSimilarity.class);
            similarityList.addAll(similarities);
        }
        //3.第二步得到的列表再次去重
        JavaRDD<VideoSimilarity> javaRDD = context.parallelize(similarityList);
        List<VideoSimilarity> distinctList = javaRDD.distinct().collect();
        //4.使用sorted set数据结构将推荐列表存储到Redis中
        HashMap<String, Double> map = new HashMap<>();
        for (VideoSimilarity similarity : distinctList) {
            map.put(String.valueOf(similarity.getVideoId()),similarity.getSimilarity());
        }
        jedisUtil.zadd(list.get(0).getUserId(),map);
    }
}
