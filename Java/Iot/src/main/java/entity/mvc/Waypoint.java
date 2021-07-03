package entity.mvc;


/**
 * @author aeolus
 * @program IOT201851385129
 * @description 地理位置定位型文档实体类
 * @String 2021-05-30 22:26:43
 */
public class Waypoint extends Data{
    private int deviceId;
    private String timing; // 时间戳
    private String longitude; // 经度
    private String latitude; // 纬度
    private String elevation; // 海拔

    public Waypoint() {
    }

    public Waypoint(int deviceId, String timing, String longitude, String latitude, String elevation) {
        this.deviceId = deviceId;
        this.timing = timing;
        this.longitude = longitude;
        this.latitude = latitude;
        this.elevation = elevation;
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                "deviceId='" + deviceId + '\'' +
                ", timing=" + timing +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", elevation='" + elevation + '\'' +
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getElevation() {
        return elevation;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }
}

