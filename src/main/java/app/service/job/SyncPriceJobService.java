package app.service.job;

import app.enums.ElasticsearchIndexType;
import app.environment.SchemaConfig;
import app.brokers.Broker;
import app.factory.MysqlReaderFactory;
import app.factory.SchemaFactory;
import app.factory.SchemaInfo;
import app.jdbc.QueryCursor;
import app.service.ElasticsearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 *
 *
 * @author OAK
 *
 */
@Service("priceJob")
public class SyncPriceJobService implements SyncJobService{

    private final static Logger logger = LoggerFactory.getLogger(SyncPriceJobService.class);

    @Autowired
    @Qualifier("priceFactory")
    private MysqlReaderFactory factory;

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
    public void run(String indexName, ElasticsearchIndexType indexType, String version) {
        try{
            Boolean mapping = schemaConfig.getMapping();
            Boolean updateMapping = schemaConfig.getUpdateMapping();
            SchemaInfo schemaInfo = schemaFactory.getSchemaInfo(indexName);
            if (null == schemaInfo) {
                System.out.println("errorKey");
            }
            long begin = System.currentTimeMillis();

            if (mapping) {
                elasticsearchService.mapping(indexName, schemaInfo, updateMapping);
            }
            Integer total = factory.getTotal(queryCursor, indexName, null),
                    bulkSize = schemaConfig.getBulkSize(),
                    pageSize = schemaConfig.getPageSize();

            Map multiValueMap = new LinkedHashMap();
            multiValueMap.put("total", total);
            multiValueMap.put("bulkSize", bulkSize);
            multiValueMap.put("pageSize", pageSize);
            multiValueMap.put("sql", schemaInfo.sql());
            multiValueMap.put("index", indexName);
            multiValueMap.put("version", version);
            multiValueMap.put("mappings", schemaInfo.columnMaps());
            multiValueMap.put("columns", schemaInfo.getColumns());
            factory.putAttrs(multiValueMap);
            if(!broker.isStart()){
                broker.start();
            }
            broker.sendMessage(factory);
            elasticsearchService.updateNumberOfReplicas(indexName, 1);
        }catch (Exception ex){
            logger.debug("bulk error {}", ex);
        }
    }

    @Deprecated
    @Override
    public void run()  {

        ExecutorService executorService = new ThreadPoolExecutor(3, 3, 0L,
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
                    Integer total = factory.getTotal(queryCursor, index, null),
                            bulkSize = schemaConfig.getBulkSize(),
                            pageSize = schemaConfig.getPageSize();

                    Map multiValueMap = new LinkedHashMap();
                    multiValueMap.put("total", total);
                    multiValueMap.put("bulkSize", bulkSize);
                    multiValueMap.put("pageSize", pageSize);
                    multiValueMap.put("sql", schemaInfo.sql());
                    multiValueMap.put("index", index);
                    multiValueMap.put("mappings", schemaInfo.columnMaps());
                    multiValueMap.put("columns", schemaInfo.getColumns());
                    factory.putAttrs(multiValueMap);
                    if(!broker.isStart()){
                        broker.start();
                    }
                    broker.sendMessage(factory);
                    elasticsearchService.updateNumberOfReplicas(index, 1);
                }catch (Exception ex){
                    logger.debug("bulk error {}", ex);
                }
            });
        });
        executorService.shutdown();
    }
}
