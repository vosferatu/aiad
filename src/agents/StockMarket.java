package agents;

import behaviour.Order;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class StockMarket extends Agent {
  private ConcurrentHashMap<String, PriorityBlockingQueue<Order>> orders;

  public void setup() {
    super.setup();
    System.out.println("Initializing stock market");


  }


}
