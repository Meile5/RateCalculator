package easv.dal.countryDao;

import easv.be.Country;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.util.HashMap;

public interface ICountryDao {
    ObservableMap<Integer, Country> getCountries() throws RateException;
}
