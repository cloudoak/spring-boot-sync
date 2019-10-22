package app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.boot.http")
public class HttpClientConfig {

    private String [] uris;

    private Integer port;

    private String restUris;

    private Integer connectTimeout;

    private Integer socketTimeout;

    private Integer maxConnTotal;

    private Integer maxConnPerRoute;

    private Integer maxConnRoute;

    private Integer monitorInterval;

    private Integer idelTimeout;

    public String[] getUris() {
        return uris;
    }

    public void setUris(String[] uris) {
        this.uris = uris;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getRestUris() {
        return restUris;
    }

    public void setRestUris(String restUris) {
        this.restUris = restUris;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Integer getMaxConnTotal() {
        return maxConnTotal;
    }

    public void setMaxConnTotal(Integer maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
    }

    public Integer getMaxConnPerRoute() {
        return maxConnPerRoute;
    }

    public void setMaxConnPerRoute(Integer maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
    }

    public Integer getMaxConnRoute() {
        return maxConnRoute;
    }

    public void setMaxConnRoute(Integer maxConnRoute) {
        this.maxConnRoute = maxConnRoute;
    }

    public Integer getMonitorInterval() {
        return monitorInterval;
    }

    public void setMonitorInterval(Integer monitorInterval) {
        this.monitorInterval = monitorInterval;
    }

    public Integer getIdelTimeout() {
        return idelTimeout;
    }

    public void setIdelTimeout(Integer idelTimeout) {
        this.idelTimeout = idelTimeout;
    }
}
