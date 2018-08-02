package hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
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
        Properties props = new Properties();
        //当前类通过输入流来读取配置文件
        InputStream inputStream = HBConn.class.getResourceAsStream("/hbase.properties");
        try {
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Configuration configuration = new Configuration();
        configuration.set("hbase.zookeeper.quorum", props.getProperty("quorum"));
        configuration.set("hbase.zookeeper.property.clientPort", props.getProperty("clientPort"));
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
