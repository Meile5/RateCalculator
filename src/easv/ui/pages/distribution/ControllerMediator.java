package easv.ui.pages.distribution;
import easv.be.Team;
import easv.ui.components.distributionPage.distributeFromTeamInfo.DistributionComponentInterface;

public class ControllerMediator {
    private  DistributionControllerInterface distributionController;
    private DistributionComponentInterface distributionTeamController;


   public  void registerDistributionController(DistributionController distributionController){
       this.distributionController=distributionController;
   }

   public void setTheSelectedComponentToDistributeFrom(DistributionComponentInterface selectedTeamComponent){
       if(this.distributionTeamController!=null){
           this.distributionTeamController.setTheStyleClassToDefault();
       }
       this.distributionTeamController=selectedTeamComponent;
   }

  // public void showTeamToDistributeFromBarChart(){
  //     distributionController.showTheTeamFromBarchart();
 //  }

    public void addTeamToDistributeFrom(Team team) {
       distributionController.addTeamToDistributeFrom(team);

    }

    /** add the selected team to distribute to */
    public void addDistributeToTeam(Team teamToDisplay) {
        distributionController.addDistributeToTeam(teamToDisplay);
    }
}
