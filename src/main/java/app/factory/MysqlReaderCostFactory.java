package app.factory;

import app.enums.ElasticsearchIndexType;
import app.jdbc.QueryCursor;
import app.templates.ElasticsearchTemplate;
import app.util.Pagination;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.util.*;

/**
 *
 * Mysql Reader Cost factory.
 *
 * @author OAK
 *
 */
@Service("costFactory")
public class MysqlReaderCostFactory implements MysqlReaderFactory {

    private final static Logger logger = LoggerFactory.getLogger(MysqlReaderCostFactory.class);

    @Autowired
    @Qualifier("dataSourceCostMaster")
    private DataSource dataSource;

    @Autowired
    SpringBeanFactory springBeanFactory;

    @Override
    public DataSource getSource() {
        return dataSource;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Autowired
    volatile QueryCursor queryCursor;

    @Override
    public void callBack(Object target, Pagination pagination, Map attrs) {
        try{
            ElasticsearchIndexType indexType = (ElasticsearchIndexType) attrs.getOrDefault("indexType", ElasticsearchIndexType.INDEX);
            ElasticsearchTemplate elasticsearchTemplate = springBeanFactory.getBean(String.format("%sTemplate", indexType.getDisplayName()));
            elasticsearchTemplate.refactoring();
            String indexName = attrs.getOrDefault("index", "_doc_" + Math.round(Double.MAX_VALUE)).toString();
            String [] columns = (String []) attrs.get("columns");
            String version = attrs.getOrDefault("version", DEFAULT_VERSION).toString();
            Instant begin = Instant.now();
            StringBuilder sql = new StringBuilder(pagination.getSql());
            Integer start = pagination.getStart(),
                    limit = pagination.getLimit(),
                    total = pagination.getTotal();
            if(total != -1){
                sql.append(String.format(" WHERE id > %d AND id <= %d", start, limit));
            }
            if(!version.equals(DEFAULT_VERSION)){
                sql.append(String.format(" AND version = %s", version));
            }
            try {
                Instant instant = Instant.now();
                logger.debug("read {},sql: {}. costTime: {}", pagination.getCurrentRecords(), sql, Instant.now().minus(instant.getMillis()).getMillis());
                Instant beginTransfer = Instant.now();
                while (queryCursor.dataSource(getSource()).cursor(sql.toString()).next()) {
                    elasticsearchTemplate.create(queryCursor, indexName, columns);
                    if (pagination.isWriteTo()) {
                        logger.debug("fetch {} records. costTime: {}. current size: {}", pagination.getWriteToBulkSize(), Instant.now().minus(beginTransfer.getMillis()).getMillis(), pagination.getCurrentRecords());
                        invoke(target, "send", elasticsearchTemplate.builder(), boolean.class, StringBuilder.class);
                        elasticsearchTemplate.refactoring();
                        beginTransfer = Instant.now();
                    }
                }
                if(!elasticsearchTemplate.isEmpty()){
                    invoke(target, "send", elasticsearchTemplate.builder(), boolean.class, StringBuilder.class);
                    elasticsearchTemplate.refactoring();
                }
            } catch (Throwable t) {
                logger.debug("execute result set find a fail, {}", t);
            } finally {
                queryCursor.close();
            }
            logger.debug("read mysql costTime: {}, handle {} records.", Instant.now().minus(begin.getMillis()).getMillis(), pagination.getCurrentRecords());
        }catch (Exception ex){
            logger.debug("execute sql error {}", ex);
        }
    }

}
