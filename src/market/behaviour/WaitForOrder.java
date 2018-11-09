package market.behaviour;

import market.StockMarketAgent;

import jade.lang.acl.ACLMessage;
import jade.core.behaviours.Behaviour;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;


public class WaitForOrder extends Behaviour {
  private StockMarketAgent agent;
  private ConcurrentHashMap<String, PriorityBlockingQueue<Order>> stocks;

  public WaitForOrder(StockMarketAgent agent, ConcurrentHashMap<String, PriorityBlockingQueue<Order>> stocks) {
    this.agent = agent;
    this.stocks = stocks;
  }

  public void action() {
    ACLMessage msg = this.agent.blockingReceive();
    if (msg != null) {
      System.out.println("Got msg: '" + msg.getContent() + "'");
      // Do something with message
    }
  }

  public boolean done() {
    return false;
  }

}
