package data; /**
 * Created by Asus- on 2018/7/17.
 */

import bean.GpsBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GPSData {

    private static int sigDigits = 8;
    //地球半径
    private static double radiusEarth = 6372.796924d;

    private static List<GpsBean> gpsBeanList;

    private List<String> gpsJsons = new ArrayList<String>();

    public GPSData() throws InterruptedException {
        this.gpsBeanList = calculate(10, 34.259516, 109.003784, 0.1);
    }

    public List<String> getData() {
        String msg = null;
        if (!gpsJsons.isEmpty()){
            gpsJsons.clear();
        }
        for (GpsBean bean : gpsBeanList){
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

    public List<GpsBean> calculate(int p, double startlat, double startlon, double maxdist) throws InterruptedException {
        List<GpsBean> rtnList = new ArrayList<GpsBean>();
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
        String cardID = getCarID();
        if (!rtnList.isEmpty()) {
            rtnList.clear();
        }
        //点数
        for (int k = 0; k < p; k++) {
            //模拟车辆停止
//            int randomSleepTime = (int) Math.round(Math.random() * 100);
//            Thread.sleep(randomSleepTime);
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
            gpsBean.setGpsID(cardID);
            gpsBean.setTime(time);
            gpsBean.setLongitude(padZeroRight(finalLon));
            gpsBean.setLatitude(padZeroRight(finalLat));
            gpsBean.setBearing(brg[j] + "");
            gpsBean.setDistance(distance + "");
            rtnList.add(gpsBean);
        }
        return rtnList;
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

    public static String getTime() {
        Date now = new Date();
        //设置日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(now);
        return time;
    }

    public static String getCarID() {
        String carID = Math.round(Math.random() * 1000) + "";
        return carID;
    }
}