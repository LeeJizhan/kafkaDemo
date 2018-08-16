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

    private static Map<Integer, Map<String, String>> map = new HashMap<>();
    //规则引擎
    private static KieServices ks = KieServices.Factory.get();
    private static KieContainer kieContainer = ks.getKieClasspathContainer();
    //private static KieSession kieSession = null;

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
        synchronized (Monitor.class) {
            if (!map.isEmpty()) {
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
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            //获取MYSQL中规则id数据
                            int id = resultSet.getInt("RuleId");
                            //获取MYSQL中围栏名称数据
                            String weiLanName = resultSet.getString("fencename");
                            double lon = resultSet.getDouble("centerlon");
                            double lat = resultSet.getDouble("centerlat");
                            double radius = resultSet.getDouble("radius");
                            Map<String, String> strMap = new HashMap<>();
                            strMap.put("name", weiLanName);
                            strMap.put("lon", Double.toString(lon));
                            strMap.put("lat", Double.toString(lat));
                            strMap.put("radius", Double.toString(radius));
//                        String str = weiLanName + ","
//                                + lon + ","
//                                + lat + ","
//                                + radius;
                            map.put(id, strMap);
                            LoggerUtil.info("更新规则：" + id);
                        }
                        LoggerUtil.info("更新规则成功!");
                    } else {
                        LoggerUtil.info("没有规则!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                nodeStatus.setNodeChange("FALSE");
            }
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
    public static List<CarStatusRuturn> getMonitorAndData(String carLon, String carLat) throws SQLException {
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
                Map<String, String> value = (Map<String, String>) entry.getValue();
                String weiLanName = value.get("name");
                double lon = Double.valueOf(value.get("lon"));
                double lat = Double.valueOf(value.get("lat"));
                double radius = Double.valueOf(value.get("radius"));

                weiLan.setRuleId(ruleId);
                weiLan.setWeiLanName(weiLanName);
                weiLan.setLon(lon);
                weiLan.setLat(lat);
                weiLan.setRadius(radius);

                double distance = LngAndLatDistance.getLngAndLat(weiLan, carData.cardata(carLon, carLat));
                car.setDistance(distance);
                KieSession kieSession = kieContainer.newKieSession("weiLan");
                kieSession.insert(weiLan);
                kieSession.insert(car);
                kieSession.fireAllRules();

                carStatusRuturn.setwLID(ruleId);
                carStatusRuturn.setcStatus(car.getType());
                carStatusList.add(carStatusRuturn);
                if (kieSession != null) {
                    kieSession.dispose();
                }
            }
        }
        return carStatusList;
    }
}
