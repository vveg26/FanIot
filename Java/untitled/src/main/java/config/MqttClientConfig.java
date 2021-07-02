package config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import service.MqttMessageCallbackService;
import service.WebSocketService;

/**
 * @author aeolus
 * @program IOT201851385129
 * @description Paho java连接EMQ X Broker
 * @date 2021-06-21 20:09:39
 */

@Configuration
public class MqttClientConfig {

    @Autowired
    private MqttMessageCallbackService mqttMessageCallbackService;

    private final String subTopicDht11 = "iotplatform/get/dht11";
    private final String subTopicStatus = "iotplatform/get/status";
    private final String broker = "tcp://192.168.43.69:1883";
    private final String clientId = "iot-iot";

    MemoryPersistence persistence = new MemoryPersistence();
    private MqttClient client = null;

    @Bean
    public void initConnect() {
        try {
            client = new MqttClient(broker, clientId, persistence);

            // MQTT 连接选项
            MqttConnectOptions connOpts = new MqttConnectOptions();
            // 保留会话
            connOpts.setCleanSession(true);
            // 设置回调
            client.setCallback(mqttMessageCallbackService);
            // 建立连接
            System.out.println("MQTT Connecting to broker: " + broker);
            client.connect(connOpts);
            System.out.println("MQTT Connected");
            // 订阅
            subscribeTopic(subTopicDht11);
            subscribeTopic(subTopicStatus);

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }


    /**
     * 订阅主题
     */
    public void subscribeTopic(String topic) throws MqttException {
        client.subscribe(topic, 0);
    }

    /**
     * 发布主题
     */
    public void publishTopic(String topic, String content) throws MqttException {
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(0);
        client.publish(topic, message);
        System.out.println("Published message: " + message);
    }


    @Autowired
    public void setWebSocketService(WebSocketService webSocketService){
        MqttMessageCallbackService.webSocketService = webSocketService;
    }

}
