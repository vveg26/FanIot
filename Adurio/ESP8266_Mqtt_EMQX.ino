#include <ESP8266WiFi.h>//添加ESP8266用于WiFi的头文件
#include <PubSubClient.h>//添加用于MQTT客户端的头文件
#include <SimpleDHT.h>
#include <ArduinoJson.h>
#include <ESP8266HTTPClient.h>


#define BrokerAddress   "192.168.45.69"                             //定义MQTT服务地址
#define BrokerPort     1883                                                //定义MQTT服务端口
#define ClientID  "esp8266-1"                                   //定义MQTT Client ID
#define SubTopicStatus "esp8266/get/status"                       //定义订阅的Topic
#define SubTopicCmd "esp8266/get/cmd"                       //定义订阅的Topic
#define PubTopicDht11 "esp8266/update/dht11"                      //定义订阅的Topic
#define PubTopicStatus "esp8266/update/status"                      //定义订阅的Topic
#define LED 16  //定义LED灯：LED所对应的引脚为16号
#define pinDHT11 4 //温湿度传感器引脚
#define pinFan  5 //风扇引脚

//温湿度
byte temperature = 0; //温度
byte humidity = 0;  //湿度
int TemLimit = 30; //温度限制值，大于等于该值将开启风扇
int Interval = 1500; //温湿度数据上报时间间隔（毫秒）
int WorkModeStatus = 1; //设置默认工作模式为自动状态（0为手动状态）
int FanStatus = 0; //风扇状态
//wifi
const char *ssid = "xm";//wifi的名字         
const char *password = "11223344";//wifi的密码   

void MqttCallback(char* topic, byte* payload, unsigned int length);        //声明回调函数
WiFiClient EspClient;                                                      //声明wifi客户端对象
PubSubClient client(BrokerAddress, BrokerPort, &MqttCallback, EspClient);  //声明mqtt客户端对象
SimpleDHT11 dht11(pinDHT11); //声明DHT11对象


void SetupWifi() {
  delay(10);
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while(WiFi.status()!=WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }

  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void publishDHT11() {
  int err = SimpleDHTErrSuccess;
  if ((err = dht11.read(&temperature, &humidity, NULL)) != SimpleDHTErrSuccess) {
    Serial.print("Read DHT11 failed, err="); Serial.print(SimpleDHTErrCode(err));
    Serial.print(","); Serial.println(SimpleDHTErrDuration(err)); 
    return ;
  }

  //构造dht11 Json数据
  StaticJsonDocument<200> doc;
  doc["dht11Tem"] = temperature;
  doc["dht11Hum"] = humidity;
  char dht11Data[50]; //dht11温湿度数据json格式
  serializeJson(doc, dht11Data, measureJson(doc) + 1);
  serializeJson(doc, Serial); Serial.println();
  
  client.publish(PubTopicDht11, dht11Data);
}

//获取到云平台命令时的LED指示：LED灯闪烁
void LEDIndicatorByGet(){
    boolean currentLED = digitalRead(LED);
    digitalWrite(LED,HIGH);
    delay(100);
    digitalWrite(LED,LOW);
    delay(100);
    digitalWrite(LED,HIGH);
    digitalWrite(LED,currentLED);
}

//发布到云平台命令时的LED指示：LED灯呼吸
void LEDIndicatorByPublish(){
    boolean currentLED = digitalRead(LED);
    delay(100);
    for (int i = 1024; i >= 0; i--){//电平降低，从暗到明
      analogWrite(LED, i);
      delay(1);
    } 
    for (int i = 0; i < 1024; i++){//电平升高，从明到暗
      analogWrite(LED, i);
      delay(1);
  }
    digitalWrite(LED,currentLED);
}


void MqttCallback(char* topic, byte* payload, unsigned int length) {
  StaticJsonDocument<50> payloadDoc;
  DeserializationError error = deserializeJson(payloadDoc, payload, length);
  if (error) {
    Serial.print(F("deserializeJson() failed: "));
    Serial.println(error.f_str());
    return;
  }
  int getStatusCmd = -1, ledCmd = -1, workModeCmd = -1, fanCmd = -1;
  if(payloadDoc.containsKey("GetStatusCmd")){ getStatusCmd = payloadDoc["GetStatusCmd"]; }
  if(payloadDoc.containsKey("LedCmd")){ ledCmd = payloadDoc["LedCmd"]; }
  if(payloadDoc.containsKey("WorkModeCmd")){ workModeCmd = payloadDoc["WorkModeCmd"]; }
  if(payloadDoc.containsKey("FanCmd")){ fanCmd = payloadDoc["FanCmd"]; }
  Serial.print("Message arrived ["); Serial.print(topic); Serial.print("] ");
  serializeJson(payloadDoc, Serial); Serial.println();
  LEDIndicatorByGet();
  
  //获取开发版状态
  if(getStatusCmd == 1){ 
    Serial.print("当前开发板状态：");
    if(digitalRead(LED) == HIGH) {
      Serial.print("LED=灭");
      client.publish(PubTopicStatus,"{\"LedStatus\":0}"); //向平台发送灯灭状态
      LEDIndicatorByPublish();
    }
    if(digitalRead(LED) == LOW) {
      Serial.print("LED=亮");
      client.publish(PubTopicStatus,"{\"LedStatus\":1}"); //向平台发送灯亮状态
      LEDIndicatorByPublish();
    }
    if(digitalRead(pinFan) == HIGH) {
      Serial.print("，fan=开");
      client.publish(PubTopicStatus,"{\"FanStatus\":1}"); //向平台发送风扇开启状态
      LEDIndicatorByPublish();
    }
    if(digitalRead(pinFan) == LOW) {
      Serial.print("，fan=关");
      client.publish(PubTopicStatus,"{\"FanStatus\":0}"); //向平台发送风扇关闭状态
      LEDIndicatorByPublish();
    }
    if(WorkModeStatus == 1){
      WorkModeStatus = 1;
      Serial.print("，WorkMode=自动");
      client.publish(PubTopicStatus,"{\"WorkModeStatus\":1}"); //向平台发送工作模式状态为自动
      LEDIndicatorByPublish();
    }
    if(WorkModeStatus == 0){
      WorkModeStatus = 0;
      Serial.print("，WorkMode=手动");
      client.publish(PubTopicStatus,"{\"WorkModeStatus\":0}"); //向平台发送工作模式状态为手动
      LEDIndicatorByPublish();
    }
    Serial.print("，温度限制="); Serial.print(TemLimit);
    Serial.print("，时间间隔="); Serial.print(Interval); Serial.println();
    StaticJsonDocument<40> doc;
    doc["TemLimit"] = TemLimit;
    doc["Interval"] = Interval;
    char docChar[40]; 
    serializeJson(doc, docChar, measureJson(doc) + 1);
    client.publish(PubTopicStatus, docChar); //向平台发送温度限制值和时间间隔值
  }

  //更新温度阀值命令
  if(payloadDoc.containsKey("TemLimit")){
    TemLimit = payloadDoc["TemLimit"]; 
    Serial.print("TemLimit="); Serial.print(TemLimit); Serial.println("，更新温度阀值");  
  }
  
  //更新时间间隔值命令
  if(payloadDoc.containsKey("Interval")){
    Interval = payloadDoc["Interval"]; 
    Serial.print("Interval="); Serial.print(Interval); Serial.println("，更新时间间隔");  
  }
  
  //LED开关命令
  if(ledCmd == 1){//点亮LED
    digitalWrite(LED,LOW);
    client.publish(PubTopicStatus,"{\"LedStatus\":1}"); //向平台发送灯亮状态
    LEDIndicatorByPublish();
    Serial.print("ledCmd="); Serial.print(ledCmd); Serial.print("，开灯"); Serial.println();
  } if(ledCmd == 0){//熄灭LED
    digitalWrite(LED,HIGH);
    client.publish(PubTopicStatus,"{\"LedStatus\":0}"); //向平台发送灯灭状态
    LEDIndicatorByPublish();
    Serial.print("ledCmd="); Serial.print(ledCmd); Serial.print("，关灯"); Serial.println();
  }

  //工作模式切换命令
  if(workModeCmd == 1){ //自动模式
    digitalWrite(pinFan,LOW); FanStatus = 0;
    WorkModeStatus = 1;
    client.publish(PubTopicStatus,"{\"WorkModeStatus\":1}"); //向平台发送工作模式状态为自动
    LEDIndicatorByPublish();
    Serial.print("workModeCmd="); Serial.print(workModeCmd); Serial.print("，切换为自动模式"); Serial.println();
  } if(workModeCmd == 0){ //手动模式
    digitalWrite(pinFan,LOW); 
    FanStatus = 0;
    WorkModeStatus = 0;
    client.publish(PubTopicStatus,"{\"WorkModeStatus\":0,\"FanStatus\":0}"); //向平台发送工作模式状态为手动
    client.publish(PubTopicStatus,"{}"); 
    LEDIndicatorByPublish();
    Serial.print("workModeCmd="); Serial.print(workModeCmd); Serial.print("，切换为手动模式"); Serial.println();
  }

  //风扇开关命令
  if(fanCmd == 1){ //打开风扇
    digitalWrite(pinFan,HIGH); FanStatus = 1;
    client.publish(PubTopicStatus,"{\"FanStatus\":1}"); //向平台发送风扇开启状态
    LEDIndicatorByPublish();
    Serial.print("fanCmd=");Serial.print(fanCmd);Serial.print("，开启风扇"); Serial.println();
  } if(fanCmd == 0) { //关闭风扇
    digitalWrite(pinFan,LOW); FanStatus = 0;
    client.publish(PubTopicStatus,"{\"FanStatus\":0}"); //向平台发送风扇关闭状态
    LEDIndicatorByPublish();
    Serial.print("fanCmd=");Serial.print(fanCmd);Serial.print("，关闭风扇"); Serial.println();
  }


}

void setup() {
  pinMode(LED,OUTPUT);//初始化LED引脚模式为输出
  pinMode(pinFan,OUTPUT);//初始化pinFan引脚模式为输出
  digitalWrite(LED,HIGH);//初始LED为高电平，把LED熄灭
  digitalWrite(pinFan,LOW);//初始pinFan为低电平，把风扇关闭
  Serial.begin(115200);//设置串口
  SetupWifi();//设置wifi
  client.setServer(BrokerAddress,1883);//设置MQTT服务地址和端口
  client.setCallback(MqttCallback);//回调函数
  client.setClient(EspClient);//设置wifi客户端
  client.connect(ClientID);//设备接入物联网云平台
  //client.connect(ClientID,UserName,Password);//设备接入物联网云平台
  if(!client.connected())//判断是否接入成功
    Serial.print("Mqtt Connected Failed!\n");
  else{
    Serial.print("Mqtt Connected Ok!\n");
    client.subscribe(SubTopicStatus);//订阅Topic
    client.subscribe(SubTopicCmd);//订阅Topic
  }  
}

void loop() {  
  publishDHT11();
  if(WorkModeStatus == 1) { //自动模式：温度超过指定值，自动打开风扇
    Serial.print("当前为自动模式：");
    if(temperature >= TemLimit) {
      digitalWrite(pinFan,HIGH); //开启风扇
      if(FanStatus != digitalRead(pinFan)){ //风扇状态没改变的话只向发送一次
        client.publish(PubTopicStatus,"{\"FanStatus\":1}"); //向平台发送风扇开启状态
        FanStatus = 1;
      }
      Serial.print("温度>=");Serial.print(TemLimit);Serial.print("，打开风扇");  Serial.println();
    } else {
      digitalWrite(pinFan,LOW); //关闭风扇
      if(FanStatus != digitalRead(pinFan)){ //风扇状态没改变的话只向发送一次
        client.publish(PubTopicStatus,"{\"FanStatus\":0}"); //向平台发送风扇关闭状态
        FanStatus = 0;
      }
      Serial.print("温度<");Serial.print(TemLimit);Serial.print("，关闭风扇"); Serial.println();
    }
  } 
  client.loop();//关键，可以侦测回调函数中数据是否到来
  delay(Interval);
}
