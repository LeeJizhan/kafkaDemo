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
    private String families;

    public String getTableName() {
        return tableName;
    }

    public String getFamilies() {
        return families;
    }

    private HBaseTableProperties() {
        Properties props = new Properties();
        //当前类通过输入流来读取配置文件
        InputStream inputStream = HBConn.class.getResourceAsStream("/hbasetable.properties");
        try {
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tableName = props.getProperty("tablename");
        this.families = props.getProperty("families");
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
