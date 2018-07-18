import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by Asus- on 2018/7/12.
 */
public class Producer extends Thread {

    private final KafkaProducer<Integer, String> producer;
    private final String topic;
    private final Boolean isAsync;

    public Producer(String topic, Boolean isAsync) {
        Properties props = new Properties();

        KafkaProperties kafkaProperties = KafkaProperties.getInstance();
        System.out.println("server : " + kafkaProperties.getKafkaServerUrl() + ":" + kafkaProperties.getKafkaServerPort());
        System.out.println("groupId : " + kafkaProperties.getKafkaGroupID());

        //BOOTSTRAP_SERVERS_CONFIG - ip地址和端口号配置
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getKafkaServerUrl() + ":" + kafkaProperties.getKafkaServerPort());
        //GroupID配置
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProperties.getKafkaGroupID());
        //key值的序列化方式
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        //value的序列化方式
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        /**
         * 新建一个KafkaProducer对象实例
         * ProducerRecord是发送到Kafka cluster.ProducerRecord类构造函数的键/值对
         */
        producer = new KafkaProducer<Integer, String>(props);
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
                data = new ProduceGPSData().getData();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isAsync) {
                //发送信息，包括topic和键值对
                for (String messageStr : data) {
//                    producer.send(new ProducerRecord<Integer, String>(topic,
//                            messageNo,
//                            messageStr), new DemoCallBack(startTime, messageNo, messageStr));
                    System.out.println("Sent message:" + messageStr);
                }
            }
            count--;
            messageNo++;
        }
    }
}

class DemoCallBack implements Callback {
    private final long startTime;
    private final int key;
    private final String message;

    public DemoCallBack(long startTime, int key, String message) {
        this.startTime = startTime;
        this.key = key;
        this.message = message;
    }

    /**
     * A callback method the user can implement to provide asynchronous handling of request completion. This method will
     * be called when the record sent to the server has been acknowledged. Exactly one of the arguments will be
     * non-null.
     *
     * @param metadata  The metadata for the record that was sent (i.e. the partition and offset). Null if an error
     *                  occurred.
     * @param exception The exception thrown during processing of this record. Null if no error occurred.
     */
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (metadata != null) {
            System.out.println(
                    "message(" + key + ", " + message + ") sent to partition(" + metadata.partition() +
                            "), " +
                            "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
        } else {
            exception.printStackTrace();
        }
    }
}
