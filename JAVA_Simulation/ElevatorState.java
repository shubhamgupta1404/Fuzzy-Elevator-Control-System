// Copyright (c) 2003 Webware Consulting
package newSim;

public class ElevatorState  {
   public int elevatorID;
   public int motionState;
   public int motionDirection;
   public int currentFloorNumber;
   public int numberRiders;
   public int doorState;
   public String destinations;
   public String toString(){
      StringBuffer sb = new StringBuffer();
      sb.append("Elevator number " + elevatorID + " is on floor " + currentFloorNumber);
      switch(motionDirection){
         case Elevator.MOVING_UP:
            sb.append(" moving up ");
            break;
         case Elevator.MOVING_DOWN:
            sb.append(" moving down " );
            break;
         case Elevator.NO_DIRECTION:
            sb.append(" no direction ");
            break;
      }
      switch(motionState){
         case Elevator.MOVING:
            sb.append("the elevator is currently moving.");
            break;
         case Elevator.STOPPED:
            sb.append("the elevator is currently stopped.");
            break;
      }
      sb.append("  There are " + numberRiders + " riders.");
      switch(doorState){
         case Elevator.DOOR_OPEN:
            sb.append(" The door is open.");
            break;
         case Elevator.DOOR_CLOSED:
            sb.append(" The door is closed.");
            break;
      }
      sb.append(" Destinations " + destinations);
      return sb.toString();
   }
}