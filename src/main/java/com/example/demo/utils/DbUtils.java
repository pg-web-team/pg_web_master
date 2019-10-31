/**
 * 
 */
package com.example.demo.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang3.StringUtils;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Wang,Jingzhu
 *
 */
final public class DbUtils {

    // private static final String SQL_FILE = "config/sql.properties";

    private static final String CONFIG_FILE = "config/config.properties";

    // private static String DRIVER = "org.postgresql.Driver";
    private static String PROD_URL =
            "jdbc:postgresql://b2b-prd-ga-pgsql-0.postgres.database.chinacloudapi.cn:5432/golden_ambassador?sslmode=require";
    private static String PROD_USER = "readonly@b2b-prd-ga-pgsql-0";
    private static String PROD_PASSWORD = "123456789";


    private static String QA_URL =
            "jdbc:postgresql://b2b-qa-ga-pgsql-0.postgres.database.chinacloudapi.cn:5432/golden_ambassador?sslmode=require";
    private static String QA_USER = "pgadmin@b2b-qa-ga-pgsql-0";
    private static String QA_PASSWORD = "In57Zv0w0n7naxtpvUPc";

    private static String DEV_URL =
            "jdbc:postgresql://b2b-dev-ga-pgsql-0.postgres.database.chinacloudapi.cn:5432/golden_ambassador_test?sslmode=require";
    private static String DEV_USER = "pgadmin@b2b-dev-ga-pgsql-0";
    private static String DEV_PASSWORD = "In57Zv0w0n7naxtpvUPc";


    private static int PAGE_SIZE = 500;

    private static HikariDataSource dataSource;


    private static String URL;
    private static String USER;
    private static String PASSWORD;

    // private static final Map<String, String> SQLS = getSqls();



    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (dataSource != null) {
                    dataSource.close();
                }
            }
        });

    }

    public static void init(Map<String, String> params) {
        String env = params.get("env");
        String pageSize = params.get("pageSize");
        if (StringUtils.equalsIgnoreCase(env, "prod")) {
            URL = PROD_URL;
            USER = PROD_USER;
            PASSWORD = PROD_PASSWORD;
        } else if (StringUtils.equalsIgnoreCase(env, "qa")) {
            URL = QA_URL;
            USER = QA_USER;
            PASSWORD = QA_PASSWORD;
        } else if (StringUtils.equalsIgnoreCase(env, "config")) {
            if (!new File(StringUtils.join(PathUtils.getAppPath(), CONFIG_FILE)).exists()) {
                throw new IllegalArgumentException("config/config.properties not found!!!");
            }
            Map<String, String> configMap =
                    PropUtils.load(StringUtils.join(PathUtils.getAppPath(), CONFIG_FILE));
            // DRIVER = configMap.get("DRIVER");
            URL = configMap.get("URL");
            USER = configMap.get("USER");
            PASSWORD = configMap.get("PASSWORD");
        } else {
            URL = DEV_URL;
            USER = DEV_USER;
            PASSWORD = DEV_PASSWORD;
        }

        PAGE_SIZE = pageSize != null ? TypeUtils.toIntValue(pageSize, 500) : PAGE_SIZE;
        dataSource = new HikariDataSource();
        dataSource.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
        dataSource.setConnectionTimeout(30000L);
        dataSource.setMaximumPoolSize(Math.max(10, Runtime.getRuntime().availableProcessors() + 4));
        dataSource.setIdleTimeout(30000);
        dataSource.setAutoCommit(false);
        dataSource.addDataSourceProperty("user", USER);
        dataSource.addDataSourceProperty("password", PASSWORD);
        dataSource.addDataSourceProperty("url", URL);
    }

    // private static Map<String, String> getSqls() {
    // return PropUtils.load(StringUtils.join(PathUtils.getAppPath(), SQL_FILE));
    // }

    private static ResultSetHandler<Integer> countHandler = new ResultSetHandler<Integer>() {
        public Integer handle(ResultSet rs) throws SQLException {
            if (!rs.next()) {
                return 0;
            }

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            Integer result = 0;

            if (cols > 0) {
                result = rs.getInt(1);
            }
            return result;
        }
    };

    /**
     * query
     * 
     * @param <T>
     * @param sql
     * @param clazz
     * @param params
     * @return
     * @throws SQLException
     */
    // public static <T> List<T> query(String sqlKey, Class<T> clazz, Object... params)
    // throws SQLException {
    // if (sqlKey == null)
    // return Collections.emptyList();
    // sqlKey = StringUtils.upperCase(sqlKey);
    // QueryRunner run = new QueryRunner();
    // ResultSetHandler<List<T>> listHandler = new BeanListHandler<T>(clazz);
    // List<T> result = Collections.emptyList();
    // Connection conn = null; // open a connection
    //
    // String sql = SQLS.get(sqlKey);
    // if (sql == null)
    // return Collections.emptyList();
    // try {
    // conn = getConnection();
    // result = run.query(conn, sql, listHandler, params);
    // // do something with the result
    // } finally {
    // // Use this helper method so we don't have to check for null
    // DbUtils.close(conn);
    // }
    // return result;
    // }

    /**
     * query
     * 
     * @param <T>
     * @param sql
     * @param clazz
     * @param params
     * @return
     * @throws SQLException
     */
    public static <T> List<T> querySql(String sql, Class<T> clazz, Object... params)
            throws SQLException {
        if (sql == null)
            return Collections.emptyList();
        QueryRunner run = new QueryRunner();
        ResultSetHandler<List<T>> listHandler = new BeanListHandler<T>(clazz);
        List<T> result = Collections.emptyList();
        Connection conn = null; // open a connection

        try {
            conn = getConnection();
            result = run.query(conn, sql, listHandler, params);
            // do something with the result
        } finally {
            // Use this helper method so we don't have to check for null
            // DbUtils.close(conn);
        }
        return result;
    }

    public static <T> List<T> querySqlMultithreading(String countSql, String sql, Class<T> clazz,
            Object... params) throws SQLException {
        if (sql == null)
            return Collections.emptyList();

        int count = getCountSql(countSql, params);
        final int pageNum =
                (count % PAGE_SIZE == 0) ? (count / PAGE_SIZE) : (count / PAGE_SIZE + 1);
        List<Callable<List<T>>> taskList = new ArrayList<>();
        CountNumber countNumber = new CountNumber();
        QueryRunner run = new QueryRunner();
        ResultSetHandler<List<T>> listHandler = new BeanListHandler<T>(clazz);
        for (int i = 0; i < pageNum; i++) {
            final int offsetIdx = i;
            taskList.add(() -> {
                List<T> result = Collections.emptyList();
                Connection conn = null; // open a connection
                try {
                    conn = getConnection();
                    String sqlPage = StringUtils.join(sql, " LIMIT ", PAGE_SIZE, " OFFSET ",
                            offsetIdx * PAGE_SIZE);
                    result = run.query(conn, sqlPage, listHandler, params);
                    // do something with the result
                } finally {
                    // DbUtils.close(conn);
                }
                String outStr = (countNumber.nextNumber()) + "/" + pageNum;
                System.out.print(StringUtils.repeat("\b", outStr.length()));
                System.out.print(outStr);
                return result;
            });
        }
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(
                Math.max(6, Runtime.getRuntime().availableProcessors() + 2));

        List<T> resultAll = new ArrayList<>();
        try {
            List<Future<List<T>>> resultList = executor.invokeAll(taskList);
            resultList.forEach(item -> {
                try {
                    resultAll.addAll(item.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
        executor.shutdown();
        return resultAll;
    }

    private static class CountNumber {
        private int number = 0;

        public int nextNumber() {
            return ++number;
        }
    }

    private static Integer getCountSql(String countSql, Object... params) throws SQLException {
        QueryRunner run = new QueryRunner();
        Integer result = 0;
        Connection conn = null; // open a connection
        try {
            conn = getConnection();
            result = run.query(conn, countSql, countHandler, params);
            // do something with the result
        } finally {
            // Use this helper method so we don't have to check for null
            // DbUtils.close(conn);
        }
        return result;
    }

    private static Integer getCountSql(String countSql) throws SQLException {
        QueryRunner run = new QueryRunner();
        Integer result = 0;
        Connection conn = null; // open a connection
        try {
            conn = getConnection();
            result = run.query(conn, countSql, countHandler);
            // do something with the result
        } finally {
            // Use this helper method so we don't have to check for null
            // DbUtils.close(conn);
        }
        return result;
    }

    /**
     * query
     * 
     * @param <T>
     * @param sql
     * @param clazz
     * @return
     * @throws SQLException
     */
    // public static <T> List<T> query(String sqlKey, Class<T> clazz) throws SQLException {
    // if (sqlKey == null)
    // return Collections.emptyList();
    // sqlKey = StringUtils.upperCase(sqlKey);
    // QueryRunner run = new QueryRunner();
    // ResultSetHandler<List<T>> listHandler = new BeanListHandler<T>(clazz);
    // List<T> result = Collections.emptyList();
    // Connection conn = null; // open a connection
    // String sql = SQLS.get(sqlKey);
    // if (sql == null)
    // return Collections.emptyList();
    // try {
    // conn = getConnection();
    // result = run.query(conn, sql, listHandler);
    // // do something with the result
    // } finally {
    // // Use this helper method so we don't have to check for null
    // DbUtils.close(conn);
    // }
    // return result;
    // }

    /**
     * query
     * 
     * @param <T>
     * @param sql
     * @param clazz
     * @return
     * @throws SQLException
     */
    public static <T> List<T> querySql(String sql, Class<T> clazz) throws SQLException {
        if (sql == null)
            return Collections.emptyList();

        QueryRunner run = new QueryRunner();
        ResultSetHandler<List<T>> listHandler = new BeanListHandler<T>(clazz);
        List<T> result = Collections.emptyList();
        Connection conn = null; // open a connection
        try {
            conn = getConnection();
            result = run.query(conn, sql, listHandler);
            // do something with the result
        } finally {
            // Use this helper method so we don't have to check for null
            // DbUtils.close(conn);
        }
        return result;
    }


    public static int insertSql(String sql, Object... params) throws SQLException {
        if (sql == null)
            return 0;
        int result = 0;
        QueryRunner run = new QueryRunner();
        Connection conn = null;
        try {
            conn = getConnection();// open a connection
            // Execute the SQL update statement and return the number of
            // inserts that were made
            result = run.update(conn, sql, params);

        } catch (SQLException sqle) {
            // Handle it
            sqle.printStackTrace();
        } finally {
            if (conn != null) {
                conn.commit();
            }
        }
        return result;
    }

    public static <T> List<T> querySqlMultithreading(String countSql, String sql, Class<T> clazz)
            throws SQLException {
        if (sql == null)
            return Collections.emptyList();

        int count = getCountSql(countSql);
        final int pageNum =
                (count % PAGE_SIZE == 0) ? (count / PAGE_SIZE) : (count / PAGE_SIZE + 1);
        List<Callable<List<T>>> taskList = new ArrayList<>();
        CountNumber countNumber = new CountNumber();
        for (int i = 0; i < pageNum; i++) {
            final int offsetIdx = i;
            taskList.add(() -> {
                QueryRunner run = new QueryRunner();
                ResultSetHandler<List<T>> listHandler = new BeanListHandler<T>(clazz);
                List<T> result = Collections.emptyList();
                Connection conn = null; // open a connection
                try {
                    conn = getConnection();
                    String sqlPage = StringUtils.join(sql, " LIMIT ", PAGE_SIZE, " OFFSET ",
                            offsetIdx * PAGE_SIZE);
                    result = run.query(conn, sqlPage, listHandler);
                    // do something with the result
                } finally {
                    // DbUtils.close(conn);
                }
                String outStr = (countNumber.nextNumber()) + "/" + pageNum;
                System.out.print(StringUtils.repeat("\b", outStr.length()));
                System.out.print(outStr);
                return result;
            });
        }
        List<T> resultAll = new ArrayList<>();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(
                Math.max(6, Runtime.getRuntime().availableProcessors() + 2));

        try {
            List<Future<List<T>>> resultList = executor.invokeAll(taskList);
            resultList.forEach(item -> {
                try {
                    resultAll.addAll(item.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
        executor.shutdown();
        return resultAll;
    }

    // private static void close(Connection conn) {
    // if (conn != null) {
    // try {
    // conn.close();
    // } catch (SQLException e) {
    // }
    // }
    // }

    private static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<Connection>() {

        @Override
        protected Connection initialValue() {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

    };

    private static Connection getConnection() {
        Connection conn = null;
        try {
            conn = connectionThreadLocal.get();
            if (conn == null) {
                conn = dataSource.getConnection();
                connectionThreadLocal.set(conn);
            }
            if (conn.isClosed()) {
                conn = dataSource.getConnection();
                connectionThreadLocal.set(conn);
            }
        } catch (SQLException e) {
            System.out.println("get connection exception!!!");
            e.printStackTrace();
        }
        return conn;
    }

}
