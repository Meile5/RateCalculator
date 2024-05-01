package easv.be;

public class Team {
    private String team;
    private int id;

    public Team(String team) {
        this.team = team;
    }

    public Team(String team, int id) {
        this.team = team;
        this.id = id;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
