package app.factory;

import app.environment.SchemaConfig;
import app.jdbc.QueryCursor;
import app.message.MessageBody;
import app.util.Pagination;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author OAK
 *
 */
@Service("priceFactory")
public class MysqlReaderPriceFactory implements MysqlReaderFactory {

    private final static Logger logger = LoggerFactory.getLogger(MysqlReaderPriceFactory.class);

    @Autowired
    @Qualifier("dataSourcePriceMaster")
    private DataSource dataSource;

    private Multimap<String, ImmutableSet<String>> schemaRepositories = ImmutableListMultimap.of(
            "channel_price", ImmutableSet.of("sales_org","sales_office", "material", "distribution_channel", "customer", "customer_price_group", "country", "incoterm", "shipping_condition"),
            "list_price_mtm", ImmutableSet.of("sales_org", "sales_office", "material"),
             "list_price_cto", ImmutableSet.of("sales_org", "sales_office", "material", "variant")
    );

    @Autowired
    SchemaConfig schemaConfig;

    @Autowired
    ThreadFactory threadFactory;

    @Override
    public DataSource getSource() {
        return dataSource;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Autowired
    QueryCursor queryCursor;

    @Override
    public void callBack(Object target, Pagination pagination, Map attrs) {
        try{
            String indexName = attrs.getOrDefault("index", "_doc_" + Math.round(Double.MAX_VALUE)).toString();
            Map<String, String> mappings = (Map<String, String>) attrs.getOrDefault("mappings", Map.of());
            String [] columns = (String []) attrs.get("columns");
            String type = attrs.getOrDefault("type", "").toString();
            StringBuilder builder = new StringBuilder();
            Instant begin = Instant.now();
            String sql = pagination.getSql();
            Integer start = pagination.getStart(),
                    limit = pagination.getLimit(),
                    total = pagination.getTotal();
            if(total != -1){
                sql = sql + " WHERE id > " + start + " AND id <= " + limit;
            }
            try {
                Instant instant = Instant.now();
                Instant beginTransfer = Instant.now();
                logger.debug("read {},sql: {}. costTime: {}", pagination.getCurrentRecords(), sql, Instant.now().minus(instant.getMillis()).getMillis());
                while (queryCursor.dataSource(dataSource).cursor(sql).next()) {
                    StringBuilder search_key = new StringBuilder();
                    String id = UUID.randomUUID().toString();
                    if(!StringUtils.isEmpty(id)){
                        id = queryCursor.getString("ID");
                    }
//                    appendHeader(builder, indexName, id);
                    builder.append("\n{");
                    int i = 0, len = columns.length;
                    for (; i < len; i++) {
                        String columnName = columns[i];
                        String value = null;
                        try {
                            value = queryCursor.getString(columnName);
                        }catch (Throwable t){
                            logger.error("according column name to result set to get column value find a fail, {}", t);
                        }
                        if(null == value){
                            value = "";
                        }
                        if(schemaRepositories.get(type).stream().findFirst().get().contains(columnName.toLowerCase())){
                            if(StringUtils.isEmpty(value)){
                                value = "0";
                            }
                            search_key.append(value);
                        }
                        String columnMapping = mappings.get(columnName);
                        if(!StringUtils.isEmpty(columnMapping)){
                            columnName = columnMapping;
                        }
//                        appendProperty(builder, columnName, value);
                        builder.append(",");
                    }
                    if(search_key.length() > 0){
//                        appendProperty(builder, "search_key", search_key.toString());
                        builder.append(",");
                    }
//                    appendProperty(builder, "TYPE", type);
                    builder.append("}\n");
                    if (pagination.isWriteTo()) {
                        logger.debug("fetch {} records. costTime: {}. current size: {}", pagination.getWriteToBulkSize(), Instant.now().minus(beginTransfer.getMillis()).getMillis(), pagination.getCurrentRecords());
                        builder.append("\n");
                        invoke(target, "produce", builder);
                        builder = new StringBuilder();
                        beginTransfer = Instant.now();
                    }
                }
            } catch (Throwable t) {
                logger.debug("execute result set find a fail, {}", t);
            } finally {
                queryCursor.close();
            }
            try {
                if (builder != null && builder.length() > 0) {
                    builder.append("\n");
                    invoke(target, "produce", builder);
                }
            }catch (Throwable t){
                logger.debug("append last sql find a fail, {}", t);
            }
            long end = System.currentTimeMillis();
            logger.debug("read mysql costTime: {}, handle {} records.", Instant.now().minus(begin.getMillis()).getMillis(), pagination.getCurrentRecords());
        }catch (Exception ex){
            logger.debug("execute sql error {}", ex);
        }
    }
}
