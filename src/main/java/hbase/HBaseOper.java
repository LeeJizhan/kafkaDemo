package hbase;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import utils.LoggerUtil;

import java.io.IOException;
import java.util.*;

/**
 * Created by Asus- on 2018/7/26.
 */
public class HBaseOper {
    private static HBaseAdmin admin;
    private static Connection connection;

    public HBaseOper() {
        initHBse();
    }

    private void initHBse() {
        try {
            HBConn hbConn = HBConn.getInstance();
            this.connection = hbConn.getConnection();
            this.admin = (HBaseAdmin) connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * HBase-创建表
     *
     * @param tableName
     * @param serFamilyStr
     */
    private void create(String tableName, String serFamilyStr) {
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
            LoggerUtil.info("不存在表: " + name);
            LoggerUtil.info("正在创建表: " + name + "...");
            //3.创建一个HTableDescriptor对象实例
            HTableDescriptor tableDescriptor = new HTableDescriptor(name);
            //4.4.将字符串切割，得到家族簇集
            String[] series = serFamilyStr.split(",");
            //5.将每一个家族簇加入到HTableDescriptor对象实例中
            for (String s : series) {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(s.getBytes());
                hColumnDescriptor.setMaxVersions(2500);
                tableDescriptor.addFamily(hColumnDescriptor);
            }
            //6.创建该表
            admin.createTable(tableDescriptor);
            LoggerUtil.info("创建表：" + name + "成功!");
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
//                put.setTimestamp(System.currentTimeMillis());
                //4.取出map中的数据放到put中
                for (Map.Entry<String, Object> entry : columns.entrySet()) {
                    LoggerUtil.info(entry.getKey() + ": " + entry.getValue());
                    put.addColumn(family.getBytes(), Bytes.toBytes(entry.getKey()), Bytes.toBytes(String.valueOf(entry.getValue())));
                }
                //5.调用table的put方法将put中的数据插入到表中
                table.put(put);
                LoggerUtil.info("插入数据成功!");
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
                int maxVersion = get.getMaxVersions();
                get.setMaxVersions(maxVersion);
                System.out.println(maxVersion);
                //5.得到返回的Result集
                Result result = table.get(get);
                //6.根据家族簇名得到Map集
                Map<byte[], byte[]> map = result.getFamilyMap(family.getBytes());
                //7.遍历Map集，将它转换成我们需要的Map
                if (map != null) {
                    Iterator<Map.Entry<byte[], byte[]>> it = map.entrySet().iterator();
                    resultMap = new HashMap<String, String>();
                    while (it.hasNext()) {
                        Map.Entry<byte[], byte[]> entry = it.next();
                        resultMap.put(Bytes.toString(entry.getKey()), Bytes.toString(entry.getValue()));
                    }
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
    private String search(String tableName, String family, String column, String rowKey) {
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

    /**
     * 删除HBase中的表
     *
     * @param tableName
     */
    public static void dropTable(String tableName) {
        try {
            TableName name = TableName.valueOf(tableName);
            admin.disableTable(name);
            admin.deleteTable(name);
            LoggerUtil.info("删除表" + name + "成功!");
        } catch (MasterNotRunningException e) {
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询数据，返回对应列族的列的
     *
     * @param tableName
     * @param family
     * @param column
     * @return
     * @throws IOException
     */
    public List<String> read(String tableName, String family, String column) throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        scan.addColumn(family.getBytes(), column.getBytes());
        scan.setMaxVersions(3000);//设置读取的最大的版本数
        ResultScanner r = table.getScanner(scan);
        List<String> list = new ArrayList<String>();
        for (Result result : r) {
            for (Cell kv : result.rawCells()) {
                list.add(Bytes.toString(kv.getRowArray()));
            }
        }
        System.out.println(list.size());
        table.close();
        return list;
    }

    /**
     * 删除表对应的行的列族的数据
     *
     * @param tableName
     * @param family
     * @param rowkey
     */
    public void delete(String tableName, String family, String rowkey) {
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(rowkey.getBytes());
            delete.addFamily(family.getBytes());
            table.delete(delete);
            LoggerUtil.info("删除列族 " + family + " 的数据成功");
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除表某行对应列族某列的数据
     *
     * @param tableName
     * @param family
     * @param column
     * @param rowkey
     */
    public void delete(String tableName, String family, String column, String rowkey) {
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(rowkey.getBytes());
//            delete.addFamily(family.getBytes());
            delete.addColumn(family.getBytes(), column.getBytes());
            table.delete(delete);
            LoggerUtil.info("删除列 " + column + " 的数据成功!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        String tableName = "gpsdata";
        String gpsFamily = "gpsinfo";
        String ususlFamily = "usualstopinfo";
        String finalFamily = "finalstopinfo";
        String serFamily = "gpsinfo,usualstopinfo,finalstopinfo";
        HBaseOper hBaseOper = new HBaseOper();
        //hBaseOper.dropTable(tableName);
        //创建表gpsdata
        //hBaseOper.create(tableName, serFamily);
        //使用键值对进行数据保存，插入
        //Map<String, Object> gpsMap = new HashMap<String, Object>();
        //gpsMap.put("lon", "121.469600");
        //gpsMap.put("lat", "31.291600");
        //hBaseOper.insert(tableName, "1", gpsFamily, gpsMap);
        Map<String, String> resultMap = hBaseOper.search(tableName, gpsFamily, "1");
        for (Map.Entry entry : resultMap.entrySet()) {
            System.out.println(entry.getValue());
        }
//        hBaseOper.delete(tableName, gpsFamily, "lon", "1");
//        hBaseOper.delete(tableName, gpsFamily, "lat", "1");
    }
}
