package shareholder.behaviour;

import market.StockMarketAgent;

import jade.lang.acl.ACLMessage;
import jade.core.behaviours.Behaviour;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class CheckMarketChanges extends Behaviour {
  @Override
  public void action() {
    System.out.println("Cenas");
  }

  @Override
  public boolean done() {
    return true;
  }
}
