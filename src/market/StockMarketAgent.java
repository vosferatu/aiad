package market;

import market.behaviour.*;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class StockMarketAgent extends Agent {
  private ConcurrentHashMap<String, PriorityBlockingQueue<Order>> buy_orders;
  private ConcurrentHashMap<String, PriorityBlockingQueue<Order>> sell_orders;


  public void setup() {
    super.setup();
    this.buy_orders = new ConcurrentHashMap<String, PriorityBlockingQueue<Order>>();
    this.sell_orders = new ConcurrentHashMap<String, PriorityBlockingQueue<Order>>();
    System.out.println("Initializing stock market");
    // Maybe initialize the orders with an external file?

    this.addBehaviour(new WaitForOrder(this, this.buy_orders, this.sell_orders));
  }


  public void takeDown() {
    System.out.println("Agent '" + this.getLocalName() + "' taken down!");
  }


}
