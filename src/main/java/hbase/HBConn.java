package hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import properties.HBaseProperties;
import properties.KafkaProperties;
import utils.LoggerUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Asus- on 2018/8/2.
 */
public class HBConn {

    private static volatile HBConn hbConn = null;

    public Connection getConnection() {
        return connection;
    }

    private Connection connection;

    private HBConn() {
        Configuration configuration = new Configuration();
        HBaseProperties properties = HBaseProperties.getInstance();
        configuration.set("hbase.zookeeper.quorum", properties.getQuorum());
        configuration.set("hbase.zookeeper.property.clientPort", properties.getClientPort());
        try {
            connection = ConnectionFactory.createConnection(configuration);
            LoggerUtil.info("HBase连接成功!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HBConn getInstance() {
        if (hbConn == null) {
            synchronized (HBConn.class) {
                if (hbConn == null) {
                    hbConn = new HBConn();
                }
            }
        }
        return hbConn;
    }
}
