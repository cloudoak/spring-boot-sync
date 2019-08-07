package app.service.job;

import app.enums.ElasticsearchIndexType;
import app.environment.SchemaConfig;
import app.factory.MysqlReaderFactory;
import app.factory.SchemaFactory;
import app.factory.SchemaInfo;
import app.service.ElasticsearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 *
 * @author OAK
 *
 */
@Service("costHttpJob")
public class SyncHttpCostJobService implements SyncJobService {

    private final static Logger logger = LoggerFactory.getLogger(SyncHttpCostJobService.class);

    @Autowired
    @Qualifier("costHttpFactory")
    private MysqlReaderFactory factory;

    @Autowired
    private SchemaFactory schemaFactory;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private volatile SchemaConfig schemaConfig;

    @Autowired
    ThreadFactory threadFactory;

    @Override
    public void run()  {

        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory, new ThreadPoolExecutor.AbortPolicy());

        List<String> tableNames = Arrays.asList(schemaConfig.getTableNames());
        tableNames.forEach(tName -> {
            executorService.submit(() -> {
                try{
                    Integer idx = tableNames.indexOf(tName);
                    String index = schemaConfig.getIndexes()[idx];
                    Boolean mapping = schemaConfig.getMapping();
                    Boolean updateMapping = schemaConfig.getUpdateMapping();
                    SchemaInfo schemaInfo = schemaFactory.getSchemaInfo(tName);
                    if (null == schemaInfo) {
                        System.out.println("errorKey");
                    }
                    long begin = System.currentTimeMillis();

                    if (mapping) {
                        elasticsearchService.mapping(index, schemaInfo, updateMapping);
                    }
//                    Map<String, Integer> result = factory.rw(schemaInfo, index);
//                    int bulkNum = result.get("bulkNum");
//                    int esTime = result.get("esTime");
//                    logger.debug("handle success, result size :" + bulkNum);
//                    long end = System.currentTimeMillis();
//                    logger.debug("total: " + bulkNum + "; costTime: " + (end - begin) + "; esTime: " + esTime);
                    elasticsearchService.updateSettings(index, 5, 1, -1);
                }catch (Exception ex){
                    logger.debug("bulk error {}", ex);
                }
            });
        });
        executorService.shutdown();
    }

    @Override
    public void run(String indexName, ElasticsearchIndexType indexType, String version) {

    }
}
