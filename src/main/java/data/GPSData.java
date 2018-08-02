package data; /**
 * Created by Asus- on 2018/7/17.
 */

import bean.GpsBean;

import java.text.SimpleDateFormat;
import java.util.*;

public class GPSData {

    private static int sigDigits = 8;
    //地球半径
    private static double radiusEarth = 6372.796924d;

    private static boolean isRun;

    private static List<GpsBean> gpsBeanList = new ArrayList<GpsBean>();

    private List<String> gpsJsons = new ArrayList<String>();

    //carNum,地图上显示的车辆数量 0<carNum<=10000
    private static final int carNum = 6;
    //依次为北京经纬度,上海经纬度,广州经纬度,深圳经纬度,珠海经纬度
    private static final String[] LatAndLon = {
            "39.911066,116.413610",
            "31.236342,121.480329",
            "23.135166,113.270813",
            "22.608554,114.066134",
            "22.261019,113.563614",
    };

    private static Map<Integer, Double> latMap = new HashMap<Integer, Double>();
    private static Map<Integer, Double> lonMap = new HashMap<Integer, Double>();
    private static Map<Integer, Double> bearingMap = new HashMap<Integer, Double>();
    private static Map<Integer, Double> distanceMap = new HashMap<Integer, Double>();
    private static Map<Integer, Boolean> isStopMap = new HashMap<Integer, Boolean>();

    public GPSData() {
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        for (int i = 1; i <= carNum; i++) {
            double[] start = setStart();
            latMap.put(i, start[0]);
            lonMap.put(i, start[1]);
            bearingMap.put(i, 0.0);
            distanceMap.put(i, 0.001);
            isStopMap.put(i, false);
        }
    }

    /**
     * 设置初始的经纬度
     *
     * @return
     */
    private double[] setStart() {
        double[] result = new double[2];
        String gps = getLatAndLon();
        String[] latAndLon = gps.split(",");
        double lat = Double.valueOf(latAndLon[0]);
        double lon = Double.valueOf(latAndLon[1]);
        //纬度变化0-0.02
        double latRand = new Random().nextDouble() / 50.0;
        int plus2 = new Random().nextInt(65535) % 2;
        if (plus2 == 0) {
            result[0] = lat - latRand;
        } else {
            result[0] = lat + latRand;
        }
        //经度变化在0-0.025
        double lonRand = new Random().nextDouble() / 40.0;
        int plus = new Random().nextInt(65535) % 2;
        if (plus == 0) {
            result[1] = lon - lonRand;
        } else {
            result[1] = lon + lonRand;
        }
        return result;
    }

    /**
     * 计算每辆车的位置信息
     *
     * @throws InterruptedException
     */
    private void fitData() throws InterruptedException {
        if (!gpsBeanList.isEmpty()) {
            lonMap.clear();
            latMap.clear();
            bearingMap.clear();
            distanceMap.clear();
            isStopMap.clear();
            //更新汽车位置信息
            for (GpsBean bean : gpsBeanList) {
                lonMap.put(Integer.valueOf(bean.getGpsID()), Double.valueOf(bean.getLongitude()));
                latMap.put(Integer.valueOf(bean.getGpsID()), Double.valueOf(bean.getLatitude()));
                bearingMap.put(Integer.valueOf(bean.getGpsID()), Double.valueOf(bean.getBearing()));
                distanceMap.put(Integer.valueOf(bean.getGpsID()), Double.valueOf(bean.getDistance()));
            }
            gpsBeanList.clear();
        }
        //重新计算车辆位置
        for (int i = 1; i <= carNum; i++) {
            double distance = distanceMap.get(i);
            if (distance != 0.0) {
                isRun = true;
            } else {
                isRun = false;
            }
            if (isRun) {//如果是运动的车，那么5%的概率停车
                double percent = new Random().nextDouble();
                if (percent >= 0.95) {
                    isRun = false;
                } else {
                    isRun = true;
                }
                double startLat = latMap.get(i);
                double startLon = lonMap.get(i);
                double bearing = bearingMap.get(i);
                if (isRun) {
                    gpsBeanList.add(carRun(Integer.toString(i), startLat, startLon, 0.1));
                } else {
                    gpsBeanList.add(carStop(Integer.toString(i), startLat, startLon, bearing));
                }
            } else {//如果是静止的车，那么5%的概率开车
                double percent = new Random().nextDouble();
                if (percent <= 0.95) {
                    isRun = false;
                } else {
                    isRun = true;
                }
                double startLat = latMap.get(i);
                double startLon = lonMap.get(i);
                double bearing = bearingMap.get(i);
                if (isRun) {
                    gpsBeanList.add(carRun(Integer.toString(i), startLat, startLon, 0.1));
                } else {
                    gpsBeanList.add(carStop(Integer.toString(i), startLat, startLon, bearing));
                }
            }
        }
    }

