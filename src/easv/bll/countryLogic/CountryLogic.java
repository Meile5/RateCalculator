package easv.bll.countryLogic;

import easv.be.Country;
import easv.dal.countryDao.CountryDao;
import easv.dal.countryDao.ICountryDao;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Map;

public class CountryLogic  implements ICountryLogic{

    private ICountryDao countryDao;


    public CountryLogic() throws RateException {
        this.countryDao = new CountryDao();
    }

    @Override
    public Map<String, Country> getCountries() throws RateException {
        return countryDao.getCountries();
    }
}
