package dbscan;

import hbase.HBaseOper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Asus- on 2018/8/14.
 */
public class DBScanData {
    private static final int count = 2350;
    private static DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();

    public static ArrayList<Point> generateSinData(int size) {
        ArrayList<Point> points = new ArrayList<Point>(size);
        Random rd = new Random(size);
        for (int i = 0; i < size / 2; i++) {
            double x = format(Math.PI / (size / 2) * (i + 1));
            double y = format(Math.sin(x));
            points.add(new Point(x, y));
        }
        for (int i = 0; i < size / 2; i++) {
            double x = format(1.5 + Math.PI / (size / 2) * (i + 1));
            double y = format(Math.cos(x));
            points.add(new Point(x, y));
        }
        return points;
    }

    private static double format(double x) {
        return Double.valueOf(df.format(x));
    }

    public static ArrayList<Point> getOneCarData(int carID) {
        ArrayList<Point> points = new ArrayList<>();
        HBaseOper oper = new HBaseOper();
        String tableName = "gpsdata";
        String usualFamily = "usualstopinfo";

        //获取HBase中的数据
        Map<String, List<String>> myListMap = oper.getAllDataByRowKeyAndFamily(tableName, Integer.toString(carID), usualFamily);
        if (myListMap.size() > 0 && myListMap != null) {
            List<String> lonList = new ArrayList<>();
            List<String> latList = new ArrayList<>();
            for (Map.Entry entry : myListMap.entrySet()) {
                String key = (String) entry.getKey();
                if (key.equals("lon")) {
                    lonList = (List<String>) entry.getValue();
                } else if (key.equals("lat")) {
                    latList = (List<String>) entry.getValue();
                }
            }
            for (int j = lonList.size() - 1; j > 0; j--) {
                points.add(new Point(Double.valueOf(lonList.get(j)), Double.valueOf(latList.get(j))));
            }
        }
        return points;
    }

    public static List<List<Point>> getAllCarData() {
        List<List<Point>> allData = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            List<Point> points = getOneCarData(i + 1);
            allData.add(points);
        }
        return allData;
    }

    public static void writeData(List<Point> points, String path) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            for (Point point : points) {
                bw.write(point.toString() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
