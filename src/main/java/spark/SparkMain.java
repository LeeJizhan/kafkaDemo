package spark;

import bean.GpsDataBean;
import com.google.gson.Gson;
import drools.droolsbean.CarStatusRuturn;
import drools.monitor.Monitor;
import hbase.HBaseOper;
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
import properties.HBaseTableProperties;
import properties.KafkaProperties;
import utils.LoggerUtil;

import java.beans.Transient;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Asus- on 2018/8/2.
 * Spark Streaming 实时数据处理
 */
public class SparkMain {

    private static JavaStreamingContext javaStreamingContext;
    private static JavaInputDStream<ConsumerRecord<String, String>> stream;
    private static Gson gson = new Gson();

    //HBase
    private static String tableName;
    private static String gpsFamily;
    private static String usualFamily;
    private static String etcFamily;
    private static HBaseOper hBaseOper;

    public SparkMain() {
        initSparkStreaming();
        initHBase();
        stream = getKafkaData();
    }

    private void initHBase() {
        hBaseOper = new HBaseOper();
        HBaseTableProperties tableProperties = HBaseTableProperties.getInstance();
        tableName = tableProperties.getTableName();
        String families = tableProperties.getFamilies();
        String[] family = families.split(",");
        gpsFamily = family[0];
        usualFamily = family[1];
        etcFamily = family[2];
    }

    /**
     * 初始化SparkStreaming
     */
    private void initSparkStreaming() {
        SparkConf sparkConf = new SparkConf().setMaster("local[2]").setAppName("calgpsdata");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        javaStreamingContext = new JavaStreamingContext(javaSparkContext, new Duration(5000));
    }

    public void doWork() {
        stream.map(record -> record.value()).map(value -> {
            String str = value;
            //System.out.println(str);
            return str;
        }).foreachRDD(rdd -> {
            rdd.foreachPartition(rdds -> {
                while (rdds.hasNext()) {
                    String jsonStr = rdds.next();
                    GpsDataBean gpsDataBean = gson.fromJson(jsonStr, GpsDataBean.class);
                    Map<String, Object> gpsMap = new HashMap<>();
                    gpsMap.put("lon", gpsDataBean.getLon());
                    gpsMap.put("lat", gpsDataBean.getLat());
                    gpsMap.put("speed", gpsDataBean.getSpeed());
                    gpsMap.put("bearing", gpsDataBean.getBearing());
                    gpsMap.put("time", gpsDataBean.getTime());
                    hBaseOper.insert(tableName, gpsDataBean.getGpsid(), gpsFamily, gpsMap);
                    //进行规则判断
                    StringBuilder stringBuilder = new StringBuilder();
                    try {
                        List<CarStatusRuturn> mList = Monitor.getMonitorAndData(gpsDataBean.getLon(), gpsDataBean.getLat());
                        if (mList != null && mList.size() != 0) {
                            for (CarStatusRuturn statusRuturn : mList) {
                                String dataStr = "{\"ruleid\":" + statusRuturn.getwLID() + ","
                                        + "\"status\":" + "\"" + statusRuturn.getcStatus() + "\""
                                        + "},";
                                stringBuilder.append(dataStr);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (stringBuilder != null && stringBuilder.length() != 0) {
                        String msg = "[" + stringBuilder.substring(0, stringBuilder.length() - 1).toString() + "]";
                        String fullMsg = "{" + "\"carid\":" + gpsDataBean.getGpsid() + ","
                                + "\"time\":" + "\"" + gpsDataBean.getTime() + "\"" + ","
                                + "\"msg\":"
                                //+ ruleCheck(gpsDataBean.getLon(), gpsDataBean.getLat())
                                + msg
                                + "}";
                        Map<String, Object> msgMap = new HashMap<>();
                        msgMap.put("msg", fullMsg);
                        //LoggerUtil.info(fullMsg);
                        hBaseOper.insert(tableName, gpsDataBean.getGpsid(), etcFamily, msgMap);
                    }
                }
            });
        });
        start();
        await();
        stop();
    }

//    /**
//     * 进行规则判断
//     *
//     * @param lon
//     * @param lat
//     * @return
//     */
//    private String ruleCheck(String lon, String lat) {
//        String msg;
//        //进行规则判断
//        StringBuilder stringBuilder = new StringBuilder();
//        try {
//            List<CarStatusRuturn> mList = Monitor.getMonitorAndData(lon, lat);
//            for (CarStatusRuturn statusRuturn : mList) {
//                String dataStr = "{\"ruleid\":" + statusRuturn.getwLID()
//                        + "\"status\":" + "\"" + statusRuturn.getcStatus() + "\""
//                        + "},";
//                stringBuilder.append(dataStr);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        msg = "[" + stringBuilder.substring(0, stringBuilder.length() - 1).toString() + "]";
//        return msg;
//    }

    private void start() {
        javaStreamingContext.start();
    }

    private void await() {
        try {
            javaStreamingContext.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        javaStreamingContext.stop();
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

        String topic = kafkaProperties.getKafkaGpsTopic();
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