package com.sqldalmaker;

import com.sqldalmaker.todolist.model.dao.GroupsDao;
import com.sqldalmaker.todolist.model.dao.TasksDao;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/*
    SQL DAL Maker Web-Site: http://sqldalmaker.sourceforge.net

    This is an example of how to implement DataStore in Java + JDBC.
    Copy-paste this code to your project and change it for your needs.

    Improvements are welcome: sqldalmaker@gmail.com
 */
public class DataStoreManager {

    public GroupsDao createGroupsDao() {
        return new GroupsDao(ds);
    }

    public TasksDao createTasksDao() {
        return new TasksDao(ds);
    }

    public static class OutParam<T> {

        private final int sqlType;
        private final Class<T> javaType;
        private T value = null;

        public OutParam(int sqlType, Class<T> javaType) {
            this.sqlType = sqlType;
            this.javaType = javaType;
        }

        public OutParam(int sqlType, Class<T> javaType, T value) {
            this.sqlType = sqlType;
            this.javaType = javaType;
            this.value = value;
        }

        public int getSqlType() {
            return this.sqlType;
        }

        public Class<T> getJavaType() {
            return this.javaType;
        }

        public T getValue() {
            return this.value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

    /*
        Helper method to simplify construct:

            OutParam<?> out_param = dsm.create_out_param(OracleTypes.NUMBER, BigDecimal.class);
     */
    public <T> OutParam<T> create_out_param(int sqlType, Class<T> javaType) {
        return new OutParam<T>(sqlType, javaType);
    }

    /*
        Helper method to simplify construct:

            OutParam<?> inout_param = dsm.create_inout_param(OracleTypes.NUMBER, new BigDecimal(10));
     */
    @SuppressWarnings("unchecked")
    public <T> OutParam<T> create_inout_param(int sqlType, T value) {
        return new OutParam<T>(sqlType, (Class<T>) value.getClass(), value);
    }

    /*
        Something like this is also OK:

            public static class OutNumber extends OutParam<BigDecimal> {

                public OutNumber() { // OUT
                    super(OracleTypes.NUMBER, BigDecimal.class);
                }

                public OutNumber(BigDecimal value) { // INOUT
                    super(OracleTypes.NUMBER, BigDecimal.class, value);
                }
            }
     */

    private Connection conn;

    private boolean is_oracle;

    public void setDataSource(DataSource dataSource) throws SQLException {
        /*

        For JSF-apps, setDataSource(...) may be used in this way:
        =============================================================

        src/main/resources/applicationContext.xml
        -----------------------------------------

        <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource" scope="singleton">
            <property name="driverClassName" value="org.h2.Driver" />
            <property name="url" value="jdbc:h2:#{servletContext.getRealPath('')}/todo_list" />
            <property name="username" value="" />
            <property name="password" value="" />
        </bean>

        <bean id="datastoreManager" class="com.sqldalmaker.DataStoreManager" scope="session">
            <property name="dataSource" ref="dataSource"/>
        </bean>

        <bean id="todoListService" class="com.sqldalmaker.todolist.service.TodoListService" scope="session">
            <property name="datastoreManager" ref="datastoreManager"/>
        </bean>

        <bean id="todoListBean" class="com.sqldalmaker.todolist.beans.TodoListBean" scope="session">
            <property name="service" ref="todoListService"/>
        </bean>

         */
        conn = dataSource.getConnection();
        boolean is_autocommit = conn.getAutoCommit();
        System.out.println("AutoCommit is " + is_autocommit);
        conn.setAutoCommit(false); // set it true if you don't need transactions
        DatabaseMetaData dmd = conn.getMetaData();
        String url = dmd.getURL();
        is_oracle = url.contains("oracle");
    }

    public void begin() throws Exception {
        conn.setAutoCommit(false);
    }

    public void commit() throws Exception {
        conn.setAutoCommit(true);
        // conn.commit();
    }

    public void rollback() throws Exception {
        conn.rollback();
    }

    /*
     * Method open() may be used instead of setDataSource (for desktop apps):
     */
    public void open() throws Exception {
        // To use OracleTypes.CURSOR, uncomment appropriate code in _prepare_call_params(...) below
        conn = DriverManager.getConnection("jdbc:sqlite:./log.sqlite", "", "");
        // conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "ORDERS", "root");
        // conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/test", "postgres", "sa");
        // conn = DriverManager.getConnection("jdbc:mysql://localhost/sakila", "root", "root");
        // conn = DriverManager.getConnection("jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=AdventureWorks2014", "sa", "root");
        // conn = DriverManager.getConnection("jdbc:h2:todo_list", "", "");
        // conn = DriverManager.getConnection("jdbc:sqlite:thesaurus.sqlite", "", "");
        DatabaseMetaData dmd = conn.getMetaData();
        String url = dmd.getURL();
        is_oracle = url.contains("oracle");
        conn.setAutoCommit(false);
    }

    /*
     * Remove close() for 'live' connections:
     */
    public void close() throws Exception {
        conn.setAutoCommit(true);
        conn.close();
    }

    private final MyDataStore ds = new MyDataStore();

    private interface RsHandler {
        void handle(final ResultSet rs) throws SQLException;
    }

    private class MyDataStore extends DataStore {

        private boolean is_string_value(Class<?> inValueType) {
            // Consider any CharSequence (including StringBuffer and
            // StringBuilder) as a String.
            return (CharSequence.class.isAssignableFrom(inValueType)
                    || StringWriter.class.isAssignableFrom(inValueType));
        }

        private boolean is_date_value(Class<?> inValueType) {
            return (java.util.Date.class.isAssignableFrom(inValueType)
                    && !(java.sql.Date.class.isAssignableFrom(inValueType)
                    || java.sql.Time.class.isAssignableFrom(inValueType)
                    || java.sql.Timestamp.class.isAssignableFrom(inValueType)));
        }

        protected void prepare_params(Object... params) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] != null) {
                    if (is_string_value(params[i].getClass())) {
                        params[i] = params[i].toString();
                    } else if (is_date_value(params[i].getClass())) {
                        params[i] = new java.sql.Timestamp(((java.util.Date) params[i]).getTime());
                    }
                }
            }
        }

        private boolean is_jdbc_stored_proc_call(String jdbc_sql) {
            jdbc_sql = jdbc_sql.trim();
            String[] parts1 = jdbc_sql.split("\\s+");
            if (parts1.length > 0 && parts1[0].trim().equalsIgnoreCase("begin")) {
                return true; // Oracle PL/SQL
            }
            // SQL Server requires {...} syntax for CALL
            if (jdbc_sql.startsWith("{") & jdbc_sql.endsWith("}")) {
                jdbc_sql = jdbc_sql.substring(1, jdbc_sql.length() - 1);
            }
            String[] parts = jdbc_sql.split("\\s+");
            if (parts.length < 2) {
                return false;
            }
            String call = parts[0];
            return call.compareToIgnoreCase("call") == 0;
        }

        private void _do_after_insert(PreparedStatement stmt, Object[] genValues) throws SQLException {
            ResultSet rs_keys = stmt.getGeneratedKeys(); // ok even for oracle (why?)
            if (rs_keys != null) {
                try {
                    int i = 0;
                    while (rs_keys.next()) {
                        // Uncomment the line that is OK with your JDBC driver
                        // keys.getBigDecimal(1) works with most of tested drivers (except SQLite)
                        Object obj = rs_keys.getBigDecimal(1);
                        // keys.getObject(1) works with all tested drivers,
                        // but it can return Long for Integer column (MySQL)
                        genValues[i] = obj;
                        i++;
                    }
                } finally {
                    //if (rs_keys != null) {
                    rs_keys.close();
                    //}
                }
            }
        }

        @Override
        public <T> T castGeneratedValue(Class<T> type, Object obj) {
            // You can improve/simplify this method if it is possible with your JDBC driver
            // For many drivers (SQL Server 2008, Derby), keys.getObject(1)
            // returns BigDecimal independently of the type of column
            if (obj instanceof BigDecimal) {
                BigDecimal bigDecimal = (BigDecimal) obj;
                if (Byte.class.equals(type)) {
                    obj = bigDecimal.byteValueExact();
                } else if (Float.class.equals(type)) {
                    // there is no 'exact' version
                    obj = bigDecimal.floatValue();
                } else if (Double.class.equals(type)) {
                    // there is no 'exact' version
                    obj = bigDecimal.doubleValue();
                } else if (Integer.class.equals(type)) {
                    obj = bigDecimal.intValueExact();
                } else if (Long.class.equals(type)) {
                    obj = bigDecimal.longValueExact();
                } else if (BigInteger.class.equals(type)) {
                    obj = bigDecimal.toBigIntegerExact();
                } else if (!BigDecimal.class.equals(type)) {
                    throw new ClassCastException("Unexpected class '" + type.getName() + "'");
                }
            }
            // cast:
            // Throws:
            // ClassCastException - if the object is not null and is not assignable to the
            // type T.
            return type.cast(obj);
        }

        @Override
        public int insert(String sql, String[] genColNames, Object[] genValues, Object... params) throws SQLException {
            prepare_params(params);
            PreparedStatement stmt = null;
            int rows;
            try {
                stmt = conn.prepareStatement(sql, genColNames);
                this._fill_statement(stmt, params);
                rows = stmt.executeUpdate();
                _do_after_insert(stmt, genValues);
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
            return rows;
        }

        @Override
        public int execDML(String sql, Object... params) throws Exception {
            prepare_params(params);
            boolean sp = is_jdbc_stored_proc_call(sql);
            if (sp) {
                return _execute_call(conn, sql, params);
            } else {
                return _update(conn, sql, params);
            }
        }

        @Override
        public <T> T query(final Class<T> type, String sql, Object... params) throws Exception {
            List<T> list = queryList(type, sql, params);
            if (list.size() == 0) {
                throw new SQLException("No rows: " + sql); // return null; as alternative
            }
            if (list.size() > 1) {
                throw new SQLException("More than 1 row available: " + sql);
            }
            return list.get(0);
        }

        @Override
        public <T> List<T> queryList(final Class<T> type, String sql, Object... params) throws Exception {
            final List<T> res = new ArrayList<T>();
            if (type == Void.class) {
                RsHandler rsh = new RsHandler() {
                    @Override
                    public void handle(final ResultSet rs) {
                    }
                };
                _query(sql, rsh, params);
                res.add(null); // to prevent exception while '_query' instead of '_query-list'
                return res;
            }
            RsHandler rsh = new RsHandler() {
                @Override
                public void handle(final ResultSet rs) throws SQLException {
                    if (!rs.next()) {
                        return;
                    }
                    Object value = rs.getObject(1);
                    // PostgreSQL: Object value is ResultSet for SQL statements like 'select * from my_func(?, ?)'
                    if (value instanceof ResultSet) {
                        while (true) {
                            final ResultSet rs_value = (ResultSet) value;
                            try {
                                while (rs_value.next()) {
                                    T t = type.cast(rs_value.getObject(1));
                                    res.add(t);
                                }
                            } finally {
                                // if (rs_value != null) {
                                rs_value.close();
                                //}
                            }
                            if (rs.next()) {
                                value = rs.getObject(1);
                                if (!(value instanceof ResultSet)) {
                                    throw new SQLException("ResultSet expected");
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        T t = type.cast(rs.getObject(1)); // 1st row is already fetched by rs.next()
                        res.add(t);
                        while (rs.next()) {
                            t = type.cast(rs.getObject(1)); // the first column is 1
                            res.add(t);
                        }
                    }
                }
            };
            _query(sql, rsh, params);
            return res;
        }

        @Override
        public RowData queryRow(String sql, Object... params) throws Exception {
            final List<RowData> rows = new ArrayList<RowData>();
            queryAllRows(sql, new RowHandler() {
                @Override
                public void handleRow(RowData rd) /*throws Exception*/ {
                    rows.add(rd);
                }
            }, params);
            if (rows.size() == 0) {
                throw new Exception("No rows");
            }
            if (rows.size() > 1) {
                throw new Exception("'More than 1 row exists");
            }
            return rows.get(0);
        }

        @Override
        public void queryAllRows(String sql, final RowHandler row_handler, Object... params) throws Exception {
            RsHandler rsh = new RsHandler() {
                @Override
                public void handle(final ResultSet rs) throws SQLException {
                    if (!rs.next()) {
                        return;
                    }
                    Object value = rs.getObject(1);
                    // PostgreSQL: Object value is ResultSet for SQL statements like 'select * from my_func(?, ?)'
                    if (value instanceof ResultSet) {
                        while (true) {
                            final ResultSet rs_value = (ResultSet) value;
                            RowData rowData = new RsRowData(rs_value);
                            try {
                                while (rs_value.next()) {
                                    _process_row(rowData, row_handler);
                                }
                            } finally {
                                // if (rs_value != null) {
                                rs_value.close();
                                // }
                            }
                            if (rs.next()) {
                                value = rs.getObject(1);
                                if (!(value instanceof ResultSet)) {
                                    throw new SQLException("ResultSet expected");
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        RowData row_data = new RsRowData(rs);
                        _process_row(row_data, row_handler); // 1st row is already fetched by rs.next()
                        while (rs.next()) { // fetch the rest
                            _process_row(row_data, row_handler);
                        }
                    }
                }
            };
            _query(sql, rsh, params);
        }

        private int _update(Connection conn, String sql, Object... params) throws SQLException {
            if (conn == null) {
                throw new SQLException("Null connection");
            }
            if (sql == null) {
                throw new SQLException("Null SQL statement");
            }
            PreparedStatement stmt = null;
            int rows;
            try {
                stmt = conn.prepareStatement(sql);
                this._fill_statement(stmt, params);
                rows = stmt.executeUpdate();
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
            return rows;
        }

        private <T> void _process_row(RowData row_data, RowHandler row_handler) throws SQLException {
            try {
                row_handler.handleRow(row_data);
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }

        private <T> void _query(String sql, RsHandler rsh, Object... params) throws SQLException {
            prepare_params(params);
            boolean sp = is_jdbc_stored_proc_call(sql);
            if (sp) {
                _query_call(conn, sql, rsh, params);
            } else {
                _query(conn, sql, rsh, params);
            }
        }

        private <T> void _query(Connection conn, String sql, RsHandler rsh, Object... params)
                throws SQLException {
            if (conn == null) {
                throw new SQLException("Null connection");
            }
            if (sql == null) {
                throw new SQLException("Null SQL statement");
            }
            if (rsh == null) {
                throw new SQLException("Null ResultSetHandler");
            }
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.prepareStatement(sql);
                this._fill_statement(stmt, params);
                rs = stmt.executeQuery();
                rsh.handle(rs);
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } finally {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
            }
        }

        private void _register(CallableStatement stmt, int index, int sqlType, Object value) throws SQLException {
            stmt.registerOutParameter(index, sqlType);
            if (value != null) {
                stmt.setObject(index, value);
            }
        }

        private void _fill_statement(PreparedStatement stmt, Object... params)
                throws SQLException {
            // nothing to do here
            if (params == null) {
                return;
            }
            CallableStatement call = null;
            if (stmt instanceof CallableStatement) {
                call = (CallableStatement) stmt;
            }
            for (int i = 0; i < params.length; i++) {
                if (params[i] != null) {
                    if (call != null && params[i] instanceof OutParam) {
                        OutParam<?> p = ((OutParam<?>) params[i]);
                        _register(call, i + 1, p.getSqlType(), p.getValue());
                    } else {
                        stmt.setObject(i + 1, params[i]);
                    }
                } else {
                    // VARCHAR works with many drivers regardless
                    // of the actual column type. Oddly, NULL and
                    // OTHER don't work with Oracle's drivers.
                    int sqlType = Types.VARCHAR;
                    stmt.setNull(i + 1, sqlType);
                }
            }
        }

        private <T> void _add_out_param(OutParam<T> cp, List<Object> call_params) {
            /* wrap into method to suppress warnings regarding Java generics */
            call_params.add(cp);
        }

        private boolean _prepare_call_params(boolean allow_cursor_params, Object[] params, List<Object> call_params) throws SQLException {
            boolean query_out_cursors = false;
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof RowHandler) {
                    if (allow_cursor_params) {
                        // uncomment/comment lines below if you are not on Oracle:
                        throw new SQLException("RowHandler params are allowed only for Oracle SYS_REFCURSOR-s");
                        //call_params.add(new OutParam<Object>(OracleTypes.CURSOR, Object.class));
                        //query_out_cursors = true;
                    } else {
                        throw new SQLException(RowHandler.class.getName() + " are not allowed here");
                    }
                } else if (params[i] instanceof OutParam) {
                    OutParam<?> cp = (OutParam<?>) params[i];
                    _add_out_param(cp, call_params);
                } else if (!(params[i] instanceof RowHandler[])) { // for implicit cursor do nothing in here
                    call_params.add(params[i]);
                }
            }
            return query_out_cursors;
        }

        private <T> void _query_call(Connection conn, String sql, RsHandler rsh, Object... params) throws SQLException {
            CallableStatement stmt = conn.prepareCall(sql);
            try {
                List<Object> call_params = new ArrayList<Object>();
                _prepare_call_params(false, params, call_params);
                this._fill_statement(stmt, call_params.toArray());
                if (is_oracle) {
                    // ........................................
                    // the code to work with Oracle implicit SYS_REFCURSOR-s
                    // https://en.it1352.com/article/8242caae4610471d962d4b45c13ae070.html
                    // ........................................
                    stmt.execute(); // it returns false for Oracle SP with implicit ref-cursors
                    while (stmt.getMoreResults()) {
                        ResultSet rs = stmt.getResultSet();
                        try {
                            rsh.handle(rs);
                        } finally {
                            if (rs != null) {
                                rs.close();
                            }
                        }
                    }
                } else {
                    // ........................................
                    // the code to work with MySQL SP returning ResultSet-s
                    // ........................................
                    ResultSet rs = stmt.executeQuery();
                    while (true) {
                        try {
                            rsh.handle(rs);
                        } finally {
                            if (rs != null) {
                                rs.close();
                            }
                        }
                        if (stmt.getMoreResults()) {
                            rs = stmt.getResultSet();
                        } else {
                            break;
                        }
                    }
                }
                // ........................................
                _read_out_params(stmt, params);
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        }

        private class RsRowData implements RowData {

            final ResultSet rs;

            RsRowData(ResultSet rs) {
                this.rs = rs;
            }

            @Override
            public <T> T getValue(Class<T> type, String columnLabel) throws Exception {
                return type.cast(rs.getObject(columnLabel));
            }

            @Override
            public Short getShort(String columnLabel) throws Exception {
                short res = rs.getShort(columnLabel);
                if (rs.wasNull()) {
                    return null;
                }
                return res;
            }

            @Override
            public Integer getInteger(String columnLabel) throws Exception {
                int res = rs.getInt(columnLabel);
                if (rs.wasNull()) {
                    return null;
                }
                return res;
            }

            @Override
            public Long getLong (String columnLabel) throws Exception {
                long res = rs.getLong(columnLabel);
                if (rs.wasNull()) {
                    return null;
                }
                return res;
            }

            @Override
            public Float getFloat(String columnLabel) throws Exception {
                float res = rs.getFloat(columnLabel);
                if (rs.wasNull()) {
                    return null;
                }
                return res;
            }

            @Override
            public Double getDouble(String columnLabel) throws Exception { // Android
                double res = rs.getDouble(columnLabel);
                if (rs.wasNull()) {
                    return null;
                }
                return res;
            }

            @Override
            public BigDecimal getBigDecimal(String columnLabel) throws Exception {
                BigDecimal res = rs.getBigDecimal(columnLabel);
                if (rs.wasNull()) {
                    return null;
                }
                return res;
            }

            @Override
            public String getString (String columnLabel) throws Exception {
                String res = rs.getString(columnLabel);
                if (rs.wasNull()) {
                    return null;
                }
                return res;
            }

            @Override
            public java.util.Date getDate(String columnLabel) throws Exception {
                Date res = rs.getDate(columnLabel);
                if (rs.wasNull()) {
                    return null;
                }
                return res;
            }

            @Override
            public byte[] getBytes(String columnLabel) throws Exception { // Android
                byte[] res = rs.getBytes(columnLabel);
                if (rs.wasNull()) {
                    return null;
                }
                return res;
            }

            @Override
            public Boolean getBoolean(String columnLabel) throws Exception {
                boolean res = rs.getBoolean(columnLabel);
                if (rs.wasNull()) {
                    return null;
                }
                return res;
            }
        }

        public int _execute_call(Connection conn, String sql, final Object... params) throws Exception {
            final CallableStatement stmt = conn.prepareCall(sql);
            try {
                List<Object> call_params = new ArrayList<Object>();
                boolean query_out_cursors = _prepare_call_params(true, params, call_params);
                this._fill_statement(stmt, call_params.toArray());
                if (query_out_cursors) {
                    // Using Ref Cursors To Return Record-sets
                    // https://oracle-base.com/articles/misc/using-ref-cursors-to-return-recordsets
                    boolean is_rs = stmt.execute();
                    if (is_rs) {
                        throw new SQLException("Invalid usage of Out Ref-Cursors. First result of stmt.execute() is a ResultSet object.");
                    }
                    for (int i = 0; i < params.length; i++) {
                        if (params[i] instanceof RowHandler) {
                            final RowHandler rh = (RowHandler) params[i];
                            final ResultSet rs_cursor = (ResultSet) stmt.getObject(i + 1);
                            final RowData row_data = new RsRowData(rs_cursor);
                            try {
                                while (rs_cursor.next()) {
                                    try {
                                        rh.handleRow(row_data);
                                    } catch (Exception e) {
                                        throw new SQLException(e);
                                    }
                                }
                            } finally {
                                if (rs_cursor != null) {
                                    rs_cursor.close();
                                }
                            }
                        } else if (params[i] instanceof OutParam) {
                            _read_out_param(stmt, i, params);
                        }
                    }
                } else { // if (query_out_cursors) {
                    boolean implicit_result_sets = false;
                    for (int i = 0; i < params.length; i++) {
                        if (params[i] instanceof RowHandler[]) {
                            implicit_result_sets = true;
                            break;
                        }
                    }
                    if (implicit_result_sets) {
                        class RC_Handler {
                            // make it class field to allow both changing and access from RowData
                            private ResultSet rs_implicit;

                            private void fetch(RowData row_data, RowHandler rh) throws SQLException {
                                try {
                                    while (rs_implicit.next()) {
                                        try {
                                            rh.handleRow(row_data);
                                        } catch (Exception e) {
                                            throw new SQLException(e);
                                        }
                                    }
                                } finally {
                                    if (rs_implicit != null) {
                                        rs_implicit.close();
                                    }
                                }
                            }

                            public void fetch_all() throws SQLException {
                                final RowData row_data = new RsRowData(rs_implicit);
                                //
                                // Oracle wants stmt.execute() --> getMoreResults() --> getResultSet()
                                // MySQL wants executeQuery() first
                                //
                                if (is_oracle) {
                                    stmt.execute(); // it returns false for Oracle SP with implicit ref-cursors
                                    for (int i = 0; i < params.length; i++) {
                                        if (params[i] instanceof RowHandler[]) {
                                            RowHandler[] rh_arr = (RowHandler[]) params[i];
                                            int rh_index = 0;
                                            while (stmt.getMoreResults()) { // move through result-sets
                                                rs_implicit = stmt.getResultSet();
                                                fetch(row_data, rh_arr[rh_index]);
                                                rh_index++;
                                            }
                                        }
                                    }
                                } else {
                                    rs_implicit = stmt.executeQuery();
                                    for (int i = 0; i < params.length; i++) {
                                        if (params[i] instanceof RowHandler[]) {
                                            RowHandler[] rh_arr = (RowHandler[]) params[i];
                                            int rh_index = 0;
                                            while (true) { // move through result-sets
                                                fetch(row_data, rh_arr[rh_index]);
                                                rh_index++;
                                                if (stmt.getMoreResults()) {
                                                    rs_implicit = stmt.getResultSet();
                                                } else {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        RC_Handler rch = new RC_Handler();
                        rch.fetch_all();
                    } else {
                        stmt.execute(); // it returns false for Oracle SP with implicit ref-cursors
                    }
                    _read_out_params(stmt, params); // for both implicit RC call and ordinary call
                }
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
            return 0;
        }

        private void _read_out_params(CallableStatement stmt, Object... params) throws SQLException {
            for (int i = 0; i < params.length; i++) {
                _read_out_param(stmt, i, params);
            }
        }

        private <T> void _assign_out_param(OutParam<T> op, Object out_value) {
            /* wrap into method to suppress warnings regarding Java generics */
            op.setValue(op.getJavaType().cast(out_value));
        }

        private void _read_out_param(CallableStatement stmt, int zero_index, Object... params) throws SQLException {
            if (params[zero_index] instanceof OutParam) {
                Object out_value = stmt.getObject(zero_index + 1);
                OutParam<?> op = (OutParam<?>) params[zero_index];
                _assign_out_param(op, out_value);
            }
        }
    }
}
