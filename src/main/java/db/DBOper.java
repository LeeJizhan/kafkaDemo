package db;

import bean.CarBean;
import data.CarData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Asus- on 2018/7/19.
 */
public class DBOper {

    private DBCon dbCon;
    private List<String> carIDList = new ArrayList<>();
    private List<String> gpsIDList = new ArrayList<>();
    private Connection conn;

    public DBOper() {
        this.dbCon = DBCon.getInstance();
        this.conn = dbCon.getConnection();
    }

    /**
     * 将车的信息写入数据库
     */
    public void updateCarData() {
        CarData carData = new CarData();
        List<CarBean> carBeanList = carData.getCarData();
        //创建数据库连接
//        Connection conn = dbCon.getConnection();
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
            System.out.println("car数据更新完成!");
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
//            conn.setAutoCommit(false);
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
            preparedStatement.executeBatch();
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
//        Connection conn = dbCon.getConnection();
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
            System.out.println("car数据和gps数据关联完成!");
            //关闭连接
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询车辆数据
     *
     * @param index    开始查询的坐标
     * @param perCount 一次查询的总数
     */
    public List<String> search(int index, int perCount) {
        if (index < 0) {
            try {
                throw new Exception("index应该大于0");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (perCount > 1000000) {
            try {
                throw new Exception("perCount不要大于1000000");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<String> messages = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("");
            ResultSet resultSet;
            String sql = "select * FROM gpsdata ORDER BY time LIMIT " + index + "," + perCount;
            resultSet = preparedStatement.executeQuery(sql);
            String msg;
            while (resultSet.next()) {
                String gpsid = resultSet.getString("gpsid");
                //将time转换成当前日期，时间不变
                String time = transform(resultSet.getString("time"));
                String lon = resultSet.getString("lon");
                String lat = resultSet.getString("lat");
                String bearing = resultSet.getString("bearing");
                String speed = resultSet.getString("speed");
                msg = "{"
                        + "\"gpsid\"" + ":" + "\"" + gpsid + "\"" + ","
                        + "\"time\"" + ":" + "\"" + time + "\"" + ","
                        + "\"lon\"" + ":" + "\"" + lon + "\"" + ","
                        + "\"lat\"" + ":" + "\"" + lat + "\"" + ","
                        + "\"bearing\"" + ":" + "\"" + bearing + "\"" + ","
                        + "\"speed\"" + ":" + "\"" + speed + "\""
                        + "}"
                ;
                messages.add(msg);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * 转换时间
     *
     * @param time
     * @return
     */
    private String transform(String time) {
        if (time == null) {
            return null;
        }
        //获取当前日期
        Date dateNow = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(dateNow);
        String[] dates = time.split(" ");
        String reallyDate = date + " " + dates[1];
        return reallyDate;
    }
}