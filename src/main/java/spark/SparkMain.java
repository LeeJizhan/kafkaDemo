package spark;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

/**
 * Created by Asus- on 2018/8/2.
 */
public class SparkMain {

    public SparkMain(){
        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("calgpsdata");
        SparkContext sparkContext = new SparkContext(sparkConf);
    }

}
