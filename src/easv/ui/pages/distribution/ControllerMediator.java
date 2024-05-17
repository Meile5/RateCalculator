package easv.ui.pages.distribution;
import easv.be.Team;
import easv.ui.components.distributionPage.distributeFromTeamInfo.DistributionComponentInterface;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToController;

import java.util.HashMap;
import java.util.Map;

public class ControllerMediator {
    private  DistributionControllerInterface distributionController;
    private DistributionComponentInterface distributionTeamController;
    private Map<Integer,DistributeToController> distributeToControllers;

    public ControllerMediator() {
        distributeToControllers = new HashMap<>();
    }

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

    /**save added controllers in order to change their style */
    public void addDistributeToController(DistributeToController selectedTeamToDistributeTo, int teamId) {
        distributeToControllers.put(teamId,selectedTeamToDistributeTo);
    }

    /**remove the controller when the user is  removing the team from the, selected list to distribute to */
    public void removeDistributeToController(int teamId){
        distributeToControllers.remove(teamId);
    }

    /**clear the stored controllers when the operation is saved*/
    public void clearSavedControllers  (){
        this.distributeToControllers.clear();
    }


    public void changeComponentStyleToError(Integer teamId) {
        DistributeToController distributeToController = distributeToControllers.get(teamId);
        if(distributeToController!=null){
            distributeToController.changeStyleToError();
        }
    }
}
