import drools.droolsbean.CarStatusRuturn;
import drools.monitor.Monitor;
import kafka.gpstest.Producer;
import properties.KafkaProperties;
import spark.SparkMain;
import spark.SparkOper;
import utils.LoggerUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Asus- on 2018/7/12.
 */
public class Main {
    public static void main(String[] args) throws Exception {
//        DBOper dbOper = new DBOper();
//        dbOper.updateCarData();
//        dbOper.updateGpsDeviceData();
//        dbOper.updateCarAndGpsData();

//        String[] carLons = {"121.468100", "121.470000", "121.583500"};
//        String[] carLats = {"31.291600", "31.198000", "31.221100"};
//
//        Monitor monitor = new Monitor();
//        monitor.setPriority(10);
//        monitor.start();
//        Thread.sleep(10000);
//        //Thread.currentThread().join();
//        for (int i = 0; i < 1000; i++) {
//            try {
//                List<CarStatusRuturn> list = monitor.getMonitorAndData(carLons[i % 3], carLats[i % 3]);
//                for (CarStatusRuturn carStatusRuturn : list) {
//                    LoggerUtil.info("id:  " + carStatusRuturn.getwLID()
//                            + "  status: " + carStatusRuturn.getcStatus());
//                }
//                System.out.println("-------------------------------------------");
//                System.out.println("-------------------------------------------");
//                System.out.println("-------------------------------------------");
//                Thread.sleep(3000);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }

        Monitor monitor = new Monitor();
        monitor.setPriority(10);
        monitor.start();
//        Thread.sleep(10000);
        KafkaProperties kafkaProperties = KafkaProperties.getInstance();
        //通过键值对的方式读取kafka配置文件的值
        String gpsTopic = kafkaProperties.getKafkaGpsTopic();
        boolean isAsync = true;
        //从数据库拿数据扔到kafka中
        Producer producer = new Producer(gpsTopic, isAsync);
        producer.start();

        //Spark Streaming从kafka拿数据进行实时处理写入HBase中
        SparkMain sparkMain = new SparkMain();
        sparkMain.doWork();

        //定时任务，进行停车点分析
        Runnable runnable = () -> {
            SparkOper oper = new SparkOper();
            oper.doWork();
        };

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        //从执行开始的时间起，12小时更新一次，initialDelay表示延迟12小时后开始第一次执行
        service.scheduleAtFixedRate(runnable, 3600 * 12, 3600 * 12, TimeUnit.SECONDS);

        //定时任务，DBScan聚类，进行常驻点分析
        Runnable runnable2 = () -> {
            SparkOper oper = new SparkOper();
            oper.doWork();
        };
        ScheduledExecutorService service2 = Executors.newSingleThreadScheduledExecutor();
        //从执行开始的时间起，24小时更新一次，initialDelay表示延迟一天后开始第一次执行
        service2.scheduleAtFixedRate(runnable2, 3600 * 24, 3600 * 24, TimeUnit.SECONDS);
    }
}