    /**
     * 对外接口，得到json数据集
     *
     * @return
     * @throws InterruptedException
     */
    public List<String> getData() throws InterruptedException {
        fitData();
        String msg;
        if (!gpsJsons.isEmpty()) {
            gpsJsons.clear();
        }
        for (GpsBean bean : gpsBeanList) {
            msg = "{"
                    + "\"gpsdata\"" + ":"
                    + "{"
                    + "\"gpsid\"" + ":" + "\"" + bean.getGpsID() + "\"" + ","
                    + "\"time\"" + ":" + "\"" + bean.getTime() + "\"" + ","
                    + "\"lon\"" + ":" + "\"" + bean.getLongitude() + "\"" + ","
                    + "\"lat\"" + ":" + "\"" + bean.getLatitude() + "\"" + ","
                    + "\"bearing\"" + ":" + "\"" + bean.getBearing() + "\"" + ","
                    + "\"distance\"" + ":" + "\"" + bean.getDistance() + "\""
                    + "}"
                    + "}";
            gpsJsons.add(msg);
        }
        return gpsJsons;
    }

    /**
     * 车辆运动
     *
     * @param gpsID
     * @param startlat
     * @param startlon
     * @param maxdist
     * @return
     * @throws InterruptedException
     */
    private GpsBean carRun(String gpsID, double startlat, double startlon, double maxdist) throws InterruptedException {
        double finalLat;
        double finalLon;
        double[] brg = new double[]{0, 180, 0};
        int j = 0;
        if (startlat == -90) {
            startlat = -89.99999999;
            j = 2;
        }
        //起始经纬度
        startlat = rad(startlat);
        startlon = rad(startlon);

        maxdist = maxdist / radiusEarth;
        double sinstartlat = Math.sin(startlat);
        double cosstartlat = Math.cos(startlat);
        double distance;
        double rad360 = 2 * Math.PI;//圆周率
        //生成0-1的随机数
        double rand1 = new Random().nextDouble();
        distance = Math.acos(rand1 * (Math.cos(maxdist) - 1) + 1);//随机数
        brg[0] = rad360 * new Random().nextDouble();
        //最终点的经纬度
        finalLat = Math.asin(sinstartlat * Math.cos(distance) + cosstartlat * Math.sin(distance) * Math.cos(brg[0]));
        finalLon = deg(normalizeLongitude(startlon * 1 + Math.atan2(Math.sin(brg[0])
                * Math.sin(distance) * cosstartlat, Math.cos(distance) - sinstartlat * Math.sin(distance))));
        finalLat = deg(finalLat);

        distance = (double) Math.round(distance * radiusEarth * 10000) / 10000.0;
        brg[0] = Math.round(deg(brg[0]) * 1000) / 1000;//随机距离
        String time = getTime();
        GpsBean gpsBean = new GpsBean();
        gpsBean.setGpsID(gpsID);
        gpsBean.setTime(time);
        gpsBean.setLongitude(padZeroRight(finalLon));
        gpsBean.setLatitude(padZeroRight(finalLat));
        gpsBean.setBearing(brg[j] + "");
        gpsBean.setDistance(distance + "");
        return gpsBean;
    }

    /**
     * 车辆停止
     *
     * @param gpsID
     * @param startLat
     * @param startLon
     * @param bearing
     * @return
     */
    private GpsBean carStop(String gpsID, double startLat, double startLon, double bearing) {
        String time = getTime();
        GpsBean gpsBean = new GpsBean();
        gpsBean.setGpsID(gpsID);
        gpsBean.setTime(time);
        gpsBean.setLongitude(String.valueOf(startLon));
        gpsBean.setLatitude(String.valueOf(startLat));
        gpsBean.setBearing(String.valueOf(bearing));
        gpsBean.setDistance("0.0000");
        return gpsBean;
    }

    public static String padZeroRight(double s) {
        String ss = "" + Math.round(s * Math.pow(10, sigDigits)) / Math.pow(10, sigDigits);
        int i = ss.indexOf(".");
        int d = ss.length() - i - 1;
        if (i == -1) {
            return ss + ".00";
        } else if (d == 1) {
            return ss + "0";
        } else {
            return ss;
        }
    }

    public static double deg(double rd) {
        return rd * 180 / Math.PI;
    }

    public static double normalizeLongitude(double lon) {
        double n = Math.PI;
        if (lon > n) {
            lon = lon - 2 * n;
        } else if (lon < -n) {
            lon = lon + 2 * n;
        }
        return lon;
    }

    public static double rad(double dg) {
        return (dg * Math.PI / 180);
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getTime() {
        Date now = new Date();
        //设置日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(now);
        return time;
    }

    /**
     * 城市经纬度
     *
     * @return
     */
    public String getLatAndLon() {
        int index = new Random().nextInt(LatAndLon.length);
        return LatAndLon[index];
    }
}