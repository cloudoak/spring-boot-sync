package app.jdbc;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * Query cursor implements class.
 *
 * @author OAK
 *
 */
@Component
public class QueryCursorImpl implements QueryCursor {

    /**
     * Connection thread local variable.
     */
    ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    /**
     * Prepared statement thread local variable.
     */
    ThreadLocal<PreparedStatement> statementThreadLocal = new ThreadLocal<>();

    /**
     * Result set thread local variable.
     */
    ThreadLocal<ResultSet>  resultSetThreadLocal = new ThreadLocal<>();

    /**
     *
     * @param dataSource
     * @return
     */
    @Override
    public QueryCursor dataSource(DataSource dataSource) throws SQLException {
        if(connectionThreadLocal.get() == null){
            connectionThreadLocal.set(dataSource.getConnection());
        }
        return this;
    }

    /**
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    @Override
    public QueryCursor cursor(String sql) throws SQLException {
        Connection connection = connectionThreadLocal.get();
        if (connection == null){
            throw new SQLException("Could not found sql connection.");
        }
        PreparedStatement statement = statementThreadLocal.get();
        if(statement == null){
            statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            statement.setFetchSize(Integer.MIN_VALUE);
            statementThreadLocal.set(statement);
        }
        ResultSet resultSet = resultSetThreadLocal.get();
        if(resultSet  == null){
            resultSet = statementThreadLocal.get().executeQuery();
            resultSetThreadLocal.set(resultSet);
        }
        return this;
    }

    @Override
    public Boolean next() throws SQLException {
        return resultSetThreadLocal.get().next();
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return resultSetThreadLocal.get().getString(columnLabel);
    }

    @Override
    public Integer getInt(String columnLabel) throws SQLException {
        return resultSetThreadLocal.get().getInt(columnLabel);
    }

    /**
     *
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
        if(resultSetThreadLocal.get() != null){
            resultSetThreadLocal.get().close();
            resultSetThreadLocal.remove();
        }
        if(statementThreadLocal.get() != null){
            statementThreadLocal.get().close();
            statementThreadLocal.remove();
        }
        if(connectionThreadLocal.get() != null){
            connectionThreadLocal.get().close();
            connectionThreadLocal.remove();
        }
    }

}
