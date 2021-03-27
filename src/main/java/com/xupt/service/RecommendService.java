package com.xupt.service;

import com.xupt.offline.HDFSDataModel;
import com.xupt.offline.ItemSimilarityToRedis;
import com.xupt.offline.UserItemSimilarityToRedis;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.precompute.MultithreadedBatchItemSimilarities;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@Service
public class RecommendService {

    @Resource
    public ItemSimilarityToRedis itemSimilarityToRedis;

    @Resource
    public UserItemSimilarityToRedis userItemSimilarityToRedis;

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
}
