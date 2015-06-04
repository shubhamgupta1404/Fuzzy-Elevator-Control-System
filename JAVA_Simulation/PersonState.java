// Copyright (c) 2003 Webware Consulting
package newSim;

public class PersonState  {
   public int personNumber;
   public int activity;
   public int location;
   public int destination;
   public int elevatorNumber;
   public PersonState() {
   }
   public String toString(){
      StringBuffer sb = new StringBuffer();
      sb.append("Person number " + personNumber);
      switch(location){
         case Person.OUTSIDE:
            sb.append(" is outside");
            break;
         case Person.IN_ELEVATOR:
            sb.append(" is in elevator");
            break;
         default:
            sb.append(" is on floor " + location);
      }
      switch(activity){
         case Person.WAITING:
            sb.append(" and is waiting for an elevator.");
            break;
         case Person.WALKING_OUTSIDE:
            sb.append(" and is walking around.");
            break;
         case Person.RIDING:
            sb.append(" and is riding elevator ");
            sb.append(String.valueOf(elevatorNumber)+".");
            break;
         case Person.WORKING:
            sb.append(" and is working.");
            break;
         case Person.TAKING_STAIRS:
            sb.append(" and is taking the stairs.");
            break;
      }
      switch(destination){
         case Person.GOING_NOWHERE:
            sb.append(" The person is going nowhere.");
            break;
         default:
            sb.append(" The person is going to " + destination + ".");
      }
      return sb.toString();
   }
}