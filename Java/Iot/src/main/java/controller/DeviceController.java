package controller;

import controller.interceptor.Developer;
import controller.interceptor.Staff;
import entity.mvc.*;
import entity.mvc.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.DeviceManageService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author aeolus
 * @program SSMDemo
 * @description
 * @date 2021-04-19 11:30:39
 */


@Controller
@RequestMapping("device")
public class DeviceController {

    @Autowired
    private DeviceManageService deviceManageService;

    @GetMapping("/find_device")
    @ResponseBody
    @Staff
    @Developer
    public Result findDevice(Device device) {
        return deviceManageService.findDevice(device.getName());
    }

    @GetMapping("/find_all_devices")
    @ResponseBody
    @Staff
    @Developer
    public Result findALLDevices() {
        return deviceManageService.findAllDevices();
    }

    @PostMapping("/add_device")
    @ResponseBody
    @Staff
    public Result addDevice(Device device) throws IOException {
        System.out.println("获取待新增设备信息：" + device);
        return deviceManageService.addDevice(device);
    }

    @PutMapping("/update_device")
    @ResponseBody
    @Staff
    public Result updateDeviceInfo(Device device) {
        System.out.println("获取待更新设备信息：" + device);
        return deviceManageService.updateDeviceInfo(device);
    }

    @DeleteMapping("/delete_device")
    @ResponseBody
    @Staff
    public Result deleteDevice(Device device) {
        System.out.println("获取待删除设备信息：" + device);
        return deviceManageService.deleteDevice(device);
    }

    @GetMapping("/find_device_all_data")
    @ResponseBody
    @Staff
    public Result findDeviceData(Device device) throws ClassNotFoundException {
        System.out.println("获取待查找数据的设备信息：" + device);
        return deviceManageService.findAllDeviceData(device.getName());
    }

    @PostMapping("/add_device_data")
    @ResponseBody
    @Developer
    public Result addDeviceData(String dataType, int deviceId, String value, String longitude, String
            latitude, String elevation) {
        System.out.println("获取待新增的设备数据：" + "dataType = " + dataType + ", deviceId = " + deviceId + ", value = " + value + ", longitude = " + longitude + ", latitude = " + latitude + ", elevation = " + elevation);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());

        Data data = null;
        if (dataType.equals(DataType.WAYPOINT)) {
            data = new Waypoint(deviceId, datetime, longitude, latitude, elevation);
        }
        if (dataType.equals(DataType.STATUS)) {
            data = new Status(deviceId, datetime, Integer.parseInt(value));
        }
        if (dataType.equals(DataType.MEASUREMENT)) {
            data = new Measurement(deviceId, datetime, value);
        }
        if (dataType.equals(DataType.ALERT)) {
            data = new Alert(deviceId, datetime, value);
        }
        return deviceManageService.addDeviceData(deviceId, datetime, data);
    }

    @PostMapping("/add_device_data_staff")
    @ResponseBody
    @Staff
    public Result addDeviceDataStaff(String dataType, int deviceId, String value, String longitude, String
            latitude, String elevation) {
        System.out.println("获取待新增的设备数据：" + "dataType = " + dataType + ", deviceId = " + deviceId + ", value = " + value + ", longitude = " + longitude + ", latitude = " + latitude + ", elevation = " + elevation);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = sdf.format(new Date());

        Data data = null;
        if (dataType.equals(DataType.WAYPOINT)) {
            data = new Waypoint(deviceId, datetime, longitude, latitude, elevation);
        }
        if (dataType.equals(DataType.STATUS)) {
            data = new Status(deviceId, datetime, Integer.parseInt(value));
        }
        if (dataType.equals(DataType.MEASUREMENT)) {
            data = new Measurement(deviceId, datetime, value);
        }
        if (dataType.equals(DataType.ALERT)) {
            data = new Alert(deviceId, datetime, value);
        }
        return deviceManageService.addDeviceData(deviceId, datetime, data);
    }



    @DeleteMapping("/delete_device_data")
    @ResponseBody
    @Developer
    public Result deleteDeviceData(int deviceId, String deviceName,
                                   String key, String value) {
        System.out.println("获取待删除数据的设备信息：" + "deviceId = " + deviceId + ", deviceName = " + deviceName + ", key = " + key + ", value = " + value);
        // 根据设备id删除所有数据
        if (("".equals(deviceName) || deviceName == null)
                && ("".equals(key) || key == null)
                && ("".equals(value) || value == null)
                && (deviceId != 0)) {
            return deviceManageService.deleteDeviceData(deviceId);
        } else { // 根据key、value删除数据
            return deviceManageService.deleteDeviceData(deviceName, key, value);
        }
    }


}
