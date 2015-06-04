// Copyright (c) 2003 Webware Consulting
package newSim;
import java.util.*;
public class Elevator implements Runnable {
   //Direction for motionDirection
   public static final int MOVING_UP = 1;
   public static final int NO_DIRECTION = 0;
   public static final int MOVING_DOWN = -1;
   //state values for motionState
   public static final int MOVING = 1;
   public static final int STOPPED = 0;
   //state values for door
   public static final int DOOR_OPEN = 1;
   public static final int DOOR_CLOSED = 0;
   private static final long FLOOR_WAIT_TIME = 1000; 
   public static final long FLOOR_TRAVEL_TIME = 500;
   private static final long INACTIVE_TIME = 1000 * 2;
   private static final int MAX_OCCUPANCY = 20;
   private int elevatorID; 
   private int doorState; 
   private int motionState;
   private int motionDirection;
   private volatile int currentFloorNumber;
   private boolean requestDoorOpen;
   private boolean[] destinationList = new boolean[Building.MAX_FLOORS]; // of type int
   private static ElevatorController elevatorController; 
   private Vector riders = new Vector();
   private Thread activeElevator;
   private Logger log;
   private volatile boolean keepRunning;
   private ElevatorState state = new ElevatorState();
   public Elevator(int elevatorNumber){
      elevatorID = elevatorNumber;
      for(int i = 0; i < destinationList.length; i++){
         destinationList[i] = false;
      }
      motionDirection = NO_DIRECTION;
      currentFloorNumber = 1;
      motionState = STOPPED;
      doorState = DOOR_CLOSED;
   }
   public static void setElevatorController(ElevatorController controller){
      elevatorController = controller;
   }
   public int getElevatorNumber(){
      return elevatorID;
   }
   public void setStopRunning(){
      keepRunning = false;
   }
   public synchronized void setDestination(int floorNumber) {
      if(riders.isEmpty() && motionState == STOPPED){
         destinationList[floorNumber - 1] = true;
         activeElevator.interrupt();
      }else{
        destinationList[floorNumber - 1] = true;
      }
   }
   public synchronized void summonDestination(int floorNumber) throws ElevatorMovingException {
      if(getCurrentFloorNumber() != floorNumber || riders.isEmpty()){
         destinationList[floorNumber - 1] = true;
         activeElevator.interrupt();
      }else{
         throw new ElevatorMovingException();
      }
   }
   public synchronized void requestOpenDoor() throws ElevatorMovingException {
      if(motionState == STOPPED)
         requestDoorOpen = true;
      else
         throw new ElevatorMovingException();
   }
   public int getCurrentFloorNumber(){
      return currentFloorNumber;
   }
   public void start() {
     if(Simulator.debug) log = new Logger("Elevator" + elevatorID) ;
      keepRunning = true;
       if(activeElevator == null){
         activeElevator = new Thread(this);
         //activeElevator.setDaemon(true);
         activeElevator.setPriority(Thread.NORM_PRIORITY - 1);
         activeElevator.start();
      }
   } // end start        
   public void run() {  
       if(Simulator.debug) log.write("Starting elevator " + elevatorID);
        while(keepRunning){
           switch(motionState){
               case STOPPED:
                  if(Simulator.debug) log.write("Elevator " + elevatorID + " is stopped");
                  if(riders.isEmpty() && !isDestination()){
                     motionDirection = NO_DIRECTION;
                     if(Simulator.debug) log.write("Elevator " + elevatorID + " is empty and has no destination");
                     action(INACTIVE_TIME);
                  }else if(isArrived()){
                     if(Simulator.debug) log.write("Elevator " + elevatorID + " has arrived on " + currentFloorNumber);
                     openDoor();
                     closingDoor();
                     removeDestination();
                  }else{
                     if(Simulator.debug) log.write("Elevator " + elevatorID + " is continuing to travel");
                     travel();
                  }
                  break;
               case MOVING:
                  if(isArrived()){
                     stopElevator();
                  }else{
                     travel();
                  }
                  break;
           }
           if(Simulator.debug) log.write(getState().toString());
        }
        if(log != null)log.close();
   } // end run
   public void leaveElevator(Person person) throws DoorClosedException{
      if(doorState == DOOR_OPEN)
         riders.remove(person);
      else{
         if(Simulator.debug) log.write("Elevator " + elevatorID + " door is closed can not leave.");
         throw new DoorClosedException();
      }
   }
   public void enterElevator(Person person)throws ElevatorFullException, DoorClosedException{
      if(doorState == DOOR_OPEN){
         if(riders.size() < MAX_OCCUPANCY){
            riders.add(person);
         }else{
            if(Simulator.debug) log.write("Elevator " + elevatorID + " is full");
            throw new ElevatorFullException();
         }
      }else{
         if(Simulator.debug) log.write("Elevator " + elevatorID + " door is closed can not enter.");
         throw new DoorClosedException();
      }
   }
   public ElevatorState getState(){
      state.elevatorID = elevatorID;
      state.currentFloorNumber = currentFloorNumber;
      state.motionState = motionState;
      state.motionDirection = motionDirection;
      state.numberRiders = riders.size();
      state.doorState = doorState;
      StringBuffer sb = new StringBuffer();
      for(int i = 0; i < destinationList.length; i++){
         sb.append(destinationList[i] ? "1" : "0");
      }
      state.destinations = sb.toString();
      return state;
   }
   private void action(long time){
      try{
         activeElevator.sleep(time);
      }catch(InterruptedException ix){
         //intentionally left empty
      }
   }
   private synchronized boolean isArrived(){
      boolean returnValue = false;
      if(destinationList[currentFloorNumber - 1]){
         returnValue = true;
         motionState = STOPPED;
      }
      return returnValue;
   }
   private void moveUp(){
      if(isDestinationAbove()){
         if(currentFloorNumber != Building.MAX_FLOORS){
            if(Simulator.debug) log.write("move up moves up");
            action(FLOOR_TRAVEL_TIME);
            ++currentFloorNumber;
         }
      }else if(isDestinationBelow()){
         if(Simulator.debug) log.write("moving up is changing direction");
         motionDirection = MOVING_DOWN; //  someone missed floor change direction
      }else{
         if(Simulator.debug) log.write("move up is stopping");
         stopElevator(); // only destination is this floor
      }
   }
   private void moveDown(){
      if(isDestinationBelow()){
         if(currentFloorNumber != 1){
            if(Simulator.debug) log.write("move down moves down");
            action(FLOOR_TRAVEL_TIME);
            --currentFloorNumber;
         }
      }else if(isDestinationAbove()){
         if(Simulator.debug) log.write("move down is changing direction");
         motionDirection = MOVING_UP;  // someone missed floor change direction
      }else{
         if(Simulator.debug) log.write("move down is stopping");
         stopElevator(); // only destination is this flooor
      }
   }
   private void openDoor(){
      if(doorState == DOOR_CLOSED && motionState == STOPPED){
         doorState = DOOR_OPEN;
         notifyRiders();
         notifyController();
         action(FLOOR_WAIT_TIME);
      }
   }
   private void closingDoor(){
      do{
         resetDoorRequest();
         notifyRiders();
         notifyController();
         action(500);//give people a change to request door open
      }while(isRequestDoorOpen());
      doorState = DOOR_CLOSED;
   }
  // private synchronized void closeDoor(){
  //    doorState = DOOR_CLOSED;
  // }
   private synchronized void resetDoorRequest(){
      requestDoorOpen = false;
   }
   private synchronized boolean isRequestDoorOpen(){
      return requestDoorOpen;
   }
   private void notifyRiders(){
       synchronized(riders){
	   for(int i = 0; i < riders.size(); i++){
	       ((Person)riders.get(i)).attention();
	   }
       }
       
   }
   private void notifyController(){
      elevatorController.elevatorArrived(currentFloorNumber, this);
   }
   private synchronized void travel(){
      if(isDestination()){
         if(Simulator.debug) log.write("Elevator has a destination");
         motionState = MOVING;
         if(motionDirection == MOVING_UP){
            if(Simulator.debug) log.write("Moving up");
            moveUp();
         }else if(motionDirection == MOVING_DOWN){
            if(Simulator.debug) log.write("Moving Down");
            moveDown();
         }else if(isDestinationAbove()){
            if(Simulator.debug) log.write("Setting direction up");
            motionDirection = MOVING_UP;
            moveUp();
         }else if(isDestinationBelow()){
            if(Simulator.debug) log.write("Setting direction down");
            motionDirection = MOVING_DOWN;
            moveDown();
         }else{ //someone wants us where we are
            if(Simulator.debug) log.write("someone wants on this floor " + currentFloorNumber);
            stopElevator();
         }
      }else{ // no destination don't move;
         if(Simulator.debug) log.write("There is no destination");
         motionDirection = NO_DIRECTION;
         motionState = STOPPED;
         action(INACTIVE_TIME);
      }
   }
   private synchronized void removeDestination(){
      destinationList[currentFloorNumber - 1] = false;
   }
   private void stopElevator(){
      motionState = STOPPED;
   }
   private synchronized boolean isDestination(){
      boolean returnValue = false;
      for(int i = 0; i < destinationList.length; i++){
         if(destinationList[i]){
            returnValue = true;
            break;
         }
      }
      return returnValue;
   }
   private synchronized boolean isDestinationAbove(){
      boolean returnValue = false;
      for(int i = getCurrentFloorNumber(); i < destinationList.length; i++){
         if(destinationList[i]){
            returnValue = true;
            break;
         }
      }
      return returnValue;
   }
   private synchronized boolean isDestinationBelow(){
      boolean returnValue = false;
      for(int i = getCurrentFloorNumber() - 2; i >= 0; i--){
         if(destinationList[i]){
            returnValue = true;
            break;
         }
      }
      return returnValue;
   }
 } // end Elevator



