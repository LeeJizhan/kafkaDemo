package drools.droolsbean;

public class WeiLan {

    /**
     * 在围栏内，状态安全
     */
    public static final String SAFE = "SAFE";  //7.0
    /**
     * 出围栏，状态为警告
     */
    public static final String WARN = "WARN";

    private String type;

    private String WeilanId;
    private int RuleId;
    double log;
    double lat;
    private double radium;
    private int carId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setWeilanId(String weilanId){
        this.WeilanId = weilanId;
    }

    public String getWeilanId() {
        return WeilanId;
    }

    public void setRuleId(int ruleId){
        this.RuleId = ruleId;
    }

    public int getRuleId(){
        return RuleId;
    }

    public void setLog(double log){
        this.log = log;
    }
    public double getLog(){
        return log;
    }

    public void setLat(double lat){
        this.lat = lat;
    }
    public double getLat(){
        return lat;
    }

    public void setRadium(double radium){
        this.radium = radium;
    }
    public double getRadium(){
        return radium;
    }

    public void setCarId(int carId){
        this.carId= carId;
    }
    public int getCarId(){
        return carId;
    }

}
