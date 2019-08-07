package app.templates;

import app.enums.ElasticsearchIndexType;
import app.jdbc.QueryCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.sql.SQLException;

/**
 *
 * Elasticsearch update basic template
 * Provide create template method.
 *
 * @author OAK
 * @since 2019/07/25 11:12 AM.
 * @version 1.0
 *
 */
@Component
public class UpdateTemplate implements ElasticsearchTemplate {

    /**
     * Logback logger.
     */
    private final static Logger logger = LoggerFactory.getLogger(IndexTemplate.class);

    @Override
    public void create(QueryCursor cursor, String indexName, String... templateProperties) throws SQLException {
//        logger.debug("According 【index {}, arguments {}】 to created elasticsearch update template", indexName, templateProperties);
        String docId = cursor.getString("id");
        constructTemplate(ElasticsearchIndexType.UPDATE, indexName, docId);
        builder().append(String.format("\n{\"doc_as_upsert\": \"%s\",\"doc\" : {", DOC_AS_UPSERT));
        append(cursor, templateProperties);
        append("TYPE", indexName);
        builder().append("}}\n");
        builder().append("\n");
    }

}
