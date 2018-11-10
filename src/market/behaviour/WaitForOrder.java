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
        this.sellOrder(message, msg.getSender());
        this.printSellOrders();
      }
    }
  }

  private void sellOrder(StockMessage msg, AID sender) {
    PriorityBlockingQueue<Order> orders;

    if (!this.sell_orders.containsKey(msg.getCompany())) {
      orders = new PriorityBlockingQueue<Order>();
      this.sell_orders.put(msg.getCompany(), orders);
    }
    else {
      orders = this.sell_orders.get(msg.getCompany());
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
