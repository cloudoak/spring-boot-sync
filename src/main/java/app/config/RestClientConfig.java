package app.config;

import app.environment.ElasticsearchConfig;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 *
 * @author OAK
 *
 */
@Component
public class RestClientConfig {

    @Autowired
    private ElasticsearchConfig esConfig;

    @Bean
    public RestClient getRestClient(){
        RestClient restClient = RestClient.builder(new HttpHost(esConfig.getHost(), esConfig.getPort(),
                esConfig.getProtocol())).setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder.setSocketTimeout(esConfig.getSocketTimeout())).setHttpClientConfigCallback(httpClientConfigCallback -> {
            httpClientConfigCallback.setMaxConnTotal(esConfig.getMaxConnTotal());
            httpClientConfigCallback.setMaxConnPerRoute(esConfig.getMaxConnPerRoute());
            httpClientConfigCallback.setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(esConfig.getIoThreadCount())
                    .setConnectTimeout(3000).setSelectInterval(3000).setSoTimeout(3000).setSoKeepAlive(true).build());
            return httpClientConfigCallback;
        }).build();
        return restClient;
    }

}
