import kafka.Consumer;
import kafka.Producer;
import properties.KafkaProperties;
import spark.SparkMain;

/**
 * Created by Asus- on 2018/7/12.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
//        DBOper dbOper = new DBOper();
//        dbOper.updateCarData();
//        dbOper.updateGpsDeviceData();
//        dbOper.updateCarAndGpsData();
        KafkaProperties kafkaProperties = KafkaProperties.getInstance();
        //通过键值对的方式读取kafka配置文件的值
        String topic = kafkaProperties.getKafkaTopic();
        boolean isAsync = true;
        Producer producer = new Producer(topic, isAsync);
        producer.start();
        SparkMain sparkMain = new SparkMain();
        sparkMain.run();
//        Consumer consumer = new Consumer(topic);
//        consumer.start();
//        CarData carData = new CarData();
//        List<CarBean> carBeans = carData.getCarData();
//        for (CarBean carBean : carBeans) {
//            LoggerUtil.info("车ID：" + carBean.getCarID()
//                    + " 车品牌：" + carBean.getBrand()
//                    + " 车型：" + carBean.getModel()
//                    + " 车牌号：" + carBean.getNumber()
//                    + " 车主：" + carBean.getOwner()
//                    + " 车主电话：" + carBean.getPhone());
//        }
    }
}
