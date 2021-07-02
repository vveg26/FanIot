package entity.mvc;


/**
 * @author aeolus
 * @program IOT201851385129
 * @description 开关状态型文档实体类
 * @String 2021-05-30 22:21:44
 */

public class Status extends Data{
    private int deviceId;
    private String timing; // 时间戳
    private int value; // 状态（0：关，1：开）

    public Status() {
    }

    public Status(int deviceId, String timing, int value) {
        this.deviceId = deviceId;
        this.timing = timing;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Status{" +
                "deviceId='" + deviceId + '\'' +
                ", timing=" + timing +
                ", value=" + value +
                '}';
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
