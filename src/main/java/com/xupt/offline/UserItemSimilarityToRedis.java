package com.xupt.offline;

import com.alibaba.fastjson.JSON;
import com.xupt.util.JedisUtil;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

@Component
public class UserItemSimilarityToRedis {

  private CountDownLatch countLatch = new CountDownLatch(1);
  @Resource
  public JedisUtil jedisUtil;

  public void redisStorage(DataModel dataModel) {
    Executors.newSingleThreadExecutor().submit(
        new Runnable() {
          public void run() {
            realization(dataModel);
            countLatch.countDown();
          }
        }
    );
  }

  private void realization(DataModel dataModel) {
    try {
      LongPrimitiveIterator userIds = dataModel.getUserIDs();
      while(userIds.hasNext()) {
        long userID = userIds.nextLong();
        FastIDSet idSet = dataModel.getItemIDsFromUser(userID);
        String key = "UI:" + userID;//这个数据暂时用不上
        String videos = JSON.toJSONString(idSet.toArray());
//        jedisUtil.set(key.getBytes(), videos.getBytes());
        System.out.println("Stored User:" + key);
      }
    } catch(TasteException te) {
      te.printStackTrace();
    }
  }

  public void waitUtilDone() throws InterruptedException {
    countLatch.await();
  }
}
