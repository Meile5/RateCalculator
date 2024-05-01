package easv.dal.countryDao;

import easv.be.Country;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.util.Map;

public interface ICountryDao {
    Map<Integer, Country> getCountries() throws RateException;
}
