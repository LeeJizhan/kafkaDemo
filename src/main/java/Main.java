import kafka.gpstest.Producer;
import properties.KafkaProperties;
import spark.SparkMain;
import spark.SparkOper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Asus- on 2018/7/12.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
//        DBOper dbOper = new DBOper();
//        dbOper.updateCarData();
//        dbOper.updateGpsDeviceData();
//        dbOper.updateCarAndGpsData();
        KafkaProperties kafkaProperties = KafkaProperties.getInstance();
        //通过键值对的方式读取kafka配置文件的值
        String gpsTopic = kafkaProperties.getKafkaGpsTopic();
        boolean isAsync = true;
        //从数据库拿数据扔到kafka中
        Producer producer = new Producer(gpsTopic, isAsync);
        producer.start();

        //Spark Streaming从kafka拿数据进行实时处理写入HBase中
        SparkMain sparkMain = new SparkMain();
        sparkMain.run();

        //定时任务，进行常驻点分析
        Runnable runnable = () -> {
            SparkOper oper = new SparkOper();
            oper.doWork();
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        //从执行开始的时间起，24小时更新一次，initialDelay表示延迟1s后开始第一次执行
        service.scheduleAtFixedRate(runnable, 1, 3600 * 24, TimeUnit.SECONDS);
    }
}
