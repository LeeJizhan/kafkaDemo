package kafka;

import data.GPSData;
import db.DBOper;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import properties.KafkaProperties;
import utils.LoggerUtil;

import java.util.List;
import java.util.Properties;

/**
 * Created by Asus- on 2018/7/12.
 */
public class Producer extends Thread {

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final Boolean isAsync;

    public Producer(String topic, Boolean isAsync) {
        Properties props = new Properties();

        KafkaProperties kafkaProperties = KafkaProperties.getInstance();
        LoggerUtil.info("server:" + kafkaProperties.getKafkaServerUrl() + ":" + kafkaProperties.getKafkaServerPort());
        LoggerUtil.info("groupId:" + kafkaProperties.getKafkaGroupID());
        LoggerUtil.info("topic:" + kafkaProperties.getKafkaTopic());

        //BOOTSTRAP_SERVERS_CONFIG - ip地址和端口号配置
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getKafkaServerUrl() + ":" + kafkaProperties.getKafkaServerPort());
        //ClientID配置
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "DemoProducer");
        //key值的序列化方式
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //value的序列化方式
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        props.put("acks", "1");
        props.put("retries", 0);
        /**
         * 新建一个KafkaProducer对象实例
         * ProducerRecord是发送到Kafka cluster.ProducerRecord类构造函数的键/值对
         */
        producer = new KafkaProducer<>(props);
        this.topic = topic;
        this.isAsync = isAsync;
    }

    public void run() {
        int index = 0;
        int count = 10000;
        long startTime = System.currentTimeMillis();
        DBOper dbOper = new DBOper();
        while (index < 1960000) {
            List<String> data = dbOper.search(index, count);
            index += count + 1;
//            if (index > 1960000)
//                index = 0;
            if (isAsync) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //发送信息，包括topic和键值对
                for (String messageStr : data) {
                    //LoggerUtil.info("Sent message:" + messageStr);
                    producer.send(new ProducerRecord<>(topic, messageStr), new DemoCallBack(startTime, messageStr));
                }
            }
            LoggerUtil.info("------------------停一下-----------------------");
        }
//        //关闭
//        producer.close();
    }
}

/**
 * 发送成功回调
 */
class DemoCallBack implements Callback {
    private final long startTime;
    private final String message;

    public DemoCallBack(long startTime, String message) {
        this.startTime = startTime;
        this.message = message;
    }

    public void onCompletion(RecordMetadata metadata, Exception exception) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (metadata != null) {
            LoggerUtil.info(message + "sent to partition(" + metadata.partition() +
                    "), " +
                    "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
        } else {
            exception.printStackTrace();
        }
    }
}
