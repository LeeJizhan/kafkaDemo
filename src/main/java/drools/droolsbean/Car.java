package drools.droolsbean;

public class Car {
    /**
     * 在围栏内，状态安全
     */
    public static final String SAFE = "SAFE";

    /**
     * 出围栏，状态为警告
     */
    public static final String WARN = "WARN";

    private String type;
    double longitude;
    double latitude;
    double distance;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public void setLongtitude(double longtitude) {
        this.longitude = longtitude;
    }
    public double getLongtitude() {
        return longitude;
    }

    public void setLantitude(double lantitude) {
        this.latitude = lantitude;
    }
    public double getLantitude() {
        return latitude;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    public double getDistance() {
        return distance;
    }
}
