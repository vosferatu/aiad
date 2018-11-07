import jade.core.Agent;
import jade.core.behaviours.Behaviour;

public class HelloAgent extends Agent {
  public static void main() {
    System.out.println("WTF?");
  }

  public void setup() {
    System.out.println("Hello World!");
  }

  public void takeDown() {
    System.out.println(this.getLocalName() + ": started working!");
  }

  class WorkingBehaviour extends Behaviour {
    private int n = 0;

    public void action() {
      this.n++;
      System.out.println("I'm doing something! " + this.n);
    }

    public boolean done() {
      return this.n == 3;
    }
  }
}
