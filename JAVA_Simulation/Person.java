// Copyright (c) 2003 Webware Consulting
package newSim;

public class Person implements Runnable{
   //possible actions
   public static final int WAITING = 1; 
   public static final int TAKING_STAIRS = 2; 
   public static final int WORKING = 3;
   public static final int WALKING_OUTSIDE = 4;
   public static final int RIDING = 5;
   public static final int GOING_NOWHERE = -1;
   public static final int OUTSIDE = -1;
   public static final int IN_ELEVATOR = 0;
   private static Building building;
   private int personID; 
   private int destination;
   private int location; //anything greater than zero is a floor number
   private long activityTime; 
   private int activity; 
   private Elevator elevator; 
   private Floor floor;
   private Thread activePerson;
   private Logger log;
   private volatile boolean keepRunning;
   private PersonState state = new PersonState();
   public static void setBuilding(Building theBuilding){
      building = theBuilding;
   }
   public Person(int personID){
      this.personID = personID;
   }
   public void setStopRunning(){
      keepRunning = false;
   }
   public boolean getKeepRunning(){
      return keepRunning;
   }
   public synchronized void attention(){
      activePerson.interrupt();
   }
   public synchronized void elevatorArrived(Elevator elevator) {        
      this.elevator = elevator;
   }
   public PersonState getState() {        
       state.personNumber = personID;
       state.activity = activity;
       state.destination = destination;
       state.location = location;
       if(elevator != null)
         state.elevatorNumber = elevator.getElevatorNumber();
       return state;
   }
   public int getPersonNumber(){
      return personID;
   }
   public void start(){
      destination = GOING_NOWHERE;
      activity = WALKING_OUTSIDE;
      if(Simulator.debug) log = new Logger("Person" + personID);
      keepRunning = true;
      if(activePerson == null){
         activePerson = new Thread(this);
         //activePerson.setDaemon(true);
         activePerson.setPriority(Thread.NORM_PRIORITY - 2);
         activePerson.start();
      }
   }
   public void run(){
      while(keepRunning){
         switch(activity){
            case WALKING_OUTSIDE:
               if(wantsToEnter()){
                  building.peopleOutside--;
                  setDestination();
                  floor = building.enterBuilding();
                  location = floor.getFloorNumber();
                  if(destination > location){ //go up
                     activity = WAITING;
                     setWaitTime();
                     floor.summonElevatorUp(this);
                     action();
                  }else{ // work on first floor
                     building.peopleWorking++;
                     activity = WORKING;
                     destination = GOING_NOWHERE;
                     setWorkingTime();
                     action();
                  }
               }else{ // did not enter
                  destination = GOING_NOWHERE;
                  location = OUTSIDE;
                  activity = WALKING_OUTSIDE;
                  setWorkingTime();
                  action();
               }
               break;
            case TAKING_STAIRS:
               if(location == destination){
                  building.peopleTakingStairs--;
                  building.peopleWorking++;
                  activity = WORKING;
                  floor = building.getFloor(location);
                  destination = GOING_NOWHERE;
                  setWorkingTime();
                  action();
               }else if(destination > location){
                  climbUp();
               }else{
                  climbDown();
               }
               break;
            case WAITING:
               if(elevator != null){
                  enterElevator();
               }else{ //elevator not here yet
                  if(wantsToTakeStairs()){
                     floor.stopWaiting(this);
                     building.peopleTakingStairs++;
                     activity = TAKING_STAIRS;
                     if(destination > location){
                        climbUp();
                     }else{
                        climbDown();
                     }
                  }else{//continue to wait
                     setWaitTime();
                     action();
                  }
               }
               break;
            case WORKING:
               if(location == 1){
                  if(wantsToLeave()){
                     building.peopleOutside++;
                     building.peopleWorking--;
                     destination = GOING_NOWHERE;
                     location = OUTSIDE;
                     activity = WALKING_OUTSIDE;
                     setWorkingTime();
                     action();
                  }else{ // stay in the building
                     setDestination();
                     if(destination > location){ //go up
                        building.peopleWorking--;
                        activity = WAITING;
                        setWaitTime();
                        floor.summonElevatorUp(this);
                        action();
                     }else{ // work on same floor
                        activity = WORKING;
                        destination = GOING_NOWHERE;
                        setWorkingTime();
                        action();
                     }
                  }
               }else{ // not on first floor
                  setDestination();
                  if(destination > location){ //go up
                     building.peopleWorking--;
                     activity = WAITING;
                     setWaitTime();
                     floor.summonElevatorUp(this);
                     action();
                  }else if(destination < location){
                     building.peopleWorking--;
                     activity = WAITING;
                     setWaitTime();
                     floor.summonElevatorDown(this);
                     action();
                  }else{ // work on same floor
                     activity = WORKING;
                     destination = GOING_NOWHERE;
                     setWorkingTime();
                     action();
                  }
               }
               break;
            case RIDING:
               if(elevator.getCurrentFloorNumber() == destination){
                  leaveElevator();
               }else
                  setWaitTime();
                  action();
               break;
         }
         if(Simulator.debug) log.write(getState().toString());
      }
      if(log != null)log.close();
   }
   private boolean wantsToEnter(){
		double i = (Math.random() * 1000);
		if(Simulator.debug) log.write("Person: " + personID + " Wants to enter: " + i);
		if ( i < 600) return true; else return false;
	}
   private boolean wantsToTakeStairs(){
		double i = (Math.random() * 10000);
		if(Simulator.debug) log.write("Person: " + personID + " Wants to climb: " + i);
		if ( i < 600) return true; else return false;
      //return true;
	}
	private void setDestination() {
		destination = ((int) (1 + ((Math.random() * 10000) % (building.MAX_FLOORS ))));
      if(Simulator.debug) log.write("Person: " + personID + " Setting destination: " + destination);
	}
	private void setWaitTime() {
		activityTime = 1000 * ((int) (1 + ((Math.random() * 100) % 4)));
		if(Simulator.debug) log.write("Person: " + personID + " Maxwait: " + activityTime);
	}
	private void setWorkingTime() {
		activityTime = 1000 * ((int) (1 + ((Math.random() * 100) % 6)));
		if(Simulator.debug) log.write("Person: " + personID + " Business: " + activityTime);
	}
	private boolean wantsToLeave() {
		double i = (Math.random() * 10000);
		if(Simulator.debug) log.write("Person: " + personID + " Leaving number (6600): " + i);
		if ( i < 6600) return true; else return false;
	}
   private void action(){
      //if(Simulator.debug) log.write("About to sleep for " + activityTime + " " + getState());
      try{
         activePerson.sleep(activityTime);
      }catch(InterruptedException ix){
         //intentionally left empty
      }
   }
   private void climbUp(){
      if(location != Building.MAX_FLOORS){
         action();
         ++location;
      }
   }
   private void climbDown(){
      if(location != 1){
         action();
         --location;
      }
   }
   private  void enterElevator(){
      try{
         elevator.enterElevator(this);
         //we don't get here unless we entered the elevator
         elevator.setDestination(destination);
         floor.stopWaiting(this);
         floor = null;
         location = IN_ELEVATOR;
         activity = RIDING;
         activityTime = Elevator.FLOOR_TRAVEL_TIME * Math.abs(location - destination);
         action();
      }catch(ElevatorFullException fx){
         resetWaitForElevator();
      }catch(DoorClosedException cx){
        try{
            elevator.requestOpenDoor();//use loop so not reenterant method
         }catch(ElevatorMovingException mx){
            resetWaitForElevator(); // assume the elevator has moved on
         }
      }
   }
   private void leaveElevator(){
      try{
         elevator.leaveElevator(this);
         floor = building.getFloor(destination);
         location = destination;
         destination = GOING_NOWHERE;
         activity = WORKING;
         building.peopleWorking++;
         setWorkingTime();
         action();
      }catch(DoorClosedException dx){
         try{
            elevator.requestOpenDoor();
         }catch(ElevatorMovingException mx){
            // missed our floor or not arrived yet either way push the button
            elevator.setDestination(destination);
         }
      }
   }
   private void resetWaitForElevator(){
      if(Simulator.debug) log.write("Person " + personID + " missed elevator " + elevator.getElevatorNumber());
      floor.stopWaiting(this);
      elevator = null;
      setWaitTime();
      action();
      if(destination > location)
         floor.summonElevatorUp(this);
      else
         floor.summonElevatorDown(this);
   }
} // end Person



