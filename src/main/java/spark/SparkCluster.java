package spark;

import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.sql.SparkSession;

/**
 * Created by Asus- on 2018/8/8.
 * 聚类分析
 */
public class SparkCluster {

    SparkSession sparkSession = SparkSession.builder()
            .appName("Spark_Cluster")
            .master("local[2]")
            .getOrCreate();
    KMeans kMeans = new KMeans().setK(3).setSeed(1L);

}
