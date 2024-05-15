package easv.ui.pages.distribution;

import easv.be.Team;
import easv.ui.components.distributionPage.distributeFromTeamInfo.DistributionComponentInterface;

public class ControllerMediator {
    private  DistributionControllerInterface distributionController;
    private DistributionComponentInterface distributionTeamController;




   public  void registerDistributionController(DistributionController distributionController){
       this.distributionController=distributionController;
   }

   public void setDistributionTeamController(DistributionComponentInterface distributionControllerInterface){
       this.distributionTeamController= distributionControllerInterface;
   }

   public void setTheSelectedTeam(Team team){
       this.distributionController.setTheSelectedTeam(team);
   }

   public void setTheSelectedComponentToDistributeFrom(DistributionComponentInterface selectedTeamComponent){
       if(this.distributionTeamController!=null){
           this.distributionTeamController.setTheStyleClassToDefault();
       }
       this.distributionTeamController=selectedTeamComponent;
   }

}
