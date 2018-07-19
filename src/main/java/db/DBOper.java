package db;

import bean.CarBean;
import data.CarData;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
        Statement statement = null;
        try {
            statement = conn.createStatement();
            for (CarBean carBean : carBeanList) {
                String carid = carBean.getCarID();
                String brand = carBean.getBrand();
                String model = carBean.getModel();
                String number = carBean.getNumber();
                String owner = carBean.getOwner();
                String phone = carBean.getPhone();

                carIDList.add(carid);
                String updateCarSql = "insert into car values("
                        + "\'" + carid + "\'" + ","
                        + "\'" + brand + "\'" + ","
                        + "\'" + model + "\'" + ","
                        + "\'" + number + "\'" + ","
                        + "\'" + owner + "\'" + ","
                        + "\'" + phone + "\'"
                        + ")";   //SQL语句
                statement.executeUpdate(updateCarSql);
            }
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
            Statement statement = conn.createStatement();
            for (int i = 1; i <= 1000; i++) {
                String updateGpsSql = "INSERT INTO gps VALUES ("
                        + "\'" + i + "\'"
                        + ")";
                gpsIDList.add(Integer.toString(i));
                statement.executeUpdate(updateGpsSql);
            }
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
            Statement statement = conn.createStatement();
            for (int i = 0; i < carIDList.size(); i++) {
                String updateCarAndGpsSql = "INSERT INTO cardevice VALUES ("
                        + "\'" + carIDList.get(i) + "\'" + ","
                        + "\'" + gpsIDList.get(i) + "\'"
                        + ")";
                statement.executeUpdate(updateCarAndGpsSql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
