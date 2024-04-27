package easv.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {

    private final long establishConnectionTime = 5000;
    private final SQLServerDataSource ds;
    private String user;
    private String password;

    public ConnectionManager() {
        ds = new SQLServerDataSource();
        this.getCredentials();
        ds.setDatabaseName("CSe2023b_e_20_Code_Crafters");
        ds.setUser(user);
        ds.setPassword(password);
        ds.setServerName("EASV-DB4");
        ds.setTrustServerCertificate(true);
    }

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
