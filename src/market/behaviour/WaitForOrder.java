package market.behaviour;

import market.StockMarketAgent;
import messages.*;

import jade.core.AID;
import java.util.Map;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.Behaviour;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;


public class WaitForOrder extends Behaviour {
  private StockMarketAgent agent;
  private ConcurrentHashMap<String, PriorityBlockingQueue<Order>> buy_orders;
  private ConcurrentHashMap<String, PriorityBlockingQueue<Order>> sell_orders;

  public WaitForOrder(StockMarketAgent agent, ConcurrentHashMap<String, PriorityBlockingQueue<Order>> buy_orders, ConcurrentHashMap<String, PriorityBlockingQueue<Order>> sell_orders) {
    this.agent = agent;
    this.buy_orders = buy_orders;
    this.sell_orders = sell_orders;
  }

  public void action() {
    ACLMessage msg = this.agent.blockingReceive();
    if (msg != null) {
      StockMessage message = StockMessage.fromString(msg.getContent());
      System.out.println("Got msg: '" + message + "' from '" + msg.getSender().getLocalName() + "'");

      if (message.getType().equals(MessageBuilder.SELL)) {
        // Check if a buy order meets requirements
        this.addOrder(message, msg.getSender(), this.sell_orders);
        this.printSellOrders();
      }
      else if (message.getType().equals(MessageBuilder.BUY)) {
        // Check if a sell order meets requirements 
        this.addOrder(message, msg.getSender(), this.buy_orders);
      }
    }
  }

  private void addOrder(StockMessage msg, AID sender, ConcurrentHashMap<String, PriorityBlockingQueue<Order>> company_orders) {
    PriorityBlockingQueue<Order> orders;

    if (!company_orders.containsKey(msg.getCompany())) {
      orders = new PriorityBlockingQueue<Order>();
      company_orders.put(msg.getCompany(), orders);
    }
    else {
      orders = company_orders.get(msg.getCompany());
    }

    orders.add(new Order(sender, msg.getCompany(), msg.getPrice(), msg.getAmount()));
  }

  public boolean done() {
    return false;
  }

  private void printSellOrders() {
    for (Map.Entry<String, PriorityBlockingQueue<Order>> entry : this.sell_orders.entrySet()) {
      System.out.println(entry.getKey());
      for (Order order : entry.getValue()) {
        System.out.println("  " + order.toString());
      }
    }
  }
}
