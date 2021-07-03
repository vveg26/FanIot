package entity.mvc;

/**
 * @author aeolus
 * @program IOT201851385129
 * @description 数值测量值型文档实体类
 * @date 2021-05-30 21:59:57
 */

public class Measurement extends Data{
    private int deviceId;
    private String timing; // 时间戳
    private String value; // 数值

    public Measurement() {
    }

    public Measurement(int deviceId, String timing, String value) {
        this.deviceId = deviceId;
        this.timing = timing;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "deviceId='" + deviceId + '\'' +
                ", timing=" + timing +
                ", value='" + value + '\'' +
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
