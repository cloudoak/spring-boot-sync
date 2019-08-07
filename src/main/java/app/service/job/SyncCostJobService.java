package app.service.job;

import app.brokers.Broker;
import app.enums.ElasticsearchIndexType;
import app.environment.SchemaConfig;
import app.factory.*;
import app.jdbc.QueryCursor;
import app.service.ElasticsearchService;
import app.templates.ElasticsearchTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.*;

import static app.factory.MysqlReaderFactory.DEFAULT_VERSION;

/**
 *
 * @author OAK
 *
 */
@Service("costJob")
public class SyncCostJobService implements SyncJobService {

    private final static Logger logger = LoggerFactory.getLogger(SyncCostJobService.class);

    @Autowired
    @Qualifier("costFactory")
    private volatile MysqlReaderCostFactory messageBody;

    @Autowired
    private SchemaFactory schemaFactory;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private volatile SchemaConfig schemaConfig;

    @Autowired
    ThreadFactory threadFactory;

    @Autowired
    @Qualifier("schemaBroker")
    Broker broker;

    @Autowired
    QueryCursor queryCursor;

    @Override
    public void run(String indexName, ElasticsearchIndexType indexType, String version)  {
        try{
            SchemaInfo schemaInfo = schemaFactory.getSchemaInfo(indexName);
            if (null == schemaInfo) {
                System.out.println("errorKey");
            }
            if (indexType.equals(ElasticsearchIndexType.INDEX) || indexType.equals(ElasticsearchIndexType.CREATE)) {
                elasticsearchService.addMapping(indexName, schemaInfo);
                elasticsearchService.addSettings(indexName, schemaInfo, 5, 0, -1);
            }
            Integer total = messageBody.getTotal(queryCursor, schemaInfo.getTableName(), null),
                    //messageBody.getTotal(queryCursor, indexName, null),
                    bulkSize = schemaConfig.getBulkSize(),
                    pageSize = schemaConfig.getPageSize();

            Map multiValueMap = new LinkedHashMap();
            multiValueMap.put("total", total);
            multiValueMap.put("bulkSize", bulkSize);
            multiValueMap.put("pageSize", pageSize);
            multiValueMap.put("sql", schemaInfo.sql());
            multiValueMap.put("index", indexName);
            multiValueMap.put("version", StringUtils.isEmpty(version) ? DEFAULT_VERSION : version);
            multiValueMap.put("mappings", schemaInfo.columnMaps());
            multiValueMap.put("columns", schemaInfo.getColumns());
            multiValueMap.put("indexType", indexType);
            messageBody.putAttrs(multiValueMap);
            if(!broker.isStart()){
                broker.start();
            }
            broker.sendMessage(messageBody);
            ConcurrentMap stopArgumentsMap = new ConcurrentHashMap(2);
            stopArgumentsMap.put("index", indexName);
            stopArgumentsMap.put("replicas", 1);
            broker.stop(stopArgumentsMap);
        }catch (Exception ex){
            logger.debug("bulk error {}", ex);
        }
    }

    @Deprecated
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
                    if (mapping) {
                        elasticsearchService.addMapping(index, schemaInfo);
                        elasticsearchService.addSettings(index, schemaInfo, 5, 0, -1);
                    }
                    Integer total = messageBody.getTotal(queryCursor, index, null),
                            bulkSize = schemaConfig.getBulkSize(),
                            pageSize = schemaConfig.getPageSize();

                    Map multiValueMap = new LinkedHashMap();
                    multiValueMap.put("total", total);
                    multiValueMap.put("bulkSize", bulkSize);
                    multiValueMap.put("pageSize", pageSize);
                    multiValueMap.put("sql", schemaInfo.sql());
                    multiValueMap.put("index", index);
                    //multiValueMap.put("version", version);
                    multiValueMap.put("mappings", schemaInfo.columnMaps());
                    multiValueMap.put("columns", schemaInfo.getColumns());
                    messageBody.putAttrs(multiValueMap);
                    if(!broker.isStart()){
                        broker.start();
                    }
                    broker.sendMessage(messageBody);
                    ConcurrentMap stopArgumentsMap = new ConcurrentHashMap();
                    stopArgumentsMap.put("index", index);
                    stopArgumentsMap.put("replicas", 1);
                    broker.stop(stopArgumentsMap);
                }catch (Exception ex){
                    logger.debug("bulk error {}", ex);
                }
            });
        });
        executorService.shutdown();
    }
}