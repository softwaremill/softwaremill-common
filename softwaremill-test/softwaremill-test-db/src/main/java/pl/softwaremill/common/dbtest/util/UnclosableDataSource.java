package pl.softwaremill.common.dbtest.util;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Map;
import java.util.Properties;

/**
 * User: szimano
 */
public class UnclosableDataSource implements DataSource {

    private DataSource delegate;

    public UnclosableDataSource(DataSource delegate) {
        this.delegate = delegate;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new UnclosableConnectionWrapper(delegate.getConnection());
    }

    @Override
    public Connection getConnection(String s, String s1) throws SQLException {
        return new UnclosableConnectionWrapper(delegate.getConnection(s, s1));
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return delegate.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        delegate.setLogWriter(printWriter);
    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {
        delegate.setLoginTimeout(i);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return delegate.getLoginTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> tClass) throws SQLException {
        return delegate.unwrap(tClass);
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return delegate.isWrapperFor(aClass);
    }
}

class UnclosableConnectionWrapper implements Connection{
    private Connection delegate;

    UnclosableConnectionWrapper(Connection delegate) {
        this.delegate = delegate;
    }

    public Statement createStatement() throws SQLException {
        return delegate.createStatement();
    }

    public PreparedStatement prepareStatement(String s) throws SQLException {
        return delegate.prepareStatement(s);
    }

    public CallableStatement prepareCall(String s) throws SQLException {
        return delegate.prepareCall(s);
    }

    public String nativeSQL(String s) throws SQLException {
        return delegate.nativeSQL(s);
    }

    public void setAutoCommit(boolean b) throws SQLException {
        delegate.setAutoCommit(b);
    }

    public boolean getAutoCommit() throws SQLException {
        return delegate.getAutoCommit();
    }

    public void commit() throws SQLException {
        delegate.commit();
    }

    public void rollback() throws SQLException {
        delegate.rollback();
    }

    public void close() throws SQLException {
        // do nothing
    }

    public boolean isClosed() throws SQLException {
        return delegate.isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return delegate.getMetaData();
    }

    public void setReadOnly(boolean b) throws SQLException {
        delegate.setReadOnly(b);
    }

    public boolean isReadOnly() throws SQLException {
        return delegate.isReadOnly();
    }

    public void setCatalog(String s) throws SQLException {
        delegate.setCatalog(s);
    }

    public String getCatalog() throws SQLException {
        return delegate.getCatalog();
    }

    public void setTransactionIsolation(int i) throws SQLException {
        delegate.setTransactionIsolation(i);
    }

    public int getTransactionIsolation() throws SQLException {
        return delegate.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        return delegate.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        delegate.clearWarnings();
    }

    public Statement createStatement(int i, int i1) throws SQLException {
        return delegate.createStatement(i, i1);
    }

    public PreparedStatement prepareStatement(String s, int i, int i1) throws SQLException {
        return delegate.prepareStatement(s, i, i1);
    }

    public CallableStatement prepareCall(String s, int i, int i1) throws SQLException {
        return delegate.prepareCall(s, i, i1);
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return delegate.getTypeMap();
    }

    public void setTypeMap(Map<String, Class<?>> stringClassMap) throws SQLException {
        delegate.setTypeMap(stringClassMap);
    }

    public void setHoldability(int i) throws SQLException {
        delegate.setHoldability(i);
    }

    public int getHoldability() throws SQLException {
        return delegate.getHoldability();
    }

    public Savepoint setSavepoint() throws SQLException {
        return delegate.setSavepoint();
    }

    public Savepoint setSavepoint(String s) throws SQLException {
        return delegate.setSavepoint(s);
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        delegate.rollback(savepoint);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        delegate.releaseSavepoint(savepoint);
    }

    public Statement createStatement(int i, int i1, int i2) throws SQLException {
        return delegate.createStatement(i, i1, i2);
    }

    public PreparedStatement prepareStatement(String s, int i, int i1, int i2) throws SQLException {
        return delegate.prepareStatement(s, i, i1, i2);
    }

    public CallableStatement prepareCall(String s, int i, int i1, int i2) throws SQLException {
        return delegate.prepareCall(s, i, i1, i2);
    }

    public PreparedStatement prepareStatement(String s, int i) throws SQLException {
        return delegate.prepareStatement(s, i);
    }

    public PreparedStatement prepareStatement(String s, int[] ints) throws SQLException {
        return delegate.prepareStatement(s, ints);
    }

    public PreparedStatement prepareStatement(String s, String[] strings) throws SQLException {
        return delegate.prepareStatement(s, strings);
    }

    public Clob createClob() throws SQLException {
        return delegate.createClob();
    }

    public Blob createBlob() throws SQLException {
        return delegate.createBlob();
    }

    public NClob createNClob() throws SQLException {
        return delegate.createNClob();
    }

    public SQLXML createSQLXML() throws SQLException {
        return delegate.createSQLXML();
    }

    public boolean isValid(int i) throws SQLException {
        return delegate.isValid(i);
    }

    public void setClientInfo(String s, String s1) throws SQLClientInfoException {
        delegate.setClientInfo(s, s1);
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        delegate.setClientInfo(properties);
    }

    public String getClientInfo(String s) throws SQLException {
        return delegate.getClientInfo(s);
    }

    public Properties getClientInfo() throws SQLException {
        return delegate.getClientInfo();
    }

    public Array createArrayOf(String s, Object[] objects) throws SQLException {
        return delegate.createArrayOf(s, objects);
    }

    public Struct createStruct(String s, Object[] objects) throws SQLException {
        return delegate.createStruct(s, objects);
    }

    public <T> T unwrap(Class<T> tClass) throws SQLException {
        return delegate.unwrap(tClass);
    }

    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return delegate.isWrapperFor(aClass);
    }
}
