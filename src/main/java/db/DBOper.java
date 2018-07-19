package db;

import bean.CarBean;
import data.CarData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus- on 2018/7/19.
 */
public class DBOper {

    private DBCon dbCon;
    private List<String> carIDList = new ArrayList<String>();
    private List<String> gpsIDList = new ArrayList<String>();

    public DBOper() {
        this.dbCon = DBCon.getInstance();
    }

    /**
     * 将车的信息写入数据库
     */
    public void updateCarData() {
        CarData carData = new CarData();
        List<CarBean> carBeanList = carData.getCarData();
        //创建数据库连接
        Connection conn = dbCon.getConnection();
        try {
            //关闭自动连接
            conn.setAutoCommit(false);
            // sql前缀
            String preSql = "INSERT INTO car (carid,brand,model,number,owner,phone) VALUES ";
            //实现1秒插入10000条数据
            PreparedStatement preparedStatement = conn.prepareStatement("");
            StringBuffer stringBuffer = new StringBuffer();
            for (CarBean carBean : carBeanList) {
                String carid = carBean.getCarID();
                String brand = carBean.getBrand();
                String model = carBean.getModel();
                String number = carBean.getNumber();
                String owner = carBean.getOwner();
                String phone = carBean.getPhone();

                carIDList.add(carid);
                String updateCarSql = "("
                        + "\'" + carid + "\'" + ","
                        + "\'" + brand + "\'" + ","
                        + "\'" + model + "\'" + ","
                        + "\'" + number + "\'" + ","
                        + "\'" + owner + "\'" + ","
                        + "\'" + phone + "\'"
                        + "),";   //SQL语句
                stringBuffer.append(updateCarSql);
            }
            //构建完整的sql
            String carSql = preSql + stringBuffer.substring(0, stringBuffer.length() - 1);
            //添加事务
            preparedStatement.addBatch(carSql);
            //执行
            preparedStatement.executeBatch();
            //提交
            conn.commit();
            System.out.println("car数据完成.");
            //关闭连接
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将gps设备信息写入数据库，暂时只写一个primary key，其它的后期有需要再加
     */
    public void updateGpsDeviceData() {
        Connection conn = dbCon.getConnection();
        try {
            //关闭自动连接
            conn.setAutoCommit(false);
            //使用PreparedStatement代替Statement，可以大大提高效率
            PreparedStatement preparedStatement = conn.prepareStatement("");
            //sql前缀
            String preSql = "INSERT INTO gps (gpsid) VALUES ";
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 1; i <= 10000; i++) {
                String updateGpsSql = "("
                        + "\'" + i + "\'"
                        + "),";
                gpsIDList.add(Integer.toString(i));
                stringBuffer.append(updateGpsSql);
            }
            String gpsSql = preSql + stringBuffer.substring(0, stringBuffer.length() - 1);
            preparedStatement.addBatch(gpsSql);
            //提交事务
            conn.commit();
            System.out.println("gps设备数据完成");
            //关闭连接
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将车的信息和gps设备信息关联
     */
    public void updateCarAndGpsData() {
        Connection conn = dbCon.getConnection();
        try {
            //关闭自动连接
            conn.setAutoCommit(false);
            //使用PreparedStatement代替Statement，提高效率
            PreparedStatement preparedStatement = conn.prepareStatement("");
            String preSql = "INSERT INTO cardevice (carid,gpsid) VALUES ";
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < carIDList.size(); i++) {
                String updateCarAndGpsSql = "("
                        + "\'" + carIDList.get(i) + "\'" + ","
                        + "\'" + gpsIDList.get(i) + "\'"
                        + "),";
                sb.append(updateCarAndGpsSql);
            }
            String carAndGpsSql = preSql + sb.substring(0, sb.length() - 1);
            //添加事务
            preparedStatement.addBatch(carAndGpsSql);
            //执行事务
            preparedStatement.executeBatch();
            //提交
            conn.commit();
            System.out.println("car数据和gps数据关联完成");
            //关闭连接
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
