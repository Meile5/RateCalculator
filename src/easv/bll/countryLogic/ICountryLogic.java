package easv.bll.countryLogic;

import easv.be.Country;
import easv.exception.RateException;

import java.util.Map;


public interface ICountryLogic {
    Map<Integer, Country> getCountries() throws RateException;

}
