package dbscan;

import db.DBCon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus- on 2018/8/14.
 */
public class DBScanMain {

    private DBCon dbCon;
    private Connection connection;

    public DBScanMain() {
        dbCon = DBCon.getInstance();
        connection = dbCon.getConnection();
    }

    public void writeEveryCarStop() {
        //每辆出租车的常驻点
        DBScan dbScan = new DBScan();
        //半径500m
        dbScan.setRadius(500);
        //4个点
        dbScan.setMinPts(4);
        List<List<Point>> everyCarData = DBScanData.getEveryCarData();
        try {
            //写入数据库
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("");
            //sql前缀
            String preSql = "INSERT INTO acarusualstop (gpsid,daytime,lon,lat,cluster) VALUES ";
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 1; i <= everyCarData.size(); i++) {
                List<Point> points = everyCarData.get(i - 1);
                dbScan.process(points);
                for (Point p : points) {
                    if (p.getCluster() != 0) {
                        String updateCarSql = "("
                                + "\'" + i + "\'" + ","
                                + "\'" + "2018-08-13" + "\'" + ","
                                + "\'" + p.getLon() + "\'" + ","
                                + "\'" + p.getLat() + "\'" + ","
                                + "\'" + p.getCluster() + "\'"
                                + "),";   //SQL语句
                        stringBuffer.append(updateCarSql);
                    }
                }
                //写入文件
                //DBScanData.writeData(points, "D:\\l\\data\\data" + (i + 1) + ".txt");
            }
            String everyCarSql = preSql + stringBuffer.substring(0, stringBuffer.length() - 1);
            preparedStatement.addBatch(everyCarSql);
            preparedStatement.executeBatch();
            //提交事务
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    //所有出租车的常驻点
//    public void wirteAllCarStop() {
//        //所有出租车的常驻点
//        DBScan dbScan = new DBScan();
//        //半径1000m
//        dbScan.setRadius(1000);
//        //12个点
//        dbScan.setMinPts(12);
//        List<Point> allCarData = DBScanData.getAllCarData();
//        dbScan.process(allCarData);
//        try {
//            //写入数据库
//            connection.setAutoCommit(false);
//            PreparedStatement preparedStatement = connection.prepareStatement("");
//            //sql前缀
//            String preSql = "INSERT INTO acarusualstop (daytime,lon,lat,cluster) VALUES ";
//            StringBuffer stringBuffer = new StringBuffer();
//            for (int i = 1; i <= allCarData.size(); i++) {
//                Point p = allCarData.get(i - 1);
//                if (p.getCluster() != 0) {
//                    String updateCarSql = "("
//                            + "\'" + "2018-08-13" + "\'" + ","
//                            + "\'" + p.getLon() + "\'" + ","
//                            + "\'" + p.getLat() + "\'" + ","
//                            + "\'" + p.getCluster() + "\'"
//                            + "),";   //SQL语句
//                    stringBuffer.append(updateCarSql);
//                }
//            }
//            String everyCarSql = preSql + stringBuffer.substring(0, stringBuffer.length() - 1);
//            preparedStatement.addBatch(everyCarSql);
//            preparedStatement.executeBatch();
//            //提交事务
//            connection.commit();
//            //写入文件
//            //DBScanData.writeData(points, "D:\\l\\data\\data" + (i + 1) + ".txt");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        //写入文件
//        DBScanData.writeData(allCarData, "D:\\l\\data\\allData.txt");
//    }

    public static void main(String[] args) {
        DBScanMain dbScanMain = new DBScanMain();
        dbScanMain.writeEveryCarStop();
    }
}
