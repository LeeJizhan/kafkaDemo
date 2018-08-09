package drools.monitor;

import db.DBOper;
import drools.droolsbean.CarStatusRuturn;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import utils.LoggerUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Monitor {

    public NodeStatus nodeStatus = new NodeStatus();
    List<CarStatusRuturn> carStatusList;
    DBOper dbOper = new DBOper();

    private static ZooKeeper zk;
    private static final String CONNECT_STRING = "10.2.17.202:2182";
    private static final int SESSION_TIMEOUT = 5000;
    private static final String PARENT = "/rootTest/child4";
    private Map<Integer, String> myMap;

    public Map<Integer, String> getMyMap() {
        return myMap;
    }

    public void monitor(final String carlon, final String carlant) throws Exception {
        zk = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, new Watcher() {

            @Override
            public void process(WatchedEvent event) {
                String path = event.getPath();
                Event.EventType type = event.getType();
                Event.KeeperState state = event.getState();
                System.out.println(path + "\t" + type + "\t" + state);

                nodeStatus.setNodeChange("TRUE");
                try {
                    getMonitorAndData(carlon, carlant);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // 循环监听
                try {
                    zk.getChildren(PARENT, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 添加监听
        zk.getChildren(PARENT, true);
    }

    public void getMonitorAndData(String carlon, String carlant) throws SQLException {
        myMap = new HashMap<>();
        if (nodeStatus.getNodeChange() == "TRUE") {
            LoggerUtil.info(nodeStatus.getNodeChange() + "NodeChildrenChanged");
            carStatusList = dbOper.weiLandate(carlon, carlant);
            for (CarStatusRuturn statusRuturn : carStatusList) {
                LoggerUtil.info(statusRuturn.getwLID() + ": " + statusRuturn.getcStatus());
                myMap.put(statusRuturn.getwLID(), statusRuturn.getcStatus());
            }
        }
    }

}
