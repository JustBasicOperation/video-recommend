package com.xupt.offline;

import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.precompute.MultithreadedBatchItemSimilarities;

import java.io.File;
import java.io.IOException;

public class RecTest {
    public static void main(String[] args) throws IOException {
        String filePath = "D:\\HDFSTest\\item.csv";
        File file = new File(filePath);
        HDFSDataModel dataModel = new HDFSDataModel(file);
        UserItemSimilarityToRedis userItemSimilarityToRedis = new UserItemSimilarityToRedis();
        userItemSimilarityToRedis.redisStorage(dataModel);

        GenericItemBasedRecommender recommender = new GenericItemBasedRecommender(dataModel,
                new LogLikelihoodSimilarity(dataModel));
        MultithreadedBatchItemSimilarities threadDeal =
                new MultithreadedBatchItemSimilarities(recommender, 5);
        threadDeal.computeItemSimilarities(Runtime.getRuntime().availableProcessors(), 1,
                new ItemSimilarityToRedis());

        try {
            userItemSimilarityToRedis.waitUtilDone();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
