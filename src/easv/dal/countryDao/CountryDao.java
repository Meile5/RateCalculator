package easv.dal.countryDao;

import easv.be.Country;
import easv.be.Team;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.exception.ErrorCode;
import easv.exception.RateException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountryDao implements ICountryDao {
    private IConnection connectionManager;

    public CountryDao() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }

    @Override
    public Map<String, Country> getCountries() throws RateException {
        String sql = "SELECT  * FROM Countries";
        Map<String, Country> countries = new HashMap<>();
        try(Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet rs = psmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("CountryId");
                    String name = rs.getString("CountryName");
                    Country country = new Country(name, id);
                    countries.put(country.getCountryName(), country);
                }
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
        return countries;
    }

    @Override
    public Integer addCountry(Country country, List<Team> teams, List<Team> newTeams) throws RateException {
        Integer countryID = null;
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "INSERT INTO Countries (CountryName) VALUES (?)";
            try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psmt.setString(1, country.getCountryName());
                psmt.executeUpdate();
                try (ResultSet res = psmt.getGeneratedKeys()) {
                    if (res.next()) {
                        countryID = res.getInt(1);
                    } else {
                        throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                    }
                }
                if(!newTeams.isEmpty()){
                    List<Integer> teamsIds = addTeams(newTeams, conn);
                    addNewTeamsToCountry(teamsIds, countryID, conn);
                }

                if(!teams.isEmpty()){
                    addTeamToCountry(countryID, teams, conn);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
            }
        }
        return countryID;
    }

    private void addNewTeamsToCountry(List<Integer> newTeamsIds, Integer countryID, Connection conn) throws RateException {
        String sql = "INSERT INTO CountryTeam (CountryID, TeamID) VALUES (?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Integer teamID : newTeamsIds) {
                psmt.setInt(1, countryID);
                psmt.setInt(2, teamID);
                psmt.executeUpdate();
            }
            psmt.executeBatch();
        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        }
    }

    @Override
    public void addTeamToCountry(Integer countryID, List<Team> teams, Connection conn) throws SQLException {
        String sql = "INSERT INTO CountryTeam (CountryID, TeamID) VALUES (?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Team team : teams) {
                psmt.setInt(1, countryID);
                psmt.setInt(2, team.getId());
                psmt.executeUpdate();
            }
            psmt.executeBatch();
        }
    }

    @Override
    public List<Integer> addTeams(List<Team> teams, Connection conn) throws RateException, SQLException {
        List<Integer> teamIds = new ArrayList<>();
        String sql = "INSERT INTO Teams (TeamName) VALUES (?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (Team team : teams) {
                psmt.setString(1, team.getTeamName());
                psmt.executeUpdate();
                try (ResultSet res = psmt.getGeneratedKeys()) {
                    if (res.next()) {
                        teamIds.add(res.getInt(1));
                    } else {
                        throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                    }
                }
            }
            psmt.executeBatch();
        }
        return teamIds;
    }

    @Override
    public void updateCountry(Country country, List<Team> teamsToAdd, List<Team> teamsToRemove, List<Team> newTeams) throws RateException {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "UPDATE Countries SET CountryName = ? WHERE CountryID = ?";
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setString(1, country.getCountryName());
                psmt.setInt(2, country.getId());
                psmt.executeUpdate();

                if(!newTeams.isEmpty()){
                    List<Integer> teamsIds = addTeams(newTeams, conn);
                    addNewTeamsToCountry(teamsIds, country.getId(), conn);
                }

                if(!teamsToAdd.isEmpty()){
                    addTeamToCountry(country.getId(), teamsToAdd, conn);
                }

                if(!teamsToRemove.isEmpty()){
                    removeTeamFromCountry(country.getId(), teamsToRemove, conn);
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
            }
        }
    }

    private void removeTeamFromCountry(int countryID, List<Team> removedTeams, Connection conn) {
        String sql = "DELETE FROM CountryTeam WHERE CountryID = ? AND TeamID = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Team team : removedTeams) {
                psmt.setInt(1, countryID);
                psmt.setInt(2, team.getId());
                psmt.executeUpdate();
            }
            psmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteCountry(Country country) throws RateException {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "DELETE FROM Countries WHERE CountryID = ?";
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setInt(1, country.getId());
                psmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
            }
        }
        return true;
    }

}

