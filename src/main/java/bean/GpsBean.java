package bean;

/**
 * Created by Asus- on 2018/7/17.
 */
public class GpsBean {
    //车ID
    private String gpsID;
    //记录时间
    private String time;
    //经度
    private String longitude;
    //纬度
    private String latitude;
    //角度
    private String bearing;
    //单次运动的距离
    private String distance;
    //车辆是否停止
    private boolean isStop;
    //车辆停止时间
    private String totalStopTime;

    public String getGpsID() {
        return gpsID;
    }

    public void setGpsID(String gpsID) {
        this.gpsID = gpsID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getBearing() {
        return bearing;
    }

    public void setBearing(String bearing) {
        this.bearing = bearing;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public String getTotalStopTime() {
        return totalStopTime;
    }

    public void setTotalStopTime(String totalStopTime) {
        this.totalStopTime = totalStopTime;
    }
}
