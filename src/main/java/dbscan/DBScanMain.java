package dbscan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus- on 2018/8/14.
 */
public class DBScanMain {
    public static void main(String[] args) {
        DBScan dbScan = new DBScan();
        //半径500m
        dbScan.setRadius(500);
        //4个点
        dbScan.setMinPts(4);
//        ArrayList<Point> points = DBScanData.getOneCarData(1);
//        dbScan.process(points);
//        for (Point p : points) {
//            System.out.println(p);
//        }
//        DBScanData.writeData(points, "data.txt");
        List<List<Point>> allData = DBScanData.getAllCarData();
        for (int i = 0; i < allData.size(); i++) {
            List<Point> points = allData.get(i);
            dbScan.process(points);
            for (Point p : points) {
                //写入数据库

            }
            DBScanData.writeData(points, "D:\\l\\data\\data" + (i + 1) + ".txt");
        }
    }
}
