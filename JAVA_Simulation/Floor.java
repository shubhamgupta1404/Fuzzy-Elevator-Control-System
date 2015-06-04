// Copyright (c) 2003 Webware Consulting
package newSim;
import java.util.*;
public class Floor {
   private int floorNumber;
   private volatile boolean summonUp;
   private volatile boolean summonDown;
   private Vector upWaiting = new Vector(); // of type Person
   private Vector downWaiting = new Vector(); // of type Person
   private static ElevatorController elevatorController;
   private Logger log;
   public Floor(int floorNumber){
      this.floorNumber = floorNumber;
      if(Simulator.debug) log = new Logger("Floor" + this.floorNumber);
   }
   public void closeLog(){
      if(log != null)log.close();
   }
   public boolean isSummonUp(){
      return summonUp;
   }
   public boolean isSummonDown(){
      return summonDown;
   }
   public static void setElevatorController(ElevatorController controller){
      elevatorController = controller;
   }
   public int getFloorNumber(){
      return floorNumber;
   }
   public int getNumberWaitingUp(){
      return upWaiting.size();
   }
   public int getNumberWaitingDown(){
      return downWaiting.size();
   }
   public void summonElevatorUp(Person person) {
      if(Simulator.debug) log.write("Summon up for person " + person.getPersonNumber());
      upWaiting.add(person);
      if(!summonUp){//if already summoned no need to do it again
         if(Simulator.debug) log.write("Light off summon UP for person " + person.getPersonNumber());
         elevatorController.summonElevatorUp(floorNumber, person);
         summonUp = true;
      }
   }
   public void summonElevatorDown(Person person) {
      if(Simulator.debug) log.write("Summon down for person " + person.getPersonNumber());
      downWaiting.add(person);
      if(!summonDown){ // id already summoned no need to do it again
         if(Simulator.debug) log.write("Light off summon DOWN for person " + person.getPersonNumber());
         elevatorController.summonElevatorDown(floorNumber, person);
         summonDown = true;
      }
   }      
   public void  elevatorArrivedUp(Elevator elevator) {
      Person p = null;
      summonUp = false;
      synchronized(upWaiting){
	  for(int i = 0; i < upWaiting.size(); i++){
	      p = (Person)upWaiting.get(i);
	      p.elevatorArrived(elevator);
	      p.attention();
	  }
      }
      if(Simulator.debug) log.write("Elevator " + elevator.getElevatorNumber() + " has arrived UP on " + getFloorNumber());
   } // end elevatorArrived
   public void elevatorArrivedDown(Elevator elevator){
      Person p = null;
      summonDown = false;
      synchronized(downWaiting){
	  for(int i = 0; i < downWaiting.size(); i++){
	      p = (Person)downWaiting.get(i);
	      p.elevatorArrived(elevator);
	      p.attention();
	  }
      }
      if(Simulator.debug) log.write("Elevator " + elevator.getElevatorNumber() + " has arrived DOWN on " + getFloorNumber());
   }
   public void stopWaiting(Person person) {
      if(Simulator.debug) log.write("Person " + person.getPersonNumber() + "  has stopped waiting on " + getFloorNumber());
      upWaiting.remove(person);
      downWaiting.remove(person);
   } // end giveUp        

} // end Floor



