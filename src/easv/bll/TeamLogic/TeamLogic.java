package easv.bll.TeamLogic;


import easv.be.*;
import easv.bll.EmployeesLogic.IRateCalculator;
import easv.bll.EmployeesLogic.RateCalculator;
import easv.dal.teamDao.ITeamDao;
import easv.dal.teamDao.TeamDao;
import easv.exception.ErrorCode;
import easv.exception.RateException;
import javafx.collections.ObservableMap;


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


    /**
     * validate if  inserted overhead percentages   are bigger than 100%
     */
    @Override
    public DistributionValidation validateDistributionInputs(Map<Integer, String> insertedDistributionPercentageFromTeams) {
        DistributionValidation distributionValidation = new DistributionValidation();
        double totalOverhead = 0.0;
        List<Integer> invalidTeamsIds = new ArrayList<>();
        List<Integer> emptyValuesInserted = new ArrayList<>();

        for (Integer teamId : insertedDistributionPercentageFromTeams.keySet()) {
            String overheadValue = insertedDistributionPercentageFromTeams.get(teamId);
            if (overheadValue.isEmpty()) {
                emptyValuesInserted.add(teamId);
            }

            if (!isOverheadFormatValid(overheadValue)) {
                invalidTeamsIds.add(teamId);
            }
            Double value = validatePercentageValue(overheadValue);
            if (value != null) {
                totalOverhead += value;
            }
        }

        if (!invalidTeamsIds.isEmpty()) {
            distributionValidation.getErrorValues().put(ErrorCode.INVALID_OVERHEADVALUE, invalidTeamsIds);
        }

        if (totalOverhead > 100) {
            distributionValidation.getErrorValues().put(ErrorCode.OVER_LIMIT, new ArrayList<>(insertedDistributionPercentageFromTeams.keySet()));
        }

        if (!emptyValuesInserted.isEmpty()) {
            distributionValidation.getErrorValues().put(ErrorCode.EMPTY_OVERHEAD, emptyValuesInserted);
        }

        return distributionValidation;
    }

    private boolean isOverheadFormatValid(String overhead) {
        return overhead.matches("^\\d{0,3}([.,]\\d{1,2})?$");
    }

    private String convertToDecimalPoint(String value) {
        String validFormat;
        if (value == null) {
            return null;
        }
        if (value.contains(",")) {
            validFormat = value.replace(",", ".");
        } else {
            validFormat = value;
        }
        return validFormat;
    }

    /**
     * convert string to double , if the input is invalid than the value returned will be null;
     */
    private Double validatePercentageValue(String newValue) {
        String decimalPoint = convertToDecimalPoint(newValue);
        Double overheadValue = null;
        try {
            overheadValue = Double.parseDouble(decimalPoint);
        } catch (NumberFormatException e) {
            return overheadValue;
        }
        return overheadValue;
    }


    /**
     * calculate the total overhead for the inserted valid values
     */
    @Override
    public double calculateTotalOverheadInsertedForValidInputs(Map<Integer, String> insertedDistributionPercentageFromTeams) {
        double totalOverhead = 0.0;
        for (String val : insertedDistributionPercentageFromTeams.values()) {
            Double validCalculation = validatePercentageValue(val);
            if (validCalculation != null) {
                totalOverhead += validCalculation;
            }
        }
        return totalOverhead;
    }


    /**perform the simulation computation*/
    @Override
    public Map<OverheadHistory, List<OverheadComputationPair<String, Double>>> performSimulationComputation(Team selectedTeamToDistributeFrom, Map<Integer, String> insertedDistributionPercentageFromTeams, ObservableMap<Integer, Team> teamsWithEmployees) {
        Map<OverheadHistory,List<OverheadComputationPair<String, Double>>>  simulationValues =  new HashMap<>();
        List<OverheadComputationPair<String,Double>> previousOverheadValues  =  new ArrayList<>();
        List<OverheadComputationPair<String,Double>> currentComputedOverheadValues=   new ArrayList<>();

        // add previous values
        if(selectedTeamToDistributeFrom.getActiveConfiguration()!=null){
            OverheadComputationPair<String,Double>  previousOverhead = new OverheadComputationPair<>(selectedTeamToDistributeFrom.getTeamName(),selectedTeamToDistributeFrom.getActiveConfiguration().getTeamDayRate().doubleValue());
            previousOverheadValues.add(previousOverhead);
        }
        for(Integer teamId:insertedDistributionPercentageFromTeams.keySet()){
         OverheadComputationPair<String,Double> previousOverhead = new OverheadComputationPair<>();
         Team team = teamsWithEmployees.get(teamId);
         previousOverhead.setKey(team.getTeamName());
         if(team.getActiveConfiguration()!=null){
             previousOverhead.setValue(team.getActiveConfiguration().getTeamDayRate().doubleValue());
         }
         previousOverheadValues.add(previousOverhead);
        }

        //compute new  overhead for the selected team to distribute from
        double totalPercentage = calculateTotalOverheadInsertedForValidInputs(insertedDistributionPercentageFromTeams);
        Double  teamToDistributeFromNewOverhead = selectedTeamToDistributeFrom.getActiveConfiguration().getTeamDayRate().doubleValue()*(totalPercentage/100);
        OverheadComputationPair<String,Double>  currentOverheadTeamFrom =  new OverheadComputationPair<>();
        currentOverheadTeamFrom.setKey(selectedTeamToDistributeFrom.getTeamName());
        currentOverheadTeamFrom.setValue(teamToDistributeFromNewOverhead);
        currentComputedOverheadValues.add(currentOverheadTeamFrom);



         double selectedTeamFromPreviousOverhead = 0.0;
         if(selectedTeamToDistributeFrom.getActiveConfiguration()!=null){
             selectedTeamFromPreviousOverhead= selectedTeamToDistributeFrom.getActiveConfiguration().getTeamDayRate().doubleValue();
         }




        //compute new overhead values for the team to distribute to
        for(Integer temId:insertedDistributionPercentageFromTeams.keySet()){
            OverheadComputationPair<String,Double>  currentOverheadTeamTo =  new OverheadComputationPair<>();
            Team team  = teamsWithEmployees.get(temId);

            double teamPreviousOverhead =  teamsWithEmployees.get(temId).getActiveConfiguration().getTeamDayRate().doubleValue();

            if(team.getActiveConfiguration()!=null){
               teamPreviousOverhead = team.getActiveConfiguration().getTeamDayRate().doubleValue();
            }
            Double teamNewInsertedValue =  validatePercentageValue(insertedDistributionPercentageFromTeams.get(temId));
            //TODO  continue from here
            if(selectedTeamFromPreviousOverhead==0){

            }


            Double computedNewOverhed = teamPreviousOverhead+(selectedTeamToDistributeFrom.getActiveConfiguration().(teamNewInsertedValue/100));
            currentOverheadTeamTo.setKey(team.getTeamName());
            currentOverheadTeamTo.setValue(computedNewOverhed);
            currentComputedOverheadValues.add(currentOverheadTeamTo);
        }

        //add the previous overhead to the map
        simulationValues.put(OverheadHistory.PREVIOUS_OVERHEAD,previousOverheadValues);

        // add current overhead to the map
        simulationValues.put(OverheadHistory.CURRENT_OVERHEAD,currentComputedOverheadValues);
        return simulationValues;
    }
}



