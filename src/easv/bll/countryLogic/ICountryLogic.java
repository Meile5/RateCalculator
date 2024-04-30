package easv.bll.countryLogic;

import easv.be.Country;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.util.HashMap;

public interface ICountryLogic {
    ObservableMap<Integer, Country> getCountries() throws RateException;

}
