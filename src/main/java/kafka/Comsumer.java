package kafka;

import kafka.utils.ShutdownableThread;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import properties.KafkaProperties;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by Asus- on 2018/7/12.
 */
public class Comsumer extends ShutdownableThread {
    private final KafkaConsumer<Integer, String> consumer;
    private final String topic;

    //构造方法
    public Comsumer(String topic) {

        super("KafkaConsumerExample", false);

        //新建一个Properties类来做相关配置
        Properties props = new Properties();
        KafkaProperties kafkaProperties = KafkaProperties.getInstance();

        //BOOTSTRAP_SERVERS_CONFIG - ip地址和端口号配置
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getKafkaServerUrl() + ":" + kafkaProperties.getKafkaServerPort());
        //GroupID配置
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getKafkaGroupID());
        //自动发送
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        //自动发送间隔 1000ms
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        //session timeout 30000ms
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        //key值的序列化方式
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        //value的序列化方式
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        /**
         * 新建一个KafkaConsumer实例
         * 用于从Kafka集群接收记录。
         */
        consumer = new KafkaConsumer<Integer, String>(props);
        this.topic = topic;
    }

    @Override
    public void doWork() {
        //消费者通过订阅来进行消费，this.topic表示当前主题
        consumer.subscribe(Arrays.asList(this.topic));
        //ConsumerRecords作为ConsumerRecord的容器，用于保存特定主题的每个分区的ConsumerRecord列表
        ConsumerRecords<Integer, String> records = consumer.poll(1000);
        for (ConsumerRecord<Integer, String> record : records) {
            System.out.println("Received message:" + record.value() + ". At offset " + record.offset());
        }
    }
}
