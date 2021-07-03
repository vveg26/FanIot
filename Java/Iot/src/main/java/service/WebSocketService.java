package service;

import config.MqttClientConfig;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author aeolus
 * @program IOT201851385129
 * @description 创建WebSocket服务端
 * @date 2021-06-23 03:17:58
 */

@Component
@ServerEndpoint("/websocket")
public class WebSocketService {

    private final String pubTopicCmd = "iotplatform/update/cmd";
    private final String pubTopicStatus = "iotplatform/update/status";

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    public static Session session;
    private static MqttClientConfig mqttClientConfig;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private CopyOnWriteArraySet<WebSocketService> webSocketServiceSet = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        WebSocketService.session = session;
        webSocketServiceSet.add(this);
        System.out.println("WebSocket连接成功");
    }

    @OnClose
    public void onClose() {
        webSocketServiceSet.remove(this);
        System.out.println("WebSocket连接关闭");
    }

    @OnMessage
    public void onMessage(String message) throws MqttException {
        System.out.println("【WebSocket接收消息】：" + message);
        if (message.contains("WorkModeCmd") || message.contains("GetStatusCmd")
                || message.contains("LedCmd") || message.contains("FanCmd")) {
            mqttClientConfig.publishTopic(pubTopicCmd, message);
        }
        if (message.contains("LedStatus") || message.contains("FanStatus")
                || message.contains("WorkModeStatus") || message.contains("TemLimit")
                || message.contains("Interval")) {
            mqttClientConfig.publishTopic(pubTopicStatus, message);
        }

    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("WebSocket发生错误");
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        System.out.println("【WebSocket发送消息】：" + message);
        try {
            WebSocketService.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setMqttService(MqttClientConfig mqttClientConfig) {
        WebSocketService.mqttClientConfig = mqttClientConfig;
    }
}
