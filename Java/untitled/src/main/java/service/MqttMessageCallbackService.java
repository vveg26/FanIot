package service;

import config.MqttClientConfig;
import dao.DeviceDao;
import dao.DeviceDataDao;
import entity.mvc.Data;
import entity.mvc.DataType;
import entity.mvc.Device;
import entity.mvc.Measurement;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author aeolus
 * @program IOT201851385129
 * @description mqtt信息接受、异常重启
 * @date 2021-06-21 20:28:46
 */

@Component
public class MqttMessageCallbackService implements MqttCallback {

    public static WebSocketService webSocketService;

    private final MqttClientConfig mqttClientConfig;
    private final DeviceDao deviceDao;
    private final DeviceDataDao deviceDataDao;

    public MqttMessageCallbackService(MqttClientConfig mqttClientConfig, DeviceDao deviceDao, DeviceDataDao deviceDataDao) {
        this.mqttClientConfig = mqttClientConfig;
        this.deviceDao = deviceDao;
        this.deviceDataDao = deviceDataDao;
    }

    @Override
    public void connectionLost(Throwable cause) {
        // 连接丢失后，在这里进行重连
        try {
            System.out.println("MQTT Disconnected");
            System.out.println("MQTT Reconnecting........");
            mqttClientConfig.initConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
      //  System.out.println("接收消息Qos:" + message.getQos());
        String payload = new String(message.getPayload());
        System.out.println("MQTT Message arrived [" + topic + "]: " + payload);
        //实时发送给iot云平台
        try {
            webSocketService.sendMessage(payload);
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("mqtt转发到websocket失败，因为websocket未连接");
        }
        //保存到数据库 todo bug操作数据库会导致mqtt连接丢失
//        saveToDB(payload);
    }

    /**
     * 将payload的温湿度数据保存到MongoDB
     **/
    private void saveToDB(String payload) {
        JSONObject payloadJSON = new JSONObject(payload);
        String value;
        if (payloadJSON.has("dht11Tem") && payloadJSON.has("dht11Hum")) {
            int dht11Tem = payloadJSON.getInt("dht11Tem");
            int dht11Hum = payloadJSON.getInt("dht11Hum");
            value = dht11Tem + "," + dht11Hum;

            System.out.println("获取待新增的设备数据：" + "dataType = " + DataType.MEASUREMENT + ", deviceId = 1" + ", value = " + value);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datetime = sdf.format(new Date());
            Data data = new Measurement(1, datetime, value);

            try {
                deviceDataDao.insert(data);
                System.out.println("添加数据成功");
                Device device = deviceDao.findOneByDeviceId(1);
                device.setLatestUpdateTime(datetime);
                deviceDao.update(device);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("添加数据失败");
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("MQTT Delivery Complete---------" + token.isComplete());
    }
}
