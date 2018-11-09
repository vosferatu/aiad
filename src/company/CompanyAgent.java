package company;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

public class CompanyAgent extends Agent {
  public void setup() {
    System.out.println("Hello World!");
    this.addBehaviour(new WorkingBehaviour());
  }

  public void takeDown() {
    System.out.println(this.getLocalName() + ": Exited!");
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
