import uwcse.graphics.*;
import java.awt.Color;
import java.util.*;

/** Class for depicting what is happening inside the elevator system
 * YOU SHOULD NOT NEED TO MODIFY THIS CLASS */
public class Viewer {
  
  // class-wide variables
  final static int height = 500;  //height of the graphical window
  final static int width = 500;   //width of the graphical window
  // instance variables
  private GWindow building;     // Graphical Window for displaying the floor
  private int floorCount;       //number of floors in the building
  private int maxPeopleCount;   //maximum number of people allowed
  
  private ArrayList humanShape;       //human shapes - currently ovals
  private ArrayList targetFloor;      //the strings for each person -- showing the target floor
  
  private int spacing;             //spacing between the floors
  private Rectangle elevator;      //rectangle for the elevator 
  private TextShape elevatorText;  //the text which appears inside the elevator
  private TextShape time;          //the time text
  private TextShape message;       //the message displayed on the top of the screen
  
  private int x1,y1,x2,y2,radius;  //variables to keep an account of various drawings on the window
  
  private int peopleDisplayCount;  //number of people currently being displayed
  
  /** constructor of the class Viewer  - takes as parameter the number of floors and
   * the maximum number of people allowed*/ 
  public Viewer(int floorCount, int maxPeopleCount) {
    this.floorCount = floorCount;
    building = new GWindow(width,height);
    this.maxPeopleCount = maxPeopleCount;
    peopleDisplayCount = 0;
    drawBasic();
  }
  
  /** Draw the basic things on the window */
  public void drawBasic() {
    radius = 15;
    //oval shapes for humans
    humanShape = new ArrayList();
    targetFloor = new ArrayList();
    for(int i=0; i<maxPeopleCount; i++) {
      humanShape.add(new Oval(0,0,radius,radius,Color.yellow,true));
      targetFloor.add(new TextShape("1",0,0));
    } 
    
    //the floors
    x1=50; y1=50; y2=450; x2=250;
    spacing = (y2-y1)/floorCount;
    Line shaft = new Line(x1,y1,x1,y2,Color.black);
    shaft.addTo(building); 
    for(int i=0; i<floorCount; i++) {
      Line floorLine = new Line(x1,y2-i*spacing,x2,y2-i*spacing);
      floorLine.addTo(building);
    }
    
    //the elevator
    int elevatorX = x2;
    int elevatorY = y2-spacing/2;
    elevator = new Rectangle(elevatorX,elevatorY,100,spacing/2,Color.red,false);
    elevator.addTo(building);
    elevatorText = new TextShape("People Inside: 0",elevatorX+5,elevatorY+20); 
    elevatorText.addTo(building);
    
    //the message on the top
    message = new TextShape("Elevator Starting",100,20);
    message.addTo(building);
    
    //the clock
    TextShape string=new TextShape("Clock",400,y1);
    time = new TextShape("0",420,y1+20);
    string.addTo(building);
    time.addTo(building);
  }   
  
  /** Redraw the parts that need to be redrawn. Takes a lot of arguments, 
   * to display the stateof the system 
   * */
  public void redraw(int clockTick, int elevatorFloor, ArrayList floors, ArrayList elevatorList, String msg) {
    int elevatorX = x2;
    int elevatorY = y2-elevatorFloor*spacing+spacing/2;
    elevator.moveTo(elevatorX,elevatorY);
    elevatorText.moveTo(elevatorX+5,elevatorY+20);
    elevatorText.setText("People Inside: "+elevatorList.size());
    System.out.println(msg);
    message.setText(msg);
    for(int i=0; i<maxPeopleCount; i++) {
      ((Oval)humanShape.get(i)).removeFromWindow();   
      ((TextShape)targetFloor.get(i)).removeFromWindow();
      peopleDisplayCount = 0;
    }
    
    for(int i=1; i<=floorCount; i++) {
      ArrayList people = (ArrayList)(floors.get(i));
      drawPeople(x2-2*radius,y2-(i-1)*spacing-2*radius,people);
    }  
    time.setText(""+clockTick);
  }     
  
  /** draw the people on a floor - takes as argument the x and y co-ordinates from
   * where the drawing of people starts and the list of people to be drawn*/
  public void drawPeople(int x, int y, ArrayList people) {
    int posx = x;
    int posy = y;
    int size = people.size();
    for(int i=0; i<size; i++) {
      Person next = (Person)people.get(i);
      peopleDisplayCount++;
      
      Oval shape = (Oval)humanShape.get(peopleDisplayCount);
      TextShape text = (TextShape)targetFloor.get(peopleDisplayCount);
      
      //display the next person at the appropriate position
      shape.moveTo(posx,posy);
      shape.addTo(building);
      
      //display the target floor the current human being displayed
      text.moveTo(posx+radius/3,posy);
      text.setText(""+next.getTargetFloor());
      text.addTo(building);
      
      posx = posx-2*radius;
    }       
  }
}