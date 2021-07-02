package service;

import config.MqttClientConfig;
import controller.DeviceController;
import dao.DeviceDao;
import dao.DeviceDataDao;
import entity.mvc.Data;
import entity.mvc.DataType;
import entity.mvc.Device;
import entity.mvc.Result;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author aeolus
 * @program IOT201851385129
 * @description
 * @date 2021-05-31 09:50:31
 */

@Service
public class DeviceManageService {
    private final DeviceDao deviceDao;
    private final DeviceDataDao deviceDataDao;
    private final MqttClientConfig mqttClientConfig;

    private final String PubTopicCmd = "iotplatform/update/cmd";
    private final String PubTopicStatus = "iotplatform/update/status";
    private final String PubTopicConnected = "$SYS/brokers/emqx@127.0.0.1/clients/esp8266-1/connected";
    private final String PubTopicDisconnected = "$SYS/brokers/emqx@127.0.0.1/clients/esp8266-1/disconnected";

    public DeviceManageService(DeviceDao deviceDao, DeviceDataDao deviceDataDao, MqttClientConfig mqttClientConfig) {
        this.deviceDao = deviceDao;
        this.deviceDataDao = deviceDataDao;
        this.mqttClientConfig = mqttClientConfig;
    }

    public Result controlEsp8266(String command) throws MqttException {
        Result result = new Result();
//        if (command.equals("isOnline")){
//            mqttClientConfig.subscribeTopic(PubTopicConnected);
//            mqttClientConfig.subscribeTopic(PubTopicDisconnected);
//        }



        return result;
    }

    public Result getEsp8266(JSONObject payload) throws MqttException {
        Result result = new Result();
        int ledStatus = -1, fanStatus = -1, workModeStatus = -1, temLimit = -1;
        if (payload.has("LedStatus")) {
            ledStatus = payload.getInt("LedStatus");
        }
        if (payload.has("FanStatus")) {
            fanStatus = payload.getInt("FanStatus");
        }
        if (payload.has("WorkModeStatus")) {
            workModeStatus = payload.getInt("WorkModeStatus");
        }
        if (payload.has("TemLimit")) {
            temLimit = payload.getInt("TemLimit");
        }

        if (payload.has("dht11Tem") && payload.has("dht11Hum")) {
            int dht11Tem = payload.getInt("dht11Tem");
            int dht11Hum = payload.getInt("dht11Hum");
            String dht11 = dht11Tem + "," + dht11Hum;
            DeviceController deviceController = new DeviceController();
            deviceController.addDeviceData(DataType.MEASUREMENT, 1, dht11, null,null,null);
        }



        return result;
    }

    public Result findDevice(String deviceName) {
        Result result = new Result();
        Device device = deviceDao.findOneByDeviceName(deviceName);
        result.setData(device);
        result.setStatus(0);
        return result;
    }

    public Result findAllDevices() {
        Result result = new Result();
        List<Device> devices = deviceDao.findList(0, 9999);
        result.setData(devices);
        result.setStatus(0);
        return result;
    }

    public Result addDevice(Device device) {
        Result result = new Result();
        if (deviceDao.findOneByDeviceId(device.getId()) != null
                || deviceDao.findOneByDeviceName(device.getName()) != null) {
            result.setMsg("添加设备失败，请检查设备id或名字是否重复");
            return result;
        }
        try {
            deviceDao.insert(device);
            result.setStatus(0);
            result.setMsg("添加设备成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("添加设备失败");
        }
        return result;
    }

    public Result updateDeviceInfo(Device device) {
        Result result = new Result();
        Device deviceId = new Device();
        deviceId.setId(device.getId());
        try {
            deviceDao.update(device);
            result.setStatus(0);
            result.setMsg("修改设备信息成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("修改设备信息失败");
        }
        return result;
    }

    public Result deleteDevice(Device device) {
        Result result = new Result();
        try {
            deviceDao.delete(device.getId());
            result.setStatus(0);
            result.setMsg("删除设备完成");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("删除设备失败");
        }
        return result;
    }

    public Result findAllDeviceData(String deviceName) throws ClassNotFoundException {
        Result result = new Result();
        List data = deviceDataDao.findListByDeviceName(deviceName,0, 9999);
        result.setData(data);
        result.setStatus(0);
        Device device = deviceDao.findOneByDeviceName(deviceName);
        result.setMsg("dataType:" + device.getDataType());
        return result;
    }


    public Result deleteDeviceData(int deviceId) {
        Result result = new Result();
        try {
            if(deviceDataDao.delete(deviceId)){
                result.setStatus(0);
                result.setMsg("删除设备数据完成");
            } else {
                result.setMsg("不存在设备数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("删除设备数据失败");
        }
        return result;
    }

    public Result addDeviceData(int deviceId, String datetime, Data data) {
        Result result = new Result();
        try {
            deviceDataDao.insert(data);
            result.setStatus(0);
            result.setMsg("添加数据成功");
            Device device = deviceDao.findOneByDeviceId(deviceId);
            device.setLatestUpdateTime(datetime);
            deviceDao.update(device);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("添加数据失败");
        }
        return result;
    }

    public Result deleteDeviceData(String deviceName, String key, String value) {
        Result result = new Result();
        try {
            Device device = deviceDao.findOneByDeviceName(deviceName);
            if (device.getDataType().equals(DataType.STATUS)){
                if (deviceDataDao.delete(deviceName, key, Integer.parseInt(value))){
                    result.setStatus(0);
                    result.setMsg("删除设备数据完成");
                } else {
                    result.setMsg("不存在数据");
                }
            } else {
                if (deviceDataDao.delete(deviceName, key, value)){
                    result.setStatus(0);
                    result.setMsg("删除设备数据完成");
                } else {
                    result.setMsg("不存在数据");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("删除设备数据失败");
        }
        return result;
    }


}
