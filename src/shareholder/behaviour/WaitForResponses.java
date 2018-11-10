package shareholder.behaviour;

import market.StockMarketAgent;

import jade.lang.acl.ACLMessage;
import jade.core.behaviours.Behaviour;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

//Waits for a reply from the market, either confirming sell or buy
public class WaitForResponses extends Behaviour {

  public void action() {
    System.out.println("Waiting");
  }

  public boolean done() {
    return false;
  }
}
