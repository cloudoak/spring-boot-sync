package app.environment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author OAK
 *
 */
@Component
@ConfigurationProperties("spring.threadpool")
public class ThreadPoolConfig {

    private Integer corePoolSize = 20;

    private Integer maximumPoolSize = 50;

    private Long keepAliveTime = 0L;

    private String unit = "MILLISECONDS";

    private Integer recycleQuantity = 20;

    private Integer queueQuantity = 200000;

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(Integer maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public Long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(Long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getRecycleQuantity() {
        return recycleQuantity;
    }

    public void setRecycleQuantity(Integer recycleQuantity) {
        this.recycleQuantity = recycleQuantity;
    }

    public Integer getQueueQuantity() {
        return queueQuantity;
    }

    public void setQueueQuantity(Integer queueQuantity) {
        this.queueQuantity = queueQuantity;
    }
}
