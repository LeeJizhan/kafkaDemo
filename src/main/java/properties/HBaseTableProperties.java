package properties;

import hbase.HBConn;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Asus- on 2018/8/3.
 */
public class HBaseTableProperties {

    private static volatile HBaseTableProperties tableProperties = null;

    private String tableName;
    private String family1;
    private String family2;

    public String getTableName() {
        return tableName;
    }

    public String getFamily1() {
        return family1;
    }

    public String getFamily2() {
        return family2;
    }

    private HBaseTableProperties() {
        Properties props = new Properties();
        //当前类通过输入流来读取配置文件
        InputStream inputStream = HBConn.class.getResourceAsStream("/hbase.properties");
        try {
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tableName = props.getProperty("tableName");
        this.family1 = props.getProperty("family1");
        this.family2 = props.getProperty("family2");
    }

    public static HBaseTableProperties getInstance() {
        if (tableProperties == null) {
            synchronized (HBaseTableProperties.class) {
                if (tableProperties == null) {
                    tableProperties = new HBaseTableProperties();
                }
            }
        }
        return tableProperties;
    }
}
