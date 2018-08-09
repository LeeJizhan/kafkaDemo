package drools.droolsbean;




public class CarDataDrools {

    public Car latLng = new Car();

    public Car cardata(String longitude, String lantitude){

        double log = Double.parseDouble(longitude);
        double lant = Double.parseDouble(lantitude);

        latLng.setLongtitude(log);
        latLng.setLantitude(lant);

        return latLng;
    }
}
