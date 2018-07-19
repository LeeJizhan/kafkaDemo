import db.DBOper;
import kafka.Comsumer;
import kafka.Producer;
import properties.KafkaProperties;

/**
 * Created by Asus- on 2018/7/12.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        DBOper dbOper = new DBOper();
        dbOper.updateCarData();
        dbOper.updateGpsDeviceData();
        dbOper.updateCarAndGpsData();
//        KafkaProperties kafkaProperties = KafkaProperties.getInstance();
//        //通过键值对的方式读取kafka配置文件的值
//        String topic = kafkaProperties.getKafkaTopic();
//        boolean isAsync = true;
//        Producer producer = new Producer(topic, isAsync);
//        producer.start();
//        //producerThread.stop();
//        Comsumer comsumer = new Comsumer(topic);
//        comsumer.start();
    }
}
