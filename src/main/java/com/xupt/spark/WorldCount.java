package com.xupt.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;

public class WorldCount {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("app").setMaster("local");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        JavaRDD<Integer> parallelize = sparkContext.parallelize(Arrays.asList(1, 2, 3, 4), 3);
        Tuple2<Integer, Integer> map =
                parallelize.mapToPair(x -> new Tuple2<>(x, 1)).reduce((x,y) -> getReduce(x,y));
        System.out.printf("======================================================");
        System.out.println("数组sum:" + map._1);
        sparkContext.stop();
    }
    public static Tuple2 getReduce(Tuple2<Integer, Integer> x, Tuple2<Integer, Integer> y) {
        Integer a = x._1();
        Integer b = x._2();
        a += y._1();
        b += y._2();
        return new Tuple2(a, b);
    }
}
