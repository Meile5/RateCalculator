package easv.bll.TeamLogic;


import easv.be.*;
import easv.bll.EmployeesLogic.IRateCalculator;
import easv.bll.EmployeesLogic.RateCalculator;
import easv.dal.teamDao.ITeamDao;
import easv.dal.teamDao.TeamDao;
import easv.exception.RateException;


import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

//TODO do not delete this class   Andrei found a usage for
public class TeamLogic implements ITeamLogic {
    private final ITeamDao teamDao;
    private final IRateCalculator rateCalculator;

    public TeamLogic() throws RateException {
        this.teamDao = new TeamDao();
        this.rateCalculator = new RateCalculator();
    }


    /**
     * compute the overhead for a team*
     *
     * @param team the team to calculate for
     *             returns a List that contains salaryOverhead,totalOverhead,productiveOverhead
     */
    private Map<TeamWithEmployees.TeamOverheadType, BigDecimal> calculateTeamOverhead(TeamWithEmployees team) {
        Map<TeamWithEmployees.TeamOverheadType, BigDecimal> teamOverhead = new HashMap<>();
        BigDecimal salaryOverhead = rateCalculator.calculateTeamSalaryOverhead(team);
        BigDecimal expensesOverhead = rateCalculator.calculateTeamOverheadWithoutPercentage(team);
        BigDecimal productiveOverhead = rateCalculator.calculateProductiveOverHead(team);
        teamOverhead.put(TeamWithEmployees.TeamOverheadType.SALARY_OVERHEAD, salaryOverhead);
        teamOverhead.put(TeamWithEmployees.TeamOverheadType.EXPENSES_OVERHEAD, expensesOverhead);
        teamOverhead.put(TeamWithEmployees.TeamOverheadType.TOTAL_OVERHEAD, productiveOverhead);
        return teamOverhead;
    }

    private List<Map<String, Double>> calculateTeamPercentage(TeamWithEmployees team) {
        List<Map<String, Double>> teamPercentagePerEmployee = team.getTeamMembers().stream().map(e -> employeePercentage(e, team.getTeamOverheadValues().get(TeamWithEmployees.TeamOverheadType.TOTAL_OVERHEAD))).toList();
        return teamPercentagePerEmployee;
    }


    private Map<String, Double> employeePercentage(Employee employee, BigDecimal totalOverhead) {
        Map<String, Double> emplPercentage = new HashMap<>();
        BigDecimal employeeOverhead = employee.getOverhead();
        if (employeeOverhead.compareTo(BigDecimal.ZERO) == 0) {
            emplPercentage.put(employee.getName(), 0.0);
            return emplPercentage;
        }
        double percentage = employeeOverhead.divide(totalOverhead, MathContext.DECIMAL32).doubleValue() * 100;
        String formattedPercentage = String.format("%.2f", percentage);
        emplPercentage.put(employee.getName() + " " + formattedPercentage, percentage);
        return emplPercentage;
    }

    public Map<Integer, Team> getTeams() throws RateException {
        return teamDao.getTeams();
    }


    /**DISTRIBUTION  OPERATION LOGIC */

    /**
     * calculate the overhead off a region , if is zero return an  OverHeadComputationPair object
     * populated with the country name and the value zero
     *
     * @param region the region to calculate for.
     */

    @Override
    public OverheadComputationPair<String, BigDecimal> computeRegionOverhead(Region region) {
        BigDecimal regionOverhead = BigDecimal.ZERO;
        if (region.getCountries() != null) {
            for (Country country : region.getCountries()) {
                for (Team team : country.getTeams()) {
                    if (team.getActiveConfiguration() != null) {
                        regionOverhead = regionOverhead.add(team.getActiveConfiguration().getTeamDayRate());
                    }
                }
            }
            return new OverheadComputationPair<>(region.getRegionName(), regionOverhead);
        }
        return new OverheadComputationPair<>(region.getRegionName(), BigDecimal.ZERO);
    }

    //TOdo change to return info error Object
    /**validate if  inserted overhead percentages   are bigger than 100% */
    @Override
    public Map<Team,String> validateDistributionInputs(Map<Team, String> insertedDistributionPercentageFromTeams) {
        Map <Team,String> teamsInvalid = new HashMap<>();
        Double totalOverhead = 0.0;
        for(Team team: insertedDistributionPercentageFromTeams.keySet()){
            String overheadValue = insertedDistributionPercentageFromTeams.get(team);
            if(!isOverheadFormatValid(overheadValue)){
                teamsInvalid.put(team,overheadValue);
            }
        }
        if(!teamsInvalid.isEmpty()){
            return teamsInvalid;
        }

        for(Team team: insertedDistributionPercentageFromTeams.keySet()){
            String overheadValue = insertedDistributionPercentageFromTeams.get(team);
            Double  value = validatePercentageValue(overheadValue);
            if(value!=null){
                totalOverhead+=value;
            }
        }

        if(totalOverhead>100){
            return insertedDistributionPercentageFromTeams;
        }

        return Collections.emptyMap();
    }




    private boolean isOverheadFormatValid(String overhead){
        return overhead.matches("^\\d{0,3}([.,]\\d{1,2})?$");
    }


    private  String convertToDecimalPoint(String value) {
        String validFormat;
        if(value== null){
            return null;
        }
        if (value.contains(",")) {
            validFormat = value.replace(",", ".");
        } else {
            validFormat = value;
        }
        return validFormat;
    }

    /**convert string to double , if the input is invalid than the value returned will be null;*/
    private Double validatePercentageValue(String newValue){
        String decimalPoint = convertToDecimalPoint(newValue);
        Double overheadValue= null;
        try{
            overheadValue =  Double.parseDouble(decimalPoint);
        }catch(NumberFormatException e){
            return overheadValue;
        }
        return overheadValue;
    }

    /** calculate the overhead for the teams with the new overhead added */
    public  void addOverheadPercentageForTeams(Team teamToDistributeFrom, Map<Team,String> teamsToDistributeTo){

//        for(){
//
//       }
    }




/**calculate the total overhead for the inserted valid values*/
    @Override
    public double calculateTotalOverheadInsertedForValidInputs(Map<Integer, String> insertedDistributionPercentageFromTeams) {
        double totalOverhead = 0.0;
        for(String val : insertedDistributionPercentageFromTeams.values()){
          Double validCalculation = validatePercentageValue(val);
          if(validCalculation!=null) {
              totalOverhead += validCalculation;
          }
        }
        return totalOverhead;
    }
}



