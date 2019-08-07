package app.templates;

import app.enums.ElasticsearchIndexType;
import app.jdbc.QueryCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.sql.SQLException;

/**
 *
 * Elasticsearch create basic template
 * Provide create template method.
 *
 * @author OAK
 * @since 2019/07/25 11:12 AM.
 * @version 1.0
 *
 */
@Component
public class CreateTemplate implements ElasticsearchTemplate {

    /**
     * Logback logger.
     */
    private final static Logger logger = LoggerFactory.getLogger(DeleteTemplate.class);

    @Override
    public void create(QueryCursor cursor, String indexName, String... templateProperties) throws SQLException {
//        logger.debug("According 【index {}, arguments {}】 to created elasticsearch create template", indexName, templateProperties);
        String docId = cursor.getString("id");
        constructTemplate(ElasticsearchIndexType.CREATE, indexName, docId);
        builder().append("\n{");
        append(cursor, templateProperties);
        append("type", indexName);
        builder().append("}\n");
        builder().append("\n");
    }

}
