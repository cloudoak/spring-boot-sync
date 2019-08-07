package app.service;

import app.util.StreamUtils;
import app.config.HttpClientConfig;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 *
 * @author OAK
 *
 */
@Service
public class HttpConnectionPoolService {

    private static Logger logger = LoggerFactory.getLogger(HttpConnectionPoolService.class);

    @Autowired
    HttpClientConfig httpClientConfig;

    @Autowired
    ThreadFactory threadFactory;

    private static CloseableHttpClient httpClient;
    private static PoolingHttpClientConnectionManager manager;
    private static ScheduledExecutorService monitorExecutor;

    private static final String PREFIX = ":";

    private static final String REGEX_PREFIX = "/";

    private static final Integer RETRY_FREQUENCY = 3;

    private final static Object syncLock = new Object();

    private void setRequestConfig(HttpRequestBase httpRequestBase){
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(httpClientConfig.getConnectTimeout())
                .setConnectTimeout(httpClientConfig.getConnectTimeout())
                .setSocketTimeout(httpClientConfig.getSocketTimeout()).build();
        httpRequestBase.setConfig(requestConfig);
    }

    public CloseableHttpClient getHttpClient(String url){
        String hostName = url.split(REGEX_PREFIX)[2];
        int port = 80;
        if (hostName.contains(PREFIX)){
            String[] args = hostName.split(PREFIX);
            hostName = args[0];
            port = Integer.parseInt(args[1]);
        }
        if (httpClient == null){
            synchronized (syncLock){
                if (httpClient == null){
                    httpClient = createHttpClient(hostName, port);
                    monitorExecutor = new ScheduledThreadPoolExecutor(1);
                    monitorExecutor.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                        manager.closeExpiredConnections();
                        manager.closeIdleConnections(httpClientConfig.getIdelTimeout(), TimeUnit.MILLISECONDS);
                        logger.debug("close expired and idle for over 5s connection");
                        }
                    }, httpClientConfig.getMonitorInterval(), httpClientConfig.getMonitorInterval(), TimeUnit.MILLISECONDS);
                }
            }
        }
        return httpClient;
    }

    public CloseableHttpClient createHttpClient(String host, int port){
        ConnectionSocketFactory plainSocketFactory = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", plainSocketFactory).register("https", sslSocketFactory).build();
        manager = new PoolingHttpClientConnectionManager(registry);
        manager.setMaxTotal(httpClientConfig.getMaxConnTotal());
        manager.setDefaultMaxPerRoute(httpClientConfig.getMaxConnPerRoute());
        HttpHost httpHost = new HttpHost(host, port);
        manager.setMaxPerRoute(new HttpRoute(httpHost), httpClientConfig.getMaxConnRoute());

        HttpRequestRetryHandler handler = (e, i, httpContext) -> {
            if (i > RETRY_FREQUENCY){
                logger.error("retry has more than 3 time, give up request");
                return false;
            }
            if (e instanceof NoHttpResponseException){
                logger.error("receive no response from server, retry");
                return true;
            }
            if (e instanceof SSLHandshakeException){
                logger.error("SSL hand shake exception");
                return false;
            }
            if (e instanceof InterruptedIOException){
                logger.error("InterruptedIOException");
                return false;
            }
            if (e instanceof UnknownHostException){
                logger.error("server host unknown");
                return false;
            }
            if (e instanceof ConnectTimeoutException){
                logger.error("Connection Time out");
                return false;
            }
            if (e instanceof SSLException){
                logger.error("SSLException");
                return false;
            }

            HttpClientContext context = HttpClientContext.adapt(httpContext);
            HttpRequest request = context.getRequest();
            if (!(request instanceof HttpEntityEnclosingRequest)){
                return true;
            }
            return false;
        };
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(manager).setRetryHandler(handler).build();
        return client;
    }

    private void setPostParams(HttpPost httpPost, String requestBody){
        httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
    }

    public JSONObject post(String url, String requestBody){
        HttpPost httpPost = new HttpPost(url);
        setRequestConfig(httpPost);
        setPostParams(httpPost, requestBody);
        CloseableHttpResponse response = null;
        InputStream in = null;
        JSONObject object = null;
        try {
            response = getHttpClient(url).execute(httpPost, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                in = entity.getContent();
                object = JSONObject.parseObject(StreamUtils.ofBytes(in));
            }
        } catch (IOException e) {
            logger.error("http post {} find a fail, {}", url, e);
        } finally {
            try{
                if(in != null){
                    in.close();
                }
                if(response != null){
                    response.close();
                }
            } catch (IOException e) {
                logger.error("http post {} find a fail, {}", url, e);
            }
        }
        return object;
    }

    public void closeConnection(){
        try {
            httpClient.close();
            manager.close();
            monitorExecutor.shutdown();
        } catch (IOException e) {
            logger.error("close http connection pool find a fail, {}", e);
        }
    }
}
