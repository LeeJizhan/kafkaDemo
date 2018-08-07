package spark;

import hbase.HBaseOper;
import kafka.point.PointProducer;
import properties.KafkaProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by Asus- on 2018/8/7.
 * Spark 数据操作
 */
public class SparkOper extends Thread {

    /**
     * 1.获取HBase中的数据
     * 2.将HBase获取到的数据进行相关处理
     * 3.进行常驻点判断
     * 4.进行最后停车点判断
     * 5.将判断得到的结果写回HBase
     */
    @Override
    public void run() {
        List<String> lonList = null;
        List<String> latList = null;
        List<String> timeList = null;
        List<String> speedList = null;
        HBaseOper oper = new HBaseOper();
        String tableName = "gpsdata";
        String family = "gpsinfo";
        int count = 2350;
        /**
         * 1.获取HBase中的数据
         * 2.将HBase获取到的数据
         */
        //1.获取HBase中的数据
        for (int i = 2; i < 3; i++) {
            Map<String, List<String>> myMap = oper.getAllDataByRowKeyAndFamily(tableName, Integer.toString(i + 1), family);
            for (Map.Entry entry : myMap.entrySet()) {
                String key = (String) entry.getKey();
                if (key.equals("lon")) {
                    lonList = (List<String>) entry.getValue();
                } else if (key.equals("lat")) {
                    latList = (List<String>) entry.getValue();
                } else if (key.equals("speed")) {
                    speedList = (List<String>) entry.getValue();
                } else if (key.equals("time")) {
                    timeList = (List<String>) entry.getValue();
                }
            }
        }
        //打印数据
        for (int i = timeList.size() - 1; i > 0; i--) {
            System.out.println("time:" + timeList.get(i)
                    + " lon:" + lonList.get(i)
                    + " lat:" + latList.get(i)
                    + " speed:" + speedList.get(i));
        }
    }

    public static void main(String[] args) {
        PointProducer pointProducer = new PointProducer();
        pointProducer.start();
    }
}
