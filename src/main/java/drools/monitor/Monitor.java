package drools.monitor;

import db.DBCon;
import drools.droolsbean.*;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import utils.LoggerUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Monitor extends Thread {

    public NodeStatus nodeStatus = new NodeStatus();

    private static ZooKeeper zk;
    private static final String CONNECT_STRING = "10.2.17.202:2182";
    private static final int SESSION_TIMEOUT = 5000;
    private static final String PARENT = "/rootTest/child4";

    private static Connection connection;

    private Map<Integer, String> map = new HashMap<>();
    //规则引擎
    KieServices ks = KieServices.Factory.get();
    KieContainer kieContainer = ks.getKieClasspathContainer();

    public Monitor() {
        DBCon dbCon = DBCon.getInstance();
        this.connection = dbCon.getConnection();
    }

    @Override
    public void run() {
        try {
            zk = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, event -> {
                String path = event.getPath();
                Watcher.Event.EventType type = event.getType();
                Watcher.Event.KeeperState state = event.getState();
                LoggerUtil.info(path + "\t" + type + "\t" + state);

                nodeStatus.setNodeChange("TRUE");
                //更新缓存中的规则
                updateRules();

                // 循环监听
                try {
                    zk.getChildren(PARENT, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 添加监听
        try {
            zk.getChildren(PARENT, true);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LoggerUtil.info(Thread.currentThread().getName());
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    /**
//     * zookeeper 监听
//     *
//     * @throws Exception
//     */
//    public void monitor() throws Exception {
//        zk = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, event -> {
//            String path = event.getPath();
//            Watcher.Event.EventType type = event.getType();
//            Watcher.Event.KeeperState state = event.getState();
//            LoggerUtil.info(path + "\t" + type + "\t" + state);
//
//            nodeStatus.setNodeChange("TRUE");
//            //更新缓存中的规则
//            updateRules();
//
//            // 循环监听
//            try {
//                zk.getChildren(PARENT, true);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//        // 添加监听
//        zk.getChildren(PARENT, true);
//        System.out.println(Thread.currentThread().getName());
//        Thread.sleep(Long.MAX_VALUE);
//    }

    /**
     * 更新缓存中的规则
     */
    private void updateRules() {
        if (!map.isEmpty()){
            map.clear();
            LoggerUtil.info("map清除成功");
        }
        if (nodeStatus.getNodeChange().equals("TRUE")) {
            /**
             * 将数据库中的规则写入缓存中
             */
            //连接数据库
            try {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement("");
                String querySql = "select * from fenceinfo";
                ResultSet resultSet = preparedStatement.executeQuery(querySql);
                while (resultSet.next()) {
                    //获取MYSQL中规则id数据
                    int id = resultSet.getInt("RuleId");
                    //获取MYSQL中围栏名称数据
                    String weiLanName = resultSet.getString("fencename");
                    double lon = resultSet.getDouble("centerlon");
                    double lat = resultSet.getFloat("centerlat");
                    double radius = resultSet.getFloat("radius");
                    String str = weiLanName + ","
                            + lon + ","
                            + lat + ","
                            + radius;
                    map.put(id, str);
                    LoggerUtil.info("更新规则：" + id);
                }
                LoggerUtil.info("更新规则成功!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            nodeStatus.setNodeChange("FALSE");
        }
    }

    /**
     * 获取所有规则对应的车辆状态
     *
     * @param carLon
     * @param carLat
     * @return
     * @throws SQLException
     */
    public List<CarStatusRuturn> getMonitorAndData(String carLon, String carLat) throws SQLException {
        List<CarStatusRuturn> carStatusList = new ArrayList<>();
        /**
         * 将map中的数据取出来
         */
        if (!map.isEmpty()) {
            for (Map.Entry entry : map.entrySet()) {
                WeiLan weiLan = new WeiLan();
                Car car = new Car();
                CarDataDrools carData = new CarDataDrools();
                CarStatusRuturn carStatusRuturn = new CarStatusRuturn();

                int ruleId = (int) entry.getKey();
                String value = (String) entry.getValue();
                String[] strs = value.split(",");
                String weiLanName = strs[0];
                double lon = Double.parseDouble(strs[1]);
                double lat = Double.parseDouble(strs[2]);
                double radius = Double.parseDouble(strs[3]);

                weiLan.setRuleId(ruleId);
                weiLan.setWeiLanName(weiLanName);
                weiLan.setLon(lon);
                weiLan.setLat(lat);
                weiLan.setRadius(radius);

                double distance = LngAndLatDistance.getLngAndLat(weiLan, carData.cardata(carLon, carLat));
                car.setDistance(distance);
                //调用规则引擎
                KieSession kieSession = kieContainer.newKieSession("weiLan");

                kieSession.insert(weiLan);
                kieSession.insert(car);
                kieSession.fireAllRules();

                carStatusRuturn.setwLID(ruleId);
                carStatusRuturn.setcStatus(car.getType());
                carStatusList.add(carStatusRuturn);
            }
        } else {
            try {
                throw new Exception("Map是空的.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return carStatusList;
    }

}
