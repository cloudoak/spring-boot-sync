package app.factory;

import app.jdbc.QueryCursor;
import app.message.MessageBody;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.SQLException;

/**
 *
 * @author OAK
 *
 */
public interface MysqlReaderFactory extends MessageBody {

     /**
      * Logback output logger.
      */
     Logger logger = LoggerFactory.getLogger(MysqlReaderFactory.class);

     /**
      * Constant statics count.
      */
     final String COUNT = "count";

     /**
      * Constant statics version.
      */
     final String DEFAULT_VERSION = "1";

     /**
      * According to query sql of Data source for Get a schema total.
      * @param sql sql.
      * @return Schema total.
      * @throws SQLException SQL exception.
      */
     default Integer getTotal(QueryCursor queryCursor, String sql) throws SQLException {
          Instant start = Instant.now();
          Integer total = 0;
          try {
               queryCursor.dataSource(getSource()).cursor(sql);
               if(queryCursor.dataSource(getSource()).cursor(sql.toString()).next()){
                    total = queryCursor.getInt(COUNT);
               }
               logger.debug("read sql {} total rows size {} costTime: {}, handle one records.", sql, total,
                       Instant.now().minus(start.getMillis()).getMillis());
          } catch (Throwable t) {
               logger.debug("read sql total rows size find a fail, {}", t);
          } finally {
               queryCursor.close();
          }
          return total;
     }

     /**
      * According to query sql of Data source for Get a schema total.
      * @param queryCursor Query cursor.
      * @param schemaName Schema name.
      * @param version Schema bulk version.
      * @return Schema total.
      * @throws SQLException SQL exception.
      */
     default Integer getTotal(QueryCursor queryCursor, String schemaName, String version) throws SQLException {
          StringBuilder sql = new StringBuilder(String.format("SELECT id AS count FROM %s ", schemaName));
          if(version != null){
               sql.append(String.format("WHERE version=%s ", version));
          }
          sql.append("ORDER BY id DESC LIMIT 1");
          Instant start = Instant.now();
          Integer total = 0;
          try {
               if(queryCursor.dataSource(getSource()).cursor(sql.toString()).next()){
                    total = queryCursor.getInt(COUNT);
               }
               logger.debug("read sql {} total rows size {} costTime: {}, handle one records.", sql, total,
                       Instant.now().minus(start.getMillis()).getMillis());
          } catch (Throwable t) {
               logger.debug("read sql total rows size find a fail, {}", t);
          } finally {
              queryCursor.close();
          }
          return total;
     }

     default void appendHeader(StringBuilder builder, String index, String id){
          builder.append(String.format("{\"index\":{\"_index\":\"%s\",\"_id\":\"%s\",\"_type\":\"_doc\"}}", index, id));
     }

     default void appendProperty(StringBuilder builder, String propertyName, Object propertyValue){
          builder.append(String.format("\"%s\":\"%s\"",propertyName, URLDecoder.decode(URLEncoder.encode(propertyValue.toString(), Charset.forName("UTF-8")), Charset.forName("UTF-8")).replace("%0A", "")));
     }

}