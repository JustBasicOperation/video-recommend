package com.xupt.offline;

import com.alibaba.fastjson.JSON;
import com.xupt.util.JedisUtil;
import com.xupt.util.VideoSimilarity;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.similarity.precompute.SimilarItem;
import org.apache.mahout.cf.taste.similarity.precompute.SimilarItems;
import org.apache.mahout.cf.taste.similarity.precompute.SimilarItemsWriter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
public class ItemSimilarityToRedis implements SimilarItemsWriter {
    private long itemCount = 0;

    @Resource
    public JedisUtil jedisUtil;

    @Override
    public void open() throws IOException {
        log.info("ItemSimilarityToRedis open");
    }

    @Override
    public void add(SimilarItems similarItems) throws IOException {
        VideoSimilarity[] videos = new VideoSimilarity[similarItems.numSimilarItems()];
        int counter = 0;
        for (SimilarItem item: similarItems.getSimilarItems()) {
            videos [counter] = new VideoSimilarity(item.getItemID(), item.getSimilarity());
            counter++;
        }
        String key = "II:" + similarItems.getItemID();
        String videoItems = JSON.toJSONString(videos);
        jedisUtil.set(key.getBytes(), videoItems.getBytes());
//        itemCount++;
//        if(itemCount % 100 == 0) {
//            System.out.println("Store " + key + " to redis, total:" + itemCount);
//        }
        log.info("stored IIKey:" + key);
    }

    @Override
    public void close() throws IOException {
        log.info("ItemSimilarityToRedis close");
    }
}
