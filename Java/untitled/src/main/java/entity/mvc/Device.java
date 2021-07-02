package entity.mvc;

/**
 * @author aeolus
 * @program IOT201851385129
 * @description 物联网设备实体类
 * @date 2021-05-30 22:58:19
 */

public class Device {
    private int id;
    private String name;
    private String latestUpdateTime;
    private int online;
    private String dataType;

    public Device() {
    }

    public Device(int id, String name, String  latestUpdateTime, int online, String dataType) {
        this.id = id;
        this.name = name;
        this.latestUpdateTime = latestUpdateTime;
        this.online = online;
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latestUpdateTime=" + latestUpdateTime +
                ", online=" + online +
                ", dataType='" + dataType + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String  getLatestUpdateTime() {
        return latestUpdateTime;
    }

    public void setLatestUpdateTime(String latestUpdateTime) {
        this.latestUpdateTime = latestUpdateTime;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
