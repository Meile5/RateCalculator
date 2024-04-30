package easv.dal.countryDao;

import easv.dal.connectionManagement.ConnectionManager;
import easv.exception.RateException;

public class CountryDao {
    private ConnectionManager connectionManager ;

    public CountryDao() throws RateException {
        this.connectionManager = new ConnectionManager();
    }
}
