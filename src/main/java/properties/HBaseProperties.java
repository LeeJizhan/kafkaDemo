package properties;

import hbase.HBConn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Asus- on 2018/8/3.
 */
public class HBaseProperties {

    private static volatile HBaseProperties properties = null;
    private String quorum;
    private String clientPort;

    public String getQuorum() {
        return quorum;
    }

    public String getClientPort() {
        return clientPort;
    }

    private HBaseProperties() {
        Properties props = new Properties();
        //当前类通过输入流来读取配置文件
        InputStream inputStream = HBConn.class.getResourceAsStream("/hbase.properties");
        try {
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.quorum = props.getProperty("quorum");
        this.clientPort = props.getProperty("clientPort");
    }

    public static HBaseProperties getInstance() {
        if (properties == null) {
            synchronized (HBaseProperties.class) {
                if (properties == null) {
                    properties = new HBaseProperties();
                }
            }
        }
        return properties;
    }
}
