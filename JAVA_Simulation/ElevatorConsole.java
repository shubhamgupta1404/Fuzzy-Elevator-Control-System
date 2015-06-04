// Copyright (c) 2003 Webware Consulting
package newSim;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class ElevatorConsole extends JFrame  implements WindowListener {
   private Building building;
	private int totalWaiting;
	private int totalRiding;
	private GridBagLayout theLayout;
	private GridBagConstraints theConstraint;
	private JLabel totalPeopleLabel;
	private JLabel inBuildingLabel;
	private JLabel outsideBuildingLabel;
	private JLabel waitLabel;
	private JLabel rideLabel;
	private JLabel tookStairLabel;
   private JLabel totalWorkingLabel;
	private JLabel timeRemainingLabel;
	private JLabel elapsedTimeLabel;
   private JLabel uCableGrid[][];
   private JLabel lCableGrid[][];
   private JLabel floorInd[];
   private JLabel floorLabel[];
   private int[] elevatorLocations;
   private Container theContainer;
   private Insets inset;
	public ElevatorConsole(String source) {
      init();
      pack();
      setVisible(true);
      if(source == null)
         setDefaultCloseOperation(EXIT_ON_CLOSE);
      else
         setDefaultCloseOperation(DISPOSE_ON_CLOSE);
   }
   public void setBuilding(Building theBuilding){
      building = theBuilding;
   }
   public void updateDisplay(){
      updateFloors();
      updateElevators();
      updateCounters();
   }
	private void init() {
      addWindowListener(this);
      elevatorLocations = new int[Building.MAX_ELEVATORS];
      uCableGrid = new JLabel[Building.MAX_FLOORS][Building.MAX_FLOORS];
      lCableGrid = new JLabel[Building.MAX_FLOORS][Building.MAX_FLOORS];
      floorInd = new JLabel[Building.MAX_ELEVATORS];
      for(int e = 0; e < Building.MAX_ELEVATORS; e++){
         lCableGrid[e][0] = new JLabel("-- " + 0,JLabel.CENTER);
         uCableGrid[e][0] = new JLabel("__:__",JLabel.CENTER);
         floorInd[e] = new JLabel("1",JLabel.CENTER);
         for (int f = 1; f < Building.MAX_FLOORS; f++) {
            lCableGrid[e][f] = new JLabel("  :  ",JLabel.CENTER);
            uCableGrid[e][f] = new JLabel("  :  ",JLabel.CENTER);
         }
      }
      floorLabel = new JLabel[Building.MAX_FLOORS];
		for (int i = 0; i < Building.MAX_FLOORS; i++) {
			floorLabel[i] = new JLabel("" + (i+1) + ":U0D0",JLabel.CENTER);
		}
      inset = new Insets(0,2,0,2);
      theContainer = getContentPane();
      theContainer.setBackground(new Color(255,255,255));
		theContainer.setFont(new Font("Dialog",Font.PLAIN,9));
		theLayout = new GridBagLayout();
		theConstraint = new GridBagConstraints();
      theConstraint.insets = inset;
		theContainer.setLayout(theLayout);
		totalPeopleLabel = new JLabel(String.valueOf(Simulator.MAX_PEOPLE));
		inBuildingLabel = new JLabel("0000");
		outsideBuildingLabel = new JLabel(String.valueOf(Simulator.MAX_PEOPLE));
		waitLabel = new JLabel("0000");
		rideLabel = new JLabel("0000");
		tookStairLabel = new JLabel("0000");
      totalWorkingLabel = new JLabel("0000");
		timeRemainingLabel = new JLabel(String.valueOf(Simulator.getTimeRemaing()));
		elapsedTimeLabel = new JLabel("0000");
		constrain(new JLabel("Floor"), 0, 0, 1, 1);
      for (int e = 0; e < Building.MAX_ELEVATORS; e++) {
			constrain(floorInd[e], e + 1, 0);
			for (int f = (Building.MAX_FLOORS) -1; f >= 0; f--)  {
				constrain(uCableGrid[e][f], e + 1, -1);
				constrain(lCableGrid[e][f], e + 1, -1);
			}
		}
      for (int i = Building.MAX_FLOORS - 1; i >= 0; i--) {
         constrain(new JLabel("         ",JLabel.CENTER), 0, -1);
			constrain(floorLabel[i], 0 , -1);
		}
		constrain(new JLabel("Total"), 0, -1, 1, 1);
		constrain(new JLabel("People"), 0, -1, 1, 1);
		constrain(new JLabel("In"),1, -1, 1,1);
		constrain(new JLabel("Bldg"),1, -1, 1,1);
		constrain(new JLabel("Outside"), 2, -1, 1, 1);
		constrain(new JLabel("Bldg"), 2, -1, 1, 1);
		constrain(new JLabel("Total"), 3, -1, 1, 1);
		constrain(new JLabel("Waiting"), 3, -1, 1, 1);
		constrain(new JLabel("Total"), 4, -1, 1, 1);
		constrain(new JLabel("Riding"), 4, -1, 1, 1);
		constrain(new JLabel("Took"), 5, -1, 1, 1);
		constrain(new JLabel("Stair"), 5, -1, 1, 1);
		constrain(new JLabel("Total"), 6, -1, 1, 1);
		constrain(new JLabel("Wrkg"), 6, -1, 1, 1);
		constrain(new JLabel("Num"), 7, -1, 1, 1);
		constrain(new JLabel("Elev"), 7, -1, 1, 1);
		constrain(new JLabel("Seconds"), 8, -1, 1, 1);
		constrain(new JLabel("Left"), 8, -1, 1, 1);
		constrain(new JLabel("Elapsed"), 9, -1, 1, 1);
		constrain(new JLabel("Time"), 9, -1, 1, 1);
		constrain(totalPeopleLabel, 0, -1, 1, 1);
		constrain(inBuildingLabel, 1, -1, 1, 1);
		constrain(outsideBuildingLabel, 2, -1, 1, 1);
		constrain(waitLabel, 3, -1, 1, 1);
		constrain(rideLabel, 4, -1, 1, 1);
		constrain(tookStairLabel, 5, -1, 1, 1);
		constrain(totalWorkingLabel, 6, -1, 1, 1);
		constrain(new JLabel(String.valueOf(Building.MAX_ELEVATORS)), 7, -1, 1, 1);
		constrain(timeRemainingLabel, 8, -1, 1, 1);
		constrain(elapsedTimeLabel, 9, -1, 1, 1);
	}
   private void constrain( JLabel lb, int x ,int y, int height, int width) {
		theConstraint.gridheight = height;
		theConstraint.gridwidth = width;
		theConstraint.anchor = GridBagConstraints.NORTH;
		theConstraint.gridx = x;
		theConstraint.gridy = y;
		theLayout.setConstraints(lb, theConstraint);
		theContainer.add(lb);
	}
   private void constrain( JLabel lb, int x ,int y) {
		theConstraint.gridheight = 1;
		theConstraint.gridwidth = 1;
		theConstraint.anchor = GridBagConstraints.NORTH;
		theConstraint.gridx = x;
		theConstraint.gridy = y;
		theLayout.setConstraints(lb, theConstraint);
		theContainer.add(lb);
	}
   private void updateCounters(){
      inBuildingLabel.setText(String.valueOf(Simulator.MAX_PEOPLE - building.peopleOutside));
      outsideBuildingLabel.setText(String.valueOf(building.peopleOutside));
      waitLabel.setText(String.valueOf(totalWaiting));
      rideLabel.setText(String.valueOf(totalRiding));
      tookStairLabel.setText(String.valueOf(building.peopleTakingStairs));
      totalWorkingLabel.setText(String.valueOf(building.peopleWorking));
      timeRemainingLabel.setText(String.valueOf(Simulator.getTimeRemaing()));
      elapsedTimeLabel.setText(String.valueOf(Simulator.getElapsedTime()));
   }
   private void updateFloors(){
      totalWaiting = 0;
      int up = 0;
      int down = 0;
      for(int i = 0; i < floorLabel.length; i++){
         up = building.getNumberWaitingUp(i+1);
         down = building.getNumberWaitingDown(i+1);
         totalWaiting += up;
         totalWaiting += down;
         floorLabel[i].setText("" + (i+1) + ":U" + up + "D" + down );
      }
   }
   private void updateElevators(){
      ElevatorState state = null;
      int currentFloor = 0;
      totalRiding = 0;
      String directionInd = new String();
      for(int e = 0; e < building.MAX_ELEVATORS; e++){
         state = building.getElevatorState(e + 1);
         totalRiding += state.numberRiders;
         currentFloor = state.currentFloorNumber;
         floorInd[e].setText(String.valueOf(currentFloor));
         if(elevatorLocations[e] != currentFloor){ // don't redraw if we don't have too
            elevatorLocations[e] = currentFloor;
            if(currentFloor > 1) {
               for (int f = currentFloor - 1; f >= 0; f--) {
                  uCableGrid[e][f].setText("   ");
                  lCableGrid[e][f].setText("   ");
               }
            }
            if(currentFloor < Building.MAX_FLOORS) {
               for (int f = currentFloor; f < Building.MAX_FLOORS; f++) {
                  uCableGrid[e][f].setText("  :  ");
                  lCableGrid[e][f].setText("  :  ");
               }
            }
         }
         switch (state.motionDirection) {
            case Elevator.NO_DIRECTION:
               directionInd = "--";
               break;
            case Elevator.MOVING_UP:
               directionInd = "Up";
               break;
            case Elevator.MOVING_DOWN:
               directionInd = "Dn";
               break;
         }
         uCableGrid[e][currentFloor - 1].setText("__:__");		
         lCableGrid[e][currentFloor - 1].setText(directionInd + state.numberRiders);
      }
   }
	public  void windowActivated ( WindowEvent e ){}
	public void windowClosed ( WindowEvent e ){Simulator.stopProgram();}
	public void windowClosing (WindowEvent e ){}
	public void windowDeactivated ( WindowEvent  e){}
   public void windowDeiconified (WindowEvent e ){}
   public void windowIconified (WindowEvent  e){}
   public void windowOpened (WindowEvent e){}

}