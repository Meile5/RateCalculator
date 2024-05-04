package easv.be;

import java.util.Objects;

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


    /**to not be used in equal comparison is used for the view only*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return  Objects.equals(countryName, country.countryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryName, id);
    }
}
