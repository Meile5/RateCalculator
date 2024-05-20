package easv.bll.TeamLogic;


import easv.be.*;
import easv.bll.EmployeesLogic.IRateCalculator;
import easv.bll.EmployeesLogic.RateCalculator;
import easv.dal.teamDao.ITeamDao;
import easv.dal.teamDao.TeamDao;
import easv.exception.ErrorCode;
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



    //TODO validate if the team to distribute to has no more overhead
    /**
     * validate if  inserted overhead percentages   are bigger than 100%
     */
    @Override
    public DistributionValidation validateDistributionInputs(Map<Team, String> insertedDistributionPercentageFromTeams,Team selectedTeamToDistributeFrom) {
        DistributionValidation distributionValidation = new DistributionValidation();
            if(selectedTeamToDistributeFrom.getActiveConfiguration().getTeamDayRate().doubleValue()<=0){
                List<Team> overheadZero = new ArrayList<>();
                overheadZero.add(selectedTeamToDistributeFrom);
                distributionValidation.getErrorValues().put(ErrorCode.OVERHEAD_ZERO,overheadZero);
                return distributionValidation;
            }


        double totalOverhead = 0.0;
        List<Team> invalidTeamsIds = new ArrayList<>();
        List<Team> emptyValuesInserted = new ArrayList<>();

        for (Team team : insertedDistributionPercentageFromTeams.keySet()) {
            String overheadValue = insertedDistributionPercentageFromTeams.get(team);
            if (overheadValue.isEmpty()) {
                emptyValuesInserted.add(team);
            }

            if (!isOverheadFormatValid(overheadValue)) {
                invalidTeamsIds.add(team);
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
    public double calculateTotalOverheadInsertedForValidInputs(Map<Team, String> insertedDistributionPercentageFromTeams) {
        double totalOverhead = 0.0;
        for (String val : insertedDistributionPercentageFromTeams.values()) {
            Double validCalculation = validatePercentageValue(val);
            if (validCalculation != null) {
                totalOverhead += validCalculation;
            }
        }
        return totalOverhead;
    }



    //TODO after the review change this method to retrieve  only the previous values, because new values are saved on the model insertedTeamsWithOverheadPercentage collection

    @Override
    public Map<OverheadHistory, List<Team>> performSimulationComputation(Team selectedTeamToDistributeFrom, Map<Team, String> insertedDistributionPercentageFromTeams) {
        Map<OverheadHistory, List<Team>> simulationValues = new HashMap<>();
        List<Team> previousOverheadValues = new ArrayList<>();
        List<Team> currentComputedOverheadValues = new ArrayList<>();

        // Create copies of the teams for previous values
        Team selectedTeamToDistributeFromCopy = new Team(selectedTeamToDistributeFrom);
        if (selectedTeamToDistributeFrom.getActiveConfiguration() != null) {
            selectedTeamToDistributeFromCopy.setActiveConfiguration(new TeamConfiguration(selectedTeamToDistributeFrom.getActiveConfiguration()));
            previousOverheadValues.add(selectedTeamToDistributeFromCopy);
        }

        for (Team team : insertedDistributionPercentageFromTeams.keySet()) {
            Team teamCopy = new Team(team);
            if (team.getActiveConfiguration() != null) {
                teamCopy.setActiveConfiguration(new TeamConfiguration(team.getActiveConfiguration()));
                previousOverheadValues.add(teamCopy);
            }
        }

        // Compute new overhead for the selected team to distribute from
        double totalPercentage = calculateTotalOverheadInsertedForValidInputs(insertedDistributionPercentageFromTeams);
        double teamToDistributeFromNewDayRate = selectedTeamToDistributeFrom.getActiveConfiguration().getTeamDayRate().doubleValue() * (1 - (totalPercentage / 100));
        double teamToDistributeFromNewHourlyRate = selectedTeamToDistributeFrom.getActiveConfiguration().getTeamHourlyRate().doubleValue() * (1 - (totalPercentage / 100));

        Team distributeFromNewComputation = new Team(selectedTeamToDistributeFrom);
        distributeFromNewComputation.getActiveConfiguration().setTeamDayRate(BigDecimal.valueOf(teamToDistributeFromNewDayRate));
        distributeFromNewComputation.getActiveConfiguration().setTeamHourlyRate(BigDecimal.valueOf(teamToDistributeFromNewHourlyRate));
        currentComputedOverheadValues.add(distributeFromNewComputation);

        // Set the new value for the selected team to distribute from
        selectedTeamToDistributeFrom.getActiveConfiguration().setTeamDayRate(BigDecimal.valueOf(teamToDistributeFromNewDayRate));
        selectedTeamToDistributeFrom.getActiveConfiguration().setTeamHourlyRate(BigDecimal.valueOf(teamToDistributeFromNewHourlyRate));

        // Compute new overhead values for the team to distribute to
        for (Team team : insertedDistributionPercentageFromTeams.keySet()) {
            Team distributeToTeam = new Team(team);
            double teamPreviousOverheadDayRate = 0;
            double teamPreviousOverheadHourRate = 0;

            if (distributeToTeam.getActiveConfiguration() != null) {
                teamPreviousOverheadDayRate = distributeToTeam.getActiveConfiguration().getTeamDayRate().doubleValue();
                teamPreviousOverheadHourRate = distributeToTeam.getActiveConfiguration().getTeamHourlyRate().doubleValue();
            }
            Double teamNewInsertedValue = validatePercentageValue(insertedDistributionPercentageFromTeams.get(team));

            if (selectedTeamToDistributeFrom.getActiveConfiguration().getTeamDayRate().doubleValue() == 0 && teamPreviousOverheadDayRate == 0) {
                distributeToTeam.getActiveConfiguration().setTeamDayRate(BigDecimal.ZERO);
                distributeToTeam.getActiveConfiguration().setTeamHourlyRate(BigDecimal.ZERO);
            } else {
                double computedNewOverheadDayRate = teamPreviousOverheadDayRate + (selectedTeamToDistributeFromCopy.getActiveConfiguration().getTeamDayRate().doubleValue() * (teamNewInsertedValue / 100));
                double computedNewOverheadHourRate = teamPreviousOverheadHourRate + (selectedTeamToDistributeFromCopy.getActiveConfiguration().getTeamHourlyRate().doubleValue() * (teamNewInsertedValue / 100));

                if (distributeToTeam.getActiveConfiguration() != null) {
                    distributeToTeam.getActiveConfiguration().setTeamDayRate(BigDecimal.valueOf(computedNewOverheadDayRate));
                    distributeToTeam.getActiveConfiguration().setTeamHourlyRate(BigDecimal.valueOf(computedNewOverheadHourRate));
                }
                if (team.getActiveConfiguration() != null) {
                    team.getActiveConfiguration().setTeamDayRate(BigDecimal.valueOf(computedNewOverheadDayRate));
                    team.getActiveConfiguration().setTeamHourlyRate(BigDecimal.valueOf(computedNewOverheadHourRate));
                }
                currentComputedOverheadValues.add(distributeToTeam);
            }
        }

        simulationValues.put(OverheadHistory.PREVIOUS_OVERHEAD, previousOverheadValues);
        simulationValues.put(OverheadHistory.CURRENT_OVERHEAD, currentComputedOverheadValues);
        return simulationValues;
    }

    @Override
    public boolean saveDistributionOperation(Map<Team, String> insertedDistributionPercentageFromTeams, Team selectedTeamToDistributeFrom) throws RateException {
        // calculate the  total overhead inserted to be distributed
        Double totalOverhead = insertedDistributionPercentageFromTeams.values()
                               .stream().map(this::validatePercentageValue)
                                .reduce(0.0,Double::sum);

        // calculate the shared overhead value
        double sharedOverhead = selectedTeamToDistributeFrom.getActiveConfiguration().getTeamDayRate().doubleValue()*(totalOverhead/100);
        double sharedHourRate = selectedTeamToDistributeFrom.getActiveConfiguration().getTeamHourlyRate().doubleValue()*(totalOverhead/100);
        selectedTeamToDistributeFrom.getActiveConfiguration().setTeamDayRate(BigDecimal.valueOf(sharedOverhead));
        selectedTeamToDistributeFrom.getActiveConfiguration().setTeamHourlyRate(BigDecimal.valueOf(sharedHourRate));



        Map<Team,Map<RateType,Double>> receivedTeams = new HashMap<>();
        for(Team team : insertedDistributionPercentageFromTeams.keySet()){
            Double overheadReceivedPercentage = validatePercentageValue(insertedDistributionPercentageFromTeams.get(team));
            double overHeadReceivedValuePerDay = sharedOverhead*(overheadReceivedPercentage/100);
            double overHeadReceivedPerHour = selectedTeamToDistributeFrom.getActiveConfiguration().getTeamHourlyRate().doubleValue()*(overheadReceivedPercentage/100);

            // compute the team  new overhead
            BigDecimal teamDayOverheadDistribution = team.getActiveConfiguration().getTeamDayRate().add(new BigDecimal(overHeadReceivedValuePerDay));
            BigDecimal teamHourOverheadDistribution = team.getActiveConfiguration().getTeamHourlyRate().add(new BigDecimal(overHeadReceivedPerHour));
            team.getActiveConfiguration().setTeamDayRate(teamDayOverheadDistribution);
            team.getActiveConfiguration().setTeamHourlyRate(teamHourOverheadDistribution);
            Map<RateType,Double> teamReceivedOverhead = new HashMap<>();
            teamReceivedOverhead.put(RateType.DAY_RATE,overHeadReceivedValuePerDay);
            teamReceivedOverhead.put(RateType.HOUR_RATE,overHeadReceivedPerHour);
            receivedTeams.put(team,teamReceivedOverhead);
        }
        // after saving in the database, update in the cached memory

        return  teamDao.savePerformedDistribution(receivedTeams,selectedTeamToDistributeFrom,sharedOverhead,sharedHourRate);
    }

}



