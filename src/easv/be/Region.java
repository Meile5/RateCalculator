package easv.be;

import java.util.List;
import java.util.Set;

public class Region {
    private String regionName;
    private int id;
//    private List<Country> countries;

    private Set<Country> uniqueCountries;


    public Region(String regionName, int id) {
        this.regionName = regionName;
        this.id = id;
    }
    public Region(String regionName, int id,Set<Country> countries) {
   this(regionName,id);
   this.uniqueCountries=countries;
    }


    public void addCountryToRegion(Country country){
        this.uniqueCountries.add(country);
    }

    public void removeCountriesFromRegion(Country country){
        this.uniqueCountries.remove(country);
    }


    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public List<Country> getCountries() {
//        return countries;
//    }
//
//    public void setCountries(List<Country> countries) {
//        this.countries = countries;
//    }
}
