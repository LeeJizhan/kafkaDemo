import drools.droolsbean.CarStatusRuturn;
import drools.monitor.Monitor;
import org.apache.derby.iapi.store.raw.log.Logger;
import utils.LoggerUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Asus- on 2018/7/12.
 */
public class Main {
    public static void main(String[] args) throws Exception {
//        DBOper dbOper = new DBOper();
//        dbOper.updateCarData();
//        dbOper.updateGpsDeviceData();
//        dbOper.updateCarAndGpsData();
        String[] carLons = {"122.04", "121.10", "112.10"};
        String[] carLats = {"22.04", "21.10", "12.10"};

        Monitor monitor = new Monitor();
        monitor.setPriority(10);
        monitor.start();
        Thread.sleep(10000);
        //Thread.currentThread().join();
        for (int i = 0; i < 300; i++) {
            try {
                List<CarStatusRuturn> list = monitor.getMonitorAndData(carLons[0], carLats[0]);
                for (CarStatusRuturn carStatusRuturn : list) {
                    LoggerUtil.info("id:  " + carStatusRuturn.getwLID()
                            + "  status: " + carStatusRuturn.getcStatus());
                }
                System.out.println("-------------------------------------------");
                Thread.sleep(3000);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        /**
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
         **/
    }
}
