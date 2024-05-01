package easv.be;

public class Country {
    private String countryName;
    private int id;

    public Country(String country) {
        this.countryName = country;
    }

    public Country(String country, int id) {
        this.countryName = country;
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return  countryName;

    }
}
