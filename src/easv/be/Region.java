package easv.be;

import java.util.List;

public class Region {
    private String regionName;
    private int id;
    private List<Country> countries;


    public Region(String regionName, int id) {
        this.regionName = regionName;
        this.id = id;
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

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }
}
