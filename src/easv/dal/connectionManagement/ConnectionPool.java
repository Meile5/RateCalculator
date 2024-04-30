package easv.dal.connectionManagement;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import easv.exception.ErrorCode;
import easv.exception.RateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {
    private BlockingQueue<Connection> connectionPool;

    private SQLServerDataSource ds;

    public ConnectionPool(SQLServerDataSource ds, int initialSize) throws  RateException {
        this.ds= ds;
        this.connectionPool = new ArrayBlockingQueue<>(initialSize);
        for (int i = 0; i < initialSize; i++) {
            this.connectionPool.add(createConnection());
        }
    }

    private Connection createConnection() throws RateException {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new RateException(e.getMessage(),e, ErrorCode.POOL_CONNECTION_FAIL);
        }
    }

    public Connection getConnection() throws RateException {
        if (connectionPool.isEmpty()) {
            return createConnection();
        } else {
            try {
                return connectionPool.take();
            } catch (InterruptedException e) {
                throw new RateException(e.getMessage(),e, ErrorCode.POOL_CONNECTION_FAIL);
            }
        }
    }

    public void releaseConnection(Connection connection) {
        connectionPool.add(connection);
    }
}