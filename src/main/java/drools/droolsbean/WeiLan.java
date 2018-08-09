package drools.droolsbean;

public class WeiLan {

    private String weiLanName;
    private int RuleId;
    private double lon;
    private double lat;
    private double radius;
    private int carId;

    public void setWeiLanName(String weiLanName) {
        this.weiLanName = weiLanName;
    }

    public String getWeiLanName() {
        return weiLanName;
    }

    public void setRuleId(int ruleId) {
        this.RuleId = ruleId;
    }

    public int getRuleId() {
        return RuleId;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLon() {
        return lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public int getCarId() {
        return carId;
    }

}
