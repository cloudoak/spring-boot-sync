package app.templates;

import app.enums.ElasticsearchIndexType;
import app.jdbc.QueryCursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 *
 * Elasticsearch create,delete,index,update basic template
 * Provide create template method.
 *
 * @author OAK
 * @since 2019/07/25 11:12 AM.
 * @version 1.0
 *
 */
public interface ElasticsearchTemplate {

    /**
     * Logback logger.
     */
    final static Logger logger = LoggerFactory.getLogger(ElasticsearchTemplate.class);

    /**
     * Constant prefix.
     */
    final String PREFIX = "_";

    /**
     * Constant default doc type for default _doc.
     */
    final String DEFAULT_DOC = "_doc";

    /**
     * Constant retry on conflict for default three times.
     */
    final Integer RETRY_ON_CONFLICT = 3;

    /**
     * Constant elasticsearch template doc as upsert for default true.
     */
    final Boolean DOC_AS_UPSERT = true;

    /**
     * Constant elasticsearch template doc basic system properties.
     */
    final String [] templateProperties = {
            "index", "id", "type"
    };

    /**
     * Judge current elasticsearch template whether exists.
     * @return no exists.
     */
    default Boolean isEmpty(){
        return builder().length() == 0;
    }

    ThreadLocal<StringBuilder> templateBuilder = new ThreadLocal<>();

    /**
     * Build or Reset current elasticsearch template.
     */
    default void refactoring() {
        templateBuilder.set(new StringBuilder());
    }

    /**
     * Get current elasticsearch template.
     * @return current elasticsearch template.
     */
    default StringBuilder builder() {
        return templateBuilder.get();
    }

    /**
     * Construct a elasticsearch template basic system properties.
     * @param indexType Current elasticsearch index type.
     * @param templateProperty Current elasticsearch template property.
     * @param <T> Serializable.
     */
    default <T> void constructTemplate(ElasticsearchIndexType indexType, T templateProperty){
        builder().append(String.format("{\"%s\":", indexType.getDisplayName()));
        java.lang.Class<?> templatePropertyClass = templateProperty.getClass();
        java.lang.invoke.MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            for (String templateProp : templateProperties) {
                VarHandle varHandle = lookup.findVarHandle(templatePropertyClass, PREFIX + templateProp, String.class);
                Object value = varHandle.get(templateProperty);
                if(StringUtils.isEmpty(value)){
                    if(templateProp.equals("type")){
                        value = PREFIX + DEFAULT_DOC;
                    }else if(templateProp.equals("id")){
                        value = UUID.randomUUID().toString();
                    }
                }
                if(!StringUtils.isEmpty(value)){
                    builder().append(String.format("\"%s%s\":\"%s\"", PREFIX, templateProp, value));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        builder().append("}");
    }

    /**
     * Construct a elasticsearch template basic system properties.
     * @param indexType Current elasticsearch index type.
     * @param indexName Current elasticsearch template property.
     * @param docId Current elasticsearch template doc id.
     * @param <T> Serializable.
     */
    default <T> void constructTemplate(ElasticsearchIndexType indexType, String indexName, String docId){
        builder().append(String.format("{\"%s\":{\"_index\":\"%s\",\"_id\":\"%s\",\"_type\":\"_doc\",\"_retry_on_conflict\":%d}}",
                indexType.getDisplayName(), indexName, docId, RETRY_ON_CONFLICT));
    }

    /**
     * Append property of Current Elasticsearch index to String builder.
     * @param propertyName property Name.
     * @param propertyValue property Value.
     */
    default void append(String propertyName, Object propertyValue){
        builder().append(String.format("\"%s\":\"%s\"",propertyName, URLDecoder.decode(URLEncoder.encode(propertyValue.toString(), Charset.forName("UTF-8")), Charset.forName("UTF-8")).replace("%0A", "")));
    }

    /**
     *  Append current template properties of Current Elasticsearch index to String builder.
     * @param cursor a query cursor.
     * @param templateProperties current template properties.
     */
    default void append(QueryCursor cursor, String... templateProperties){
        int i = 0, len = templateProperties.length;
        for (; i < len; i++) {
            String propertyName = templateProperties[i];
            String value = null;
            try {
                value = cursor.getString(propertyName);
            }catch (Throwable t){
                logger.error("according column name to result set to get column value find a fail, {}", t);
            }
            if(StringUtils.isEmpty(value)){
                value = "";
            }
            append(propertyName, value);
            builder().append(",");
        }
    }

    /**
     * According query cursor and index name and template properties to created a elasticsearch template.
     * @param cursor a query cursor.
     * @param indexName Current index name.
     * @param templateProperties Current template properties.
     * @throws SQLException Throw SQL exception.
     */
    void create(QueryCursor cursor, String indexName, String... templateProperties) throws SQLException;

}
