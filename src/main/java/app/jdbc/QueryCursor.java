package app.jdbc;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * Query cursor interface.
 * @author OAK
 *
 */
public interface QueryCursor {

    QueryCursor dataSource(DataSource dataSource) throws SQLException;

    QueryCursor cursor(String sql) throws SQLException;

    Boolean next() throws SQLException;

    Integer getInt(String columnLabel) throws SQLException;

    String getString(String columnLabel) throws SQLException;

    void close() throws SQLException;

}
