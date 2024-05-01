package easv.dal.connectionManagement;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import easv.dal.FileHandler;
import easv.exception.RateException;
import java.sql.Connection;
import java.sql.DriverManager;

public class MSSQLConnection implements IConnection {
    private final int  CONN_POOL_SIZE  = 2;
    private final long establishConnectionTime = 5000;
    private final SQLServerDataSource ds;
    private String user;
    private String password;
   // private ConnectionPool connectionPool;

    /**Manages the Microsoft SQL server connection, by encapsulating an connectionPool ,
     *that will create new connections or retrieve an existing one from the pool */
    public MSSQLConnection() throws RateException {
        ds = new SQLServerDataSource();
        this.getCredentials();
        ds.setDatabaseName("CSe2023b_e_20_Code_Crafters");
        ds.setUser(user);
        ds.setPassword(password);
        ds.setServerName("EASV-DB4");
        ds.setTrustServerCertificate(true);
      //  this.connectionPool = new ConnectionPool(ds, CONN_POOL_SIZE);

    }

   // @Override
   // public void releaseConnection(Connection connection) {
  // connectionPool.releaseConnection(connection);
   // }

    public Connection getConnection() {
        Connection conn = null;
        long time = System.currentTimeMillis();
        DriverManager.setLoginTimeout(2);
        while (conn == null && System.currentTimeMillis() < time + establishConnectionTime) {
            try {
                return ds.getConnection();
            } catch (SQLServerException e) {
                if (System.currentTimeMillis() >= time + establishConnectionTime) {
                    throw new RuntimeException();
                }
            }
        }
        if (conn == null) {
            throw new RuntimeException();
        }
        return conn;
    }

    private void getCredentials() {
        String[] credentials = new FileHandler().readDbLogin();
        this.user = credentials[1].trim();
        this.password = credentials[2].trim();
    }

}
