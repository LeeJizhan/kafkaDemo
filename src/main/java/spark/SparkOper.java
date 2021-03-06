package spark;

import hbase.HBaseOper;
import utils.LoggerUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Asus- on 2018/8/7.
 * Spark 数据操作/离线
 */
public class SparkOper {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd hh:mm:ss");

    /**
     * 1.获取HBase中的数据
     * 2.将HBase获取到的数据进行相关处理
     * 3.进行停车点判断和停车时间计算
     * 4.将得到的结果写回HBase
     */
    public void doWork() {
        List<String> lonList = null;
        List<String> latList = null;
        List<String> timeList = null;
        List<String> speedList = null;
        HBaseOper oper = new HBaseOper();
        String tableName = "gpsdata";
        String gpsFamily = "gpsinfo";
        String usualFamily = "usualstopinfo";
        int count = 2350;
//        Map<String, Object> myMap = new HashMap<>();
//        myMap.put("lon", 121.382333);
//        myMap.put("lat", 31.192717);
//        myMap.put("totalTime", 3600 * 24);
//        oper.insert(tableName, Integer.toString(375), usualFamily, myMap);
        /**
         * 1.获取HBase中的数据
         * 2.将HBase获取到的数据
         */
        //1.获取HBase中的数据
        for (int i = 0; i < count; i++) {
            Map<String, List<String>> myListMap = oper.getAllDataByRowKeyAndFamily(tableName, Integer.toString(i + 1), gpsFamily);
            for (Map.Entry entry : myListMap.entrySet()) {
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

            String startStopTime = "";
            String finalStopTime = "";
            String totalStopTime = "";
            //默认是运动的
            boolean isStop = false;
            boolean isChange = false;
            String stopLon = "";
            String stopLat = "";

            //处理数据
            for (int j = timeList.size() - 1; j > 0; j--) {
                if (isStop == false) {
                    //运动到停止,记录停车点和当前时间
                    if (speedList.get(j).equals("0")) {
                        isStop = true;
                        stopLon = lonList.get(j);
                        stopLat = latList.get(j);
                        startStopTime = timeList.get(j);
                    }
                } else {
                    //停止到运动，记录当前时间，将停车点和停车时间写进HBase
                    if (Integer.valueOf(speedList.get(j)) > 0) {
                        isStop = false;
                        isChange = true;
                        finalStopTime = timeList.get(j);
                        try {
                            long start = simpleDateFormat.parse(startStopTime).getTime();
                            long stop = simpleDateFormat.parse(finalStopTime).getTime();
                            int seconds = (int) ((stop - start) / (1000));
                            totalStopTime = Integer.toString(seconds);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Map<String, Object> myMap = new HashMap<>();
                        myMap.put("lon", stopLon);
                        myMap.put("lat", stopLat);
                        myMap.put("totalTime", totalStopTime);
                        oper.insert(tableName, Integer.toString(i + 1), usualFamily, myMap);
                        LoggerUtil.info("stopLon:" + stopLon
                                + "   stopLat:" + stopLat
                                + "   totalStopTime:" + totalStopTime + " s");
                    }
                }
            }
            //表示速度一直为0
            if (isStop == true && isChange == false) {
                Map<String, Object> myMap = new HashMap<>();
                myMap.put("lon", lonList.get(0));
                myMap.put("lat", latList.get(0));
                myMap.put("totalTime", 3600 * 24);
                oper.insert(tableName, Integer.toString(i + 1), usualFamily, myMap);
            }
            //表示速度一直不变
            if (isStop == false && isChange == false) {
                Map<String, Object> myMap = new HashMap<>();
                myMap.put("lon", lonList.get(0));
                myMap.put("lat", latList.get(0));
                myMap.put("totalTime", 3600 * 24);
                oper.insert(tableName, Integer.toString(i + 1), usualFamily, myMap);
            }
        }
    }

    public static void main(String[] args) {
        SparkOper sparkOper = new SparkOper();
        sparkOper.doWork();
    }
}
