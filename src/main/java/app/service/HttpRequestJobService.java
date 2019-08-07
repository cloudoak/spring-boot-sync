package app.service;

import app.config.HttpClientConfig;
import app.enums.ElasticsearchIndexType;
import app.service.job.SyncJobService;
import app.util.StreamUtils;
import com.alibaba.fastjson.JSONObject;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.*;

@Service("httprequestJob")
public class HttpRequestJobService implements SyncJobService {

    private static Logger logger = LoggerFactory.getLogger(HttpRequestJobService.class);

    @Autowired
    HttpClientConfig httpClientConfig;

    @Autowired
    HttpConnectionPoolService httpConnectionPoolService;

    @Autowired
    ThreadFactory threadFactory;

    private final Integer MAX_SIZE = 10;

    LinkedBlockingQueue<String> queues = new LinkedBlockingQueue(MAX_SIZE);

    @Override
    public void run() {
        ExecutorService executorService = new ThreadPoolExecutor(MAX_SIZE, MAX_SIZE, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory, new ThreadPoolExecutor.AbortPolicy());
        CompletionService<JSONObject> completionService = new ExecutorCompletionService<>(executorService);

        String uris = Arrays.stream(httpClientConfig.getUris()).findFirst().get();
        Integer port = httpClientConfig.getPort();
        httpConnectionPoolService.createHttpClient( uris, port);
        for(int i = 1; i <= MAX_SIZE; i++){
            String jsonContent = StreamUtils.of(JobService.class.getResourceAsStream("/metadata/" + i + ".json"));
            queues.add(jsonContent);
        }

        for(int i = 1;  i <= MAX_SIZE; i++){
            completionService.submit(() -> {
                Instant instant = Instant.now();
                JSONObject jsonObject = httpConnectionPoolService.post(httpClientConfig.getRestUris(), queues.poll());
                long mills = Instant.now().minus(instant.getMillis()).getMillis();
                logger.info("thread Name: {} result total size: {} cost Time: {}", Thread.currentThread().getName(),
                        jsonObject.getJSONObject("hits").getInteger("total"), mills);
                JSONObject result = new JSONObject();
                result.put("status", jsonObject.getString("status"));
                result.put("total", jsonObject.getString("total"));
                result.put("mills", mills);
                return result;
            });
        }
        executorService.shutdown();
        try {
            Future<JSONObject> item = null;
            while((item = completionService.poll()) != null){
                if(item.isDone()){
                    JSONObject result = item.get();
                    System.out.println(result.getLong("mills"));
                }
            }
        } catch (Exception e) {
            logger.error("shut down pool find a fail, {}", e);
        }
    }

    @Override
    public void run(String indexName, ElasticsearchIndexType indexType, String version) {

    }
}
