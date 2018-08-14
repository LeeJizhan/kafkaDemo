package dbscan;

/**
 * Created by Asus- on 2018/8/14.
 */
public class Point {
    private double lon;
    private double lat;
    private boolean isVisited;  //是否被访问
    private int cluster;      //簇类
    private boolean isNoised;  //是否噪声点

    public Point(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
        this.isVisited = false;  //初始未被访问
        this.cluster = 0;
        this.isNoised = false; //噪声点初始为非噪声点
    }

    public double getDistance(Point point) {
        double lat1 = (Math.PI / 180) * lat;
        double lat2 = (Math.PI / 180) * point.lat;

        double lon1 = (Math.PI / 180) * lon;
        double lon2 = (Math.PI / 180) * point.lon;

        //地球半径
        double R = 6371.004;

        //两点间距离，单位km
        double dis = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

        dis *= 1000;
        return dis;
    }

    public void setVisit(boolean isVisited) {
        this.isVisited = isVisited;
    }

    public boolean getVisit() {
        return isVisited;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public int getCluster() {
        return cluster;
    }

    public void setNoised(boolean noised) {
        isNoised = noised;
    }

    public boolean getNoised() {
        return isNoised;
    }

    @Override
    public String toString() {
        return lon + " " + lat + " " + cluster + " " + (isNoised ? 1 : 0);
    }
}
