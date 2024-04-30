package easv.be;

public class Country {
    private String country;
    private int id;

    public Country(String country) {
        this.country = country;
    }

    public Country(String country, int id) {
        this.country = country;
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
