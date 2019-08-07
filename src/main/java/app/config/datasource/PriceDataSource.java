package app.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class PriceDataSource {

    @Bean(name = "dataSourcePriceMaster")
    @ConfigurationProperties(prefix = "spring.datasource.price")
    public DataSource dataSourcePriceMaster() {
        //指定使用DruidDataSource
        return DataSourceBuilder.create()
                .type(DruidDataSource.class)
                .build();
    }

}
