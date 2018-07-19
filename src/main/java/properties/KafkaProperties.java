package properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Asus- on 2018/7/17.
 */
public class KafkaProperties {

    private static volatile KafkaProperties kafkaProperties = null;

    private String kafkaTopic;
    private String kafkaServerUrl;
    private String kafkaServerPort;
    private String kafkaGroupID;

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public String getKafkaServerUrl() {
        return kafkaServerUrl;
    }

    public String getKafkaServerPort() {
        return kafkaServerPort;
    }

    public String getKafkaGroupID() {
        return kafkaGroupID;
    }

    private KafkaProperties() {
        //新建一个Properties类来做相关配置
        Properties props = new Properties();
        //当前类通过输入流来读取配置文件
        InputStream inputStream = KafkaProperties.class.getResourceAsStream("/kafka.properties");

        try {
            /**
             * Reads a property list (key and element pairs) from the input
             * byte stream
             */
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //通过键值对的方式读取kafka配置文件的值
        this.kafkaTopic = props.getProperty("kafka.topic_gps");
        this.kafkaServerUrl = props.getProperty("kafka.server.url");
        this.kafkaServerPort = props.getProperty("kafka.server.port");
        this.kafkaGroupID = props.getProperty("kafka.groupID");
    }

    public static KafkaProperties getInstance(){
        if (kafkaProperties == null){
            synchronized (KafkaProperties.class){
                if (kafkaProperties == null){
                    kafkaProperties = new KafkaProperties();
                }
            }
        }
        return kafkaProperties;
    }
}
