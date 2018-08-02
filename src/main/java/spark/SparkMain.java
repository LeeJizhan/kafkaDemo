package spark;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import properties.KafkaProperties;
import scala.Tuple2;

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
        JavaPairDStream<String, String> dStream = stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));
        dStream.print();
    }

    /**
     * 初始化SparkStreaming
     */
    private void initSparkStreaming() {
        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("calgpsdata");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        this.javaStreamingContext = new JavaStreamingContext(javaSparkContext, new Duration(2000));
    }

    /**
     * 获取kafka的数据
     *
     * @return
     */
    private JavaInputDStream<ConsumerRecord<String, String>> getKafkaData() {
        KafkaProperties kafkaProperties = KafkaProperties.getInstance();
        Map<String, Object> kafkaParams = new HashMap<String, Object>();
        kafkaParams.put("bootstrap.servers", kafkaProperties.getKafkaServerUrl() + ":" + kafkaProperties.getKafkaServerPort());
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
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
