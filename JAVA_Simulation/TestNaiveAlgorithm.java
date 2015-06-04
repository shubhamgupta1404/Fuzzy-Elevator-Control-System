import java.util.*;
/** Main class -- contains the main method to get program started 
 * Your Names Here
 * Quiz Section ID
 */

public class TestNaiveAlgorithm {
  
    public static void main(String[] args) {
      
      //specify algorithm -- here it is the naive algorithm
      NaiveElevatorAlgorithm algo = new NaiveElevatorAlgorithm(); 
      //create controller for this algorithm
      ElevatorController controller = new ElevatorController(algo);
      
      // run simulation
      controller.run();
      
      // get statistics about time length
      int waitingTime = controller.getTotalWaitingTime();
      // print out total waiting time
      controller.view("The total waiting time was :"+waitingTime);
      controller.delay(4);
      System.out.println("main() ends");
    }
}