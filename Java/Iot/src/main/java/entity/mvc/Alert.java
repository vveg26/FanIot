package entity.mvc;

/**
 * @author aeolus
 * @program IOT201851385129
 * @description 文本预警消息型文档实体类
 * @date 2021-05-30 22:27:14
 */
public class Alert extends Data{
    private int deviceId;
    private String timing; // 时间戳
    private String  news; // 消息

    public Alert() {
    }

    public Alert(int deviceId, String timing, String  news) {
        this.deviceId = deviceId;
        this.timing = timing;
        this.news = news;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "deviceId='" + deviceId + '\'' +
                ", timing=" + timing +
                ", news='" + news + '\'' +
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

    public void setTiming(String  timing) {
        this.timing = timing;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }
}
