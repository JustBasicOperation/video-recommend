package com.xupt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xupt.util.HDFSUtils;
import com.xupt.util.VideoSimilarity;
import org.apache.hadoop.conf.Configuration;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.util.TimeZone;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RecommendServiceTest {

    @Resource
    public RecommendService recommendService;

    @Test
    public void test01(){
        recommendService.recommend("D:\\HDFSTest\\test.csv");
    }

    @Test
    public void test02(){
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://8.131.121.224:8082");
        HDFSUtils.uploadFile(conf,"D:\\HDFSTest\\item.csv","/videoRec");
    }

    @Test
    public void test05(){
        VideoSimilarity videoSimilarity = new VideoSimilarity();
        videoSimilarity.setVideoId(1);
        videoSimilarity.setSimilarity(2.0);
        String string = JSON.toJSONString("string");
        System.out.println(string);
        String str = JSONObject.toJSONString("str");
        System.out.println(str);
    }

    @Test
    public void test06(){
        TimeZone aDefault = TimeZone.getDefault();
        System.out.println(aDefault);
    }

    /**
     * 测试准确率和召回率
     * @throws Exception
     */
    @Test
    public void testEvaluator() throws Exception {
        //准备数据 这里是电影评分数据
        File file = new File("D:\\HDFSTest\\item.csv");
        //将数据加载到内存中，GroupLensDataModel是针对开放电影评论数据的
        DataModel dataModel = new FileDataModel(file);
        RecommenderIRStatsEvaluator statsEvaluator = new GenericRecommenderIRStatsEvaluator();
        RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel model) throws TasteException {
                UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
                UserNeighborhood neighborhood = new NearestNUserNeighborhood(4, similarity, model);
                return new GenericUserBasedRecommender(model, neighborhood, similarity);
            }
        };
        // 计算推荐4个结果时的查准率和召回率
        //使用评估器，并设定评估期的参数
        //4表示"precision and recall at 4"即相当于推荐top4，然后在top-4的推荐上计算准确率和召回率
        IRStatistics stats = statsEvaluator.evaluate(recommenderBuilder, null, dataModel, null, 10, GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
        System.out.println(stats.getPrecision());
        System.out.println(stats.getRecall());
    }

    /**
     * 评估推荐模型
     * @throws Exception
     */
    @Test
    public void testModelEvaluator() throws Exception {
        //准备数据 这里是电影评分数据
        File file = new File("D:\\HDFSTest\\item.csv");
        //将数据加载到内存中，GroupLensDataModel是针对开放电影评论数据的
        DataModel dataModel = new FileDataModel(file);
        //推荐评估，使用均方根
        //RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
        //推荐评估，使用平均差值
        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        RecommenderBuilder builder = new RecommenderBuilder() {

            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
                UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
                return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
            }
        };
        // 用70%的数据用作训练，剩下的30%用来测试
        double score = evaluator.evaluate(builder, null, dataModel, 0.7, 1.0);
        //最后得出的评估值越小，说明推荐结果越好
        System.out.println(score);
    }

    /**
     * mahout协同过滤性能测试
     */
    @Test
    public void testPerformance() {
        //1.1000条数据测试
        Long start = System.currentTimeMillis();
        String path = "D:\\HDFSTest\\100000\\100000.csv";
        recommendService.recommend(path);
        System.out.println("100000 data cost time = " + (System.currentTimeMillis() - start));
    }
}