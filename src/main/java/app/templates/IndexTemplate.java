package app.templates;

import app.enums.ElasticsearchIndexType;
import app.jdbc.QueryCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.sql.SQLException;

/**
 *
 * Elasticsearch index basic template
 * Provide create template method.
 *
 * @author OAK
 * @since 2019/07/25 11:12 AM.
 * @version 1.0
 *
 */
@Component
public class IndexTemplate implements ElasticsearchTemplate {

    /**
     * Logback logger.
     */
    private final static Logger logger = LoggerFactory.getLogger(IndexTemplate.class);

    @Override
    public void create(QueryCursor cursor, String indexName, String... templateProperties) throws SQLException {
//        logger.debug("According 【index {}, arguments {}】 to created elasticsearch index template", indexName, templateProperties);
        String docId = cursor.getString("id");
        constructTemplate(ElasticsearchIndexType.INDEX, indexName, docId);
        builder().append("\n{");
        append(cursor, templateProperties);
        append("TYPE", indexName);
        builder().append("}\n");
        builder().append("\n");
    }

}
