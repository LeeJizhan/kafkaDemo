# kafkaDemo
### 主要内容
- kafka消息队列
- 模拟小车gps轨迹数据生成
- 从MySQL读取车辆轨迹数据,生成Json格式，发送到kafka消息队列中
- kafka消息队列与Spark Streaming对接进行数据的实时处理
- 将处理后的数据存入HBase

### HBaseOper 关方法说明
1. public void create(String tableName, String serFamilyStr)
功能：创建表
参数：1）tableName：表名;
     2）serFamilyStr：由","分隔的列族名.
返回：无
示例：
```
HBaseOper oper = new HBaseOper();
String tableName = "User";
String serFamilyStr = "userInfo,moreInfo";
HBaseOper.create(tableName,serFamily);
```
2.  public void insert(String tableName, String rowKey, String family, Map<String, Object> columns)
功能：向HBase对应的表指定的rowkey、列族插入数据
参数：1）tableName：表名;
     2）rowKey：行键;
     3）family：列族名;
     4）columns：列名和值的map集.
返回：无
示例：
```
HBaseOper oper = new HBaseOper();
String tableName = "User";
Map<String, Object> map = new HashMap<String, Object>();
map.put("name", "tony");
map.put("sex", "girl");
oper.insert(tableName, "1", "userInfo", map);
```
3. public Map<String, String> getNewDataByRowKey(String tableName, String rowKey)
功能：获取对应行键的所有列族、所有列的最新版本数据
参数：1）tableName：表名;
     2）rowKey：行键.
返回：Map<String, String> 前一个String表示列名，后一个String表示对应的值
示例：
```
HBaseOper oper = new HBaseOper();
String tableName = "User";
oper.getNewDataByRowKey(tableName, "1");
```
4.  public Map<String, String> getNewDataByRowKeyAndFamily(String tableName, String rowKey, String family)
功能：获取对应行键的指定列族、所有列的最新版本数据
参数：1）tableName：表名;
     2）rowKey：行键;
     3）family：列族名.
返回：Map<String, String> 前一个String表示列名，后一个String表示对应的值
示例：
```
HBaseOper oper = new HBaseOper();
String tableName = "User";
String family = "userInfo";
oper.getNewDataByRowKeyAndFamily(tableName, "1", family);
```
5. public Map<String, String> getNewDataByRowKeyAndFamilyAndColumn(String tableName, String rowKey, String family, String column)
功能：获取对应行键的指定列族、指定列的最新版本数据
参数：1）tableName：表名;
     2）rowKey：行键;
     3）family：列族名;
     4）column：列名.
返回：Map<String, String> 前一个String表示列名，后一个String表示对应的值
示例：
```
HBaseOper oper = new HBaseOper();
String tableName = "User";
String family = "userInfo";
String column = "name";
oper.getNewDataByRowKeyAndFamilyAndColumn(tableName, "1", family, column);
```
6. public Map<String, List<String>> getAllDataByRowKeyAndFamily(String tableName, String rowKey, String family)
功能：获取对应行键的指定列族的所有版本数据
参数：1）tableName：表名;
     2）rowKey：行键;
     3）family：列族名.
返回：Map<String, List<String>> String表示列名，List<String>表示对应列的所有版本的值
示例：
```
HBaseOper oper = new HBaseOper();
String tableName = "User";
String family = "userInfo";
oper.getAllDataByRowKeyAndFamily(tableName, "1", family);
```
7. public List<String> getAllDataByRowKeyAndFamilyAndColumn(String tableName, String rowKey, String family, String column) 
功能：获取对应行键的指定列族的所有版本数据
参数：1）tableName：表名;
     2）rowKey：行键;
     3）family：列族名;
     4）column：列名.
返回：List<String> 表示对应列的所有版本的值
示例：
```
HBaseOper oper = new HBaseOper();
String tableName = "User";
String family = "userInfo";
String column = "name";
oper.getAllDataByRowKeyAndFamilyAndColumn(tableName, "1", family, column);
```
8. public void dropTable(String tableName) 
功能：删除HBase中对应的表
其他：略
9. public void delete(String tableName, String rowkey, String family)
功能：删除表对应的行的列族的数据
其他：略
10. public void delete(String tableName, String rowkey, String family, String column)
功能：删除表某行对应列族某列的数据
其他：略
