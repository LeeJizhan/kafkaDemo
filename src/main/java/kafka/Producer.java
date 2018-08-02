package kafka;

import data.GPSData;
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
        producer = new KafkaProducer<String, String>(props);
        this.topic = topic;
        this.isAsync = isAsync;
    }

    public void run() {
        int count = 10;
        int messageNo = 0;
        long startTime = System.currentTimeMillis();

        while (count > 0) {
            List<String> data = null;
            try {
                data = new GPSData().getData();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isAsync) {
                //发送信息，包括topic和键值对
                for (String messageStr : data) {
                    producer.send(new ProducerRecord<String, String>(topic,
                            Integer.toString(messageNo),
                            messageStr), new DemoCallBack(startTime, messageNo, messageStr));
                    //LoggerUtil.info("Sent message:" + messageStr);
                }
            }
            count--;
            messageNo++;
            //LoggerUtil.info("----------------停一下-------------");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //关闭
        producer.close();
    }
}

/**
 * 发送成功回调
 */
class DemoCallBack implements Callback {
    private final long startTime;
    private final int key;
    private final String message;

    public DemoCallBack(long startTime, int key, String message) {
        this.startTime = startTime;
        this.key = key;
        this.message = message;
    }

    public void onCompletion(RecordMetadata metadata, Exception exception) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (metadata != null) {
            LoggerUtil.info(
                    "message(" + key + ", " + message + ") sent to partition(" + metadata.partition() +
                            "), " +
                            "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
        } else {
            exception.printStackTrace();
        }
    }
}
