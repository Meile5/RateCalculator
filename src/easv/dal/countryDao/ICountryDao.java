package easv.dal.countryDao;

import easv.be.Country;

import java.util.HashMap;

public interface ICountryDao {
    HashMap<Integer, Country> getCountries();
}
