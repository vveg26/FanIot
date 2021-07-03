package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author aeolus
 * @program SSMDemo
 * @description
 * @date 2021-05-17 10:23:10
 */

@Configuration
@PropertySource(value = "classpath:mongoConfig.properties")
public class MongoProperty {
    @Value("localhost")
    private String host;

    @Value("27017")
    private int port;

    @Value("iot")
    private String dbname;

    @Value("10")
    private int connectsPerHost;

    @Value("5")
    private int multiplier;

    @Value("1000")
    private int connectTimeout;

    @Value("1500")
    private int maxWaitTime;

    @Value("true")
    private boolean socketKeepAlive;

    @Value("1500")
    private int socketTimeout;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public int getConnectsPerHost() {
        return connectsPerHost;
    }

    public void setConnectsPerHost(int connectsPerHost) {
        this.connectsPerHost = connectsPerHost;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public boolean isSocketKeepAlive() {
        return socketKeepAlive;
    }

    public void setSocketKeepAlive(boolean socketKeepAlive) {
        this.socketKeepAlive = socketKeepAlive;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    @Override
    public String toString() {
        return "MongoProperty{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", dbname='" + dbname + '\'' +
                ", connectsPerHost=" + connectsPerHost +
                ", multiplier=" + multiplier +
                ", connectTimeout=" + connectTimeout +
                ", maxWaitTime=" + maxWaitTime +
                ", socketKeepAlive=" + socketKeepAlive +
                ", socketTimeout=" + socketTimeout +
                '}';
    }
}
