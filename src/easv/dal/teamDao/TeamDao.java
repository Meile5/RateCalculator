package easv.dal.teamDao;
import easv.be.*;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.exception.ErrorCode;
import easv.exception.RateException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TeamDao implements ITeamDao {
    private IConnection connectionManager;
    public TeamDao() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }


    public Map<Integer, Team> getTeams() throws RateException {
        String sql = "SELECT  * FROM Teams";
        Map<Integer, Team> teams = new HashMap<>();
        try(Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet rs = psmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("TeamId");
                    String name = rs.getString("TeamName");
                    Team team = new Team(name, id);
                    teams.put(team.getId(), team);
                }
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
        return teams;
    }



}
