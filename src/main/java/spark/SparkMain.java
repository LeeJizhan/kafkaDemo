package spark;

import bean.GpsDataBean;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import properties.KafkaProperties;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Asus- on 2018/8/2.
 */
public class SparkMain {

    private static JavaStreamingContext javaStreamingContext;
    private static JavaInputDStream<ConsumerRecord<String, String>> stream;

    public SparkMain() {
        initSparkStreaming();
        this.stream = getKafkaData();
    }

    /**
     * 初始化SparkStreaming
     */
    private void initSparkStreaming() {
        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("calgpsdata");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        this.javaStreamingContext = new JavaStreamingContext(javaSparkContext, new Duration(5000));
    }

    public void run() {
        stream.map(record -> record.value()).map(value -> {
            String str = value;
            //System.out.println(s);
            return str;
        }).foreachRDD(rdd -> {
            rdd.foreachPartition(rdds -> {
                while (rdds.hasNext()) {
                    Gson gson = new Gson();
                    String jsonStr = rdds.next();
                    GpsDataBean gpsDataBean = gson.fromJson(jsonStr, GpsDataBean.class);
                    System.out.println("id: " + gpsDataBean.getGpsid() +
                            " time: " + gpsDataBean.getTime() +
                            " bearing: " + gpsDataBean.getBearing() +
                            " speed: " + gpsDataBean.getSpeed());
                }
            });
        });
        start();
        await();
        stop();
    }

    private void start() {
        this.javaStreamingContext.start();
    }

    private void await() {
        try {
            this.javaStreamingContext.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        this.javaStreamingContext.stop();
    }

    /**
     * 获取kafka的数据
     *
     * @return
     */
    private JavaInputDStream<ConsumerRecord<String, String>> getKafkaData() {
        KafkaProperties kafkaProperties = KafkaProperties.getInstance();
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", kafkaProperties.getKafkaServerUrl() + ":" + kafkaProperties.getKafkaServerPort());
        kafkaParams.put("key.deserializer", StringDeserializer.class.getName());
        kafkaParams.put("value.deserializer", StringDeserializer.class.getName());
        kafkaParams.put("group.id", kafkaProperties.getKafkaGroupID());
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        String topic = kafkaProperties.getKafkaTopic();
        Collection<String> topics = Arrays.asList(topic);
        JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        javaStreamingContext,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
                );
        return stream;
    }
}