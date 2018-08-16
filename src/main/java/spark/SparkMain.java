package spark;

import bean.GpsDataBean;
import bean.MsgBean;
import bean.MsgDataBean;
import com.google.gson.Gson;
import db.DBCon;
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

import java.beans.Transient;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by Asus- on 2018/8/2.
 * Spark Streaming 实时数据处理
 */
public class SparkMain implements Serializable {

    private static JavaStreamingContext javaStreamingContext;
    private static JavaInputDStream<ConsumerRecord<String, String>> stream;

    private SparkConf sparkConf;
    private static Gson gson = new Gson();

    //mysql
    private static transient DBCon dbCon;
    private static transient Connection connection;

    //HBase
    private static String tableName;
    private static String gpsFamily;
    private static String etcFamily;
    private static HBaseOper hBaseOper;

    //车辆状态 map
    private static transient Map<Integer, String> stateMap = new HashMap<>();

    public SparkMain() {
        dbCon = DBCon.getInstance();
        connection = dbCon.getConnection();
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
        etcFamily = family[2];
    }

    /**
     * 初始化SparkStreaming
     */
    private void initSparkStreaming() {
        sparkConf = new SparkConf().setMaster("local[2]").setAppName("calgpsdata");
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
                        //插入车辆状态数据
                        hBaseOper.insert(tableName, gpsDataBean.getGpsid(), etcFamily, msgMap);

                        //插入车辆报警信息
                        //解析json，获取当前状态的数据
                        MsgBean msgBeanNow = gson.fromJson(fullMsg, MsgBean.class);
                        int carid = msgBeanNow.getCarid();
                        String time = msgBeanNow.getTime();
                        //存在carid才操作
                        if (stateMap.containsKey(carid)) {
                            //连接mysql将数据保存进去
                            Statement statement;
                            try {
                                connection.setAutoCommit(false);
                                statement = connection.createStatement();
                                List<MsgDataBean> msgDataBeanNowList = msgBeanNow.getMsgDataBean();
                                //获取上一状态的数据
                                String oldJson = stateMap.get(carid);
                                MsgBean msgBeanOld = gson.fromJson(oldJson, MsgBean.class);
                                List<MsgDataBean> msgDataBeanOldList = msgBeanOld.getMsgDataBean();
                                int size = msgDataBeanOldList.size() > msgDataBeanNowList.size() ? msgDataBeanNowList.size() : msgDataBeanOldList.size();
                                //判断当前的状态和上一状态是否一致
                                for (int i = 0; i < size; i++) {
                                    if (!msgDataBeanNowList.get(i).getStatus().equals(msgDataBeanOldList.get(i).getStatus())) {
                                        int status;
                                        if (msgDataBeanNowList.get(i).getStatus().equals("WARN")) {//出
                                            status = 0;
                                        } else {//进
                                            status = 1;
                                        }
                                        String fullSql = "insert into warntable(gpsid,ruleid,time,status) VALUES "
                                                + "("
                                                + "\'" + carid + "\'" + ","
                                                + "\'" + msgDataBeanNowList.get(i).getRuleid() + "\'" + ","
                                                + "\'" + time + "\'" + ","
                                                + "\'" + status + "\'"
                                                + ")";
                                        statement.executeUpdate(fullSql);
                                        connection.commit();
                                    }
                                }
                                statement.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        //更新map保存的状态
                        stateMap.put(carid, fullMsg);
                    }
                }
            });
        });
        start();
        await();
        stop();
    }

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

//    public void test(String fullMsg) {
//        //插入车辆报警信息
//        //1.解析json，获取当前状态的数据
//        Gson gson = new Gson();
//        MsgBean msgBeanNow = gson.fromJson(fullMsg, MsgBean.class);
//        int carid = msgBeanNow.getCarid();
//        String time = msgBeanNow.getTime();
//        //存在carid才操作
//        if (stateMap.containsKey(carid)) {
//            //连接mysql将数据保存进去
//            Statement statement = null;
//            try {
//                connection.setAutoCommit(false);
//                statement = connection.createStatement();
//                List<MsgDataBean> msgDataBeanNowList = msgBeanNow.getMsgDataBean();
//                //获取上一状态的数据
//                String oldJson = stateMap.get(carid);
//                MsgBean msgBeanOld = gson.fromJson(oldJson, MsgBean.class);
//                List<MsgDataBean> msgDataBeanOldList = msgBeanOld.getMsgDataBean();
//                //判断当前的状态和上一状态是否一致，根据上一状态的长度
//                for (int i = 0; i < msgDataBeanOldList.size(); i++) {
//                    System.out.println(msgDataBeanNowList.get(i).getStatus() + "   " + msgDataBeanOldList.get(i).getStatus());
//                    if (!msgDataBeanNowList.get(i).getStatus().equals(msgDataBeanOldList.get(i).getStatus())) {
//                        int status;
//                        if (msgDataBeanNowList.get(i).getStatus().equals("WARN")) {//出
//                            status = 0;
//                        } else {
//                            status = 1;
//                        }
//
//                        String fullSql = "insert into warntable(gpsid,ruleid,time,status) VALUES "
//                                + "("
//                                + "\'" + carid + "\'" + ","
//                                + "\'" + msgDataBeanNowList.get(i).getRuleid() + "\'" + ","
//                                + "\'" + time + "\'" + ","
//                                + "\'" + status + "\'"
//                                + ")";
//                        statement.executeUpdate(fullSql);
//                        connection.commit();
//                    }
//                }
//                statement.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        //更新map保存的状态
//        stateMap.put(carid, fullMsg);
//    }
//
//    public static void main(String[] args) {
//
//    }
}