package easv.dal.connectionManagement;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import easv.exception.ErrorCode;
import easv.exception.RateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool  {


//    private BlockigitngQueue<Connection> connectionPool;
//
//    private SQLServerDataSource ds;
//    /**
//     * this class is responsible of the connections creation,
//     * @param  initialSize the max number off connections available
//     * @param ds the SqlDataSource objet that holds all the info related to the actual database, like user , password, url*/
//    public ConnectionPool(SQLServerDataSource ds, int initialSize) throws  RateException {
//        this.ds= ds;
//        this.connectionPool = new ArrayBlockingQueue<>(initialSize);
//        for (int i = 0; i < initialSize; i++) {
//            this.connectionPool.add(createConnection());
//        }
//    }
//
//    /**create a connection*/
//    private Connection createConnection() throws RateException {
//        try {
//            return ds.getConnection();
//        } catch (SQLException e) {
//            throw new RateException(e.getMessage(),e, ErrorCode.POOL_CONNECTION_FAIL);
//        }
//    }
//
//
//    /**retrieve a connection*/
//    public Connection getConnection() throws RateException {
//        if (connectionPool.isEmpty()) {
//            return createConnection();
//        } else {
//            try {
//                return connectionPool.take();
//            } catch (InterruptedException e) {
//                throw new RateException(e.getMessage(),e, ErrorCode.POOL_CONNECTION_FAIL);
//            }
//        }
//    }
//
//    /**release a connection that is not used anymore*/
//    public void releaseConnection(Connection connection) {
//        connectionPool.add(connection);
//    }
}