package hbase;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import properties.HBaseTableProperties;
import utils.LoggerUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void create(String tableName, String serFamilyStr) {
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
    public void insert(String tableName, String rowKey, String family, Map<String, Object> columns) {
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
     * 获取行键指定行的 所有列族、所有列的最新版本数据
     *
     * @param tableName
     * @param rowKey
     * @return
     */
    public Map<String, String> getNewDataByRowKey(String tableName, String rowKey) {
        Map<String, String> map = new HashMap<>();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowKey.getBytes());
            Result result = table.get(get);
            for (Cell cell : result.rawCells()) {
                map.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取行键指定行的 指定列族、所有列的最新版本数据
     *
     * @param tableName
     * @param rowKey
     * @param family
     * @return
     */
    public Map<String, String> getNewDataByRowKeyAndFamily(String tableName, String rowKey, String family) {
        Map<String, String> map = new HashMap<>();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowKey.getBytes());
            get.addFamily(family.getBytes());
            Result result = table.get(get);
            for (Cell cell : result.rawCells()) {
                map.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取行键指定行的 指定列族、指定列的最新版本数据
     *
     * @param tableName
     * @param rowkey
     * @param family
     * @param column
     * @return
     */
    public Map<String, String> getNewDataByRowKeyAndFamilyAndColumn(String tableName, String rowkey, String family, String column) {
        Map<String, String> map = new HashMap<>();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowkey.getBytes());
            get.addColumn(family.getBytes(), column.getBytes());
            Result result = table.get(get);
            for (Cell cell : result.rawCells()) {
                map.put(Bytes.toString(CellUtil.cloneQualifier(cell)), Bytes.toString(CellUtil.cloneValue(cell)));
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取行键指定行的 指定列族的所有行的所有版本数据
     *
     * @param tableName
     * @param rowkey
     * @param family
     * @return
     */
    public Map<String, List<String>> getAllDataByRowKeyAndFamily(String tableName, String rowkey, String family) {
        Map<String, List<String>> myMap = new HashMap<>();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowkey.getBytes());
            get.addFamily(family.getBytes());
            get.setMaxVersions();
            Result result = table.get(get);
            List<Cell> cells = result.listCells();
            for (int i = 0; i < cells.size(); i++) {
                Cell currentCell = cells.get(i);
                String column = Bytes.toString(CellUtil.cloneQualifier(currentCell));
                String value = Bytes.toString(CellUtil.cloneValue(currentCell));
                if (!myMap.containsKey(column)) {
                    List<String> list = new ArrayList<>();
                    list.add(value);
                    myMap.put(column, list);
                } else {
                    myMap.get(column).add(value);
                }
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myMap;
    }

    /**
     * 获取行键指定行的 指定列族、指定列的所有版本数据
     *
     * @param tableName
     * @param rowKey
     * @param family
     * @param column
     * @return
     */
    public List<String> getAllDataByRowKeyAndFamilyAndColumn(String tableName, String rowKey, String family, String column) {
        List<String> list = new ArrayList<>();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowKey.getBytes());
            get.addColumn(family.getBytes(), column.getBytes());
            get.setMaxVersions();
            Result result = table.get(get);
            for (Cell cell : result.rawCells()) {
                list.add(Bytes.toString(CellUtil.cloneValue(cell)));
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 删除HBase中的表
     *
     * @param tableName
     */
    public void dropTable(String tableName) {
        try {
            TableName name = TableName.valueOf(tableName);
            admin.disableTable(name);
            admin.deleteTable(name);
            LoggerUtil.info("删除表" + name + "成功!");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        HBaseTableProperties tableProperties = HBaseTableProperties.getInstance();
        String tableName = tableProperties.getTableName();
        String families = tableProperties.getFamilies();
        String[] family = families.split(",");
        String gpsFamily = family[0];
        String ususlFamily = family[1];
        String finalFamily = family[2];
        HBaseOper hBaseOper = new HBaseOper();
//        hBaseOper.dropTable(tableName);
//        hBaseOper.create(tableName, families);
        //hBaseOper.dropTable(tableName);
        //创建表gpsdata
        //hBaseOper.create(tableName, serFamily);
        //使用键值对进行数据保存，插入
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("lon", "121.4");
//        map.put("lat", "31.2");
//        hBaseOper.insert(tableName, "1", "moreinfo", map);
//        map.put("name", "tony");
//        map.put("sex", "girl");
//        hBaseOper.insert(tableName, "1", "userinfo", map);
        Map<String, List<String>> map = hBaseOper.getAllDataByRowKeyAndFamily(tableName, "1", gpsFamily);
        for (Map.Entry entry : map.entrySet()) {
            String key = (String) entry.getKey();
            List<String> list = (List<String>) entry.getValue();
            for (String s : list) {
                System.out.println(key + ": " + s);
            }
        }
//        Map<String, String> resultMap = hBaseOper.getNewDataByRowKeyAndFamily(tableName, "1", gpsFamily);
//        for (Map.Entry entry : resultMap.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }
//        hBaseOper.delete(tableName, gpsFamily, "lon", "1");
//        hBaseOper.delete(tableName, gpsFamily, "lat", "1");
    }
}
