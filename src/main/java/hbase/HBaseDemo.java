package hbase;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.jets3t.service.model.container.ObjectKeyAndVersion;
import utils.LoggerUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Asus- on 2018/7/26.
 */
public class HBaseDemo {
    private static HBaseAdmin admin;
    private static Configuration configuration;
    private static Connection connection;

    public HBaseDemo() {
        initHBse();
    }

    private void initHBse() {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "10.2.17.204");
        configuration.set("hbase.zookeeper.property.clientPort", "2182");
        try {
            this.connection = ConnectionFactory.createConnection(configuration);
            this.admin = (HBaseAdmin) connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * HBase-创建表
     *
     * @param tableName
     * @param seriesStr
     */
    private void create(String tableName, String seriesStr) {
        /**
         * 1.得到表名
         * 2.判断数据库中是否已经含有该表
         * 3.创建一个HTableDescriptor对象实例
         * 4.将字符串切割，得到家族簇集
         * 5.将每一个家族簇加入到HTableDescriptor对象实例中
         * 6.创建该表
         */
        //1.得到表名
        TableName name = TableName.valueOf(tableName);
        try {
            //2.判断数据库中是否已经含有该表
            if (!admin.tableExists(name)) {
                LoggerUtil.info("不存在表: " + name);
                LoggerUtil.info("正在创建表: " + name + "...");
                //3.创建一个HTableDescriptor对象实例
                HTableDescriptor tableDescriptor = new HTableDescriptor(name);
                //4.4.将字符串切割，得到家族簇集
                String[] series = seriesStr.split(",");
                //5.将每一个家族簇加入到HTableDescriptor对象实例中
                for (String s : series) {
                    tableDescriptor.addFamily(new HColumnDescriptor(s.getBytes()));
                }
                //6.创建该表
                admin.createTable(tableDescriptor);
                LoggerUtil.info("创建表：" + name + "成功!");
            } else {
                throw new Exception("数据库中已经含有该表，不用再创建!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(admin);
        }
    }

    /**
     * 插入数据
     *
     * @param tableName
     * @param rowKey
     * @param family
     * @param columns
     */
    private void insert(String tableName, String rowKey, String family, Map<String, Object> columns) {
        /**
         * 1.得到tableName
         * 2.根据tableName查找表
         * 3.创建一个put对象实例
         * 4.取出map中的数据放到put中
         * 5.调用table的put方法将put中的数据插入到表中
         */
        //1.得到tableName
        TableName name = TableName.valueOf(tableName);
        try {
            //2.根据tableName查找表
            Table table = connection.getTable(name);
            if (table != null) {
                //3.创建一个put对象实例
                Put put = new Put(Bytes.toBytes(rowKey));
                //4.取出map中的数据放到put中
                for (Map.Entry<String, Object> entry : columns.entrySet()) {
                    put.addColumn(family.getBytes(), Bytes.toBytes(entry.getKey()), Bytes.toBytes(String.valueOf(entry.getValue())));
                }
                //5.调用table的put方法将put中的数据插入到表中
                table.put(put);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(admin);
        }
    }

    /**
     * 查询数据 根据rowKey进行查询
     *
     * @param rowKey
     * @param tableName
     * @param family
     * @return
     */
    private Map<String, String> search(String tableName, String family, String rowKey) {
        /**
         * 1.得到tableName
         * 2.根据tableName得到table
         * 3.判断table是否存在
         * 4.创建一个Get对象实例
         * 5.得到返回的Result集
         * 6.根据家族簇名得到Map集
         * 7.遍历Mao集，将它转换成我们需要的Map
         */
        //1.得到tableName
        TableName name = TableName.valueOf(tableName);
        Map<String, String> resultMap = null;
        try {
            //2.根据tableName得到table
            Table table = connection.getTable(name);
            //3.判断table是否存在
            if (table != null) {
                //4.创建一个Get对象实例
                Get get = new Get(Bytes.toBytes(rowKey));
                get.addFamily(family.getBytes());
                //5.得到返回的Result集
                Result result = table.get(get);
                //6.根据家族簇名得到Map集
                Map<byte[], byte[]> map = result.getFamilyMap(family.getBytes());
                //7.遍历Mao集，将它转换成我们需要的Map
                Iterator<Map.Entry<byte[], byte[]>> it = map.entrySet().iterator();
                resultMap = new HashMap<String, String>();
                while (it.hasNext()) {
                    Map.Entry<byte[], byte[]> entry = it.next();
                    resultMap.put(Bytes.toString(entry.getKey()), Bytes.toString(entry.getValue()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(admin);
        }
        return resultMap;
    }

    /**
     * 根据rowKey和column查询数据
     *
     * @param tableName
     * @param family
     * @param rowKey
     * @param column
     * @return
     */
    private String search(String tableName, String family, String rowKey, String column) {
        TableName name = TableName.valueOf(tableName);
        String resultStr = "";
        try {
            Table table = connection.getTable(name);
            if (table != null) {
                Get get = new Get(Bytes.toBytes(rowKey));
                get.addColumn(Bytes.toBytes(column), Bytes.toBytes(family));
                Result result = table.get(get);
                byte[] bytes = result.getValue(Bytes.toBytes(family), Bytes.toBytes(column));
                resultStr = Bytes.toString(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(admin);
        }
        return resultStr;
    }

    public static void main(String[] args) {
        HBaseDemo hBaseDemo = new HBaseDemo();
    }
}
