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
    ACLMessage message = this.agent.blockingReceive();

    if (message != null && message.getPerformative() == ACLMessage.UNKNOWN) {
      AID sender = message.getSender();
      StockMessage msg = StockMessage.fromString(message.getContent());
      System.out.println("Got msg: '" + message + "' from '" + sender.getLocalName() + "'");

      if (msg.getType().equals(MessageBuilder.SELL)) {
        this.handleSellRequest(sender, msg.getCompany(), msg.getPrice(), msg.getAmount());
      }
      else if (msg.getType().equals(MessageBuilder.BUY)) {
        this.handleBuyRequest(sender, msg.getCompany(), msg.getPrice(), msg.getAmount());
      }
    }
  }

  private PriorityBlockingQueue<Order> getOrdersOf(String company, ConcurrentHashMap<String, PriorityBlockingQueue<Order>> company_orders) {
    PriorityBlockingQueue<Order> orders;

    if (!company_orders.containsKey(company)) {
      orders = new PriorityBlockingQueue<Order>();
      company_orders.put(company, orders);
    }
    else {
      orders = company_orders.get(company);
    }
    return orders;
  }

  private void handleSellRequest(AID sender, String company, double price, int amount) {
    BuyOrder compatible = (BuyOrder)this.compatibleBuyOrder(company, price, amount);
    if (compatible == null) { // Add order to queue
      SellOrder order = new SellOrder(sender, company, price, amount);
      this.getOrdersOf(company, this.sell_orders).add(order);
      this.printOrders("SELL", this.sell_orders);
    }
    else { // Remove order from queue and warn buyer and seller
      AID owner = compatible.getOwner();
      this.warnBothParties(sender, owner, company, price, amount);
    }
  }

  // Checks the buy orders to see if the highest is higher than proposed price
  // If an order is compatible it is returned
  private synchronized Order compatibleBuyOrder(String company, double price, int amount) {
    PriorityBlockingQueue<Order> orders = this.buy_orders.get(company);
    if (orders != null) {
      Order highest = orders.peek();
      if (highest.getPrice() >= price) {
        try {
          return orders.take();
        }
        catch (Exception e) {
          return null;
        }
      }
    }
    return null;
  }


  private void handleBuyRequest(AID sender, String company, double price, int amount) {
    SellOrder compatible = (SellOrder)this.compatibleSellOrder(company, price, amount);
    if (compatible == null) {
      BuyOrder order = new BuyOrder(sender, company, price, amount);
      this.getOrdersOf(company, this.buy_orders).add(order);
      this.printOrders("BUY", this.sell_orders);
    }
    else { //Remove order from queue and warn buyer and seller
      AID owner = compatible.getOwner();
      this.warnBothParties(owner, sender, company, price, amount);
    }
  }

  private synchronized Order compatibleSellOrder(String company, double price, int amount) {
    PriorityBlockingQueue<Order> orders = this.sell_orders.get(company);
    if (orders != null) {
      Order lowest = orders.peek();
      if (lowest.getPrice() <= price) {
        try {
          return orders.take();
        }
        catch (Exception e) {
          return null;
        }
      }
    }
    return null;
  }

  private void warnBothParties(AID bought_aid, AID sold_aid, String company, double price, int amount) {
    StockMessage bought = MessageBuilder.boughtStockMsg(company, price, amount);
    StockMessage sold = MessageBuilder.soldStockMsg(company, price, amount);
    ACLMessage bought_msg = new ACLMessage(ACLMessage.UNKNOWN);
    ACLMessage sold_msg = new ACLMessage(ACLMessage.UNKNOWN);
    bought_msg.addReceiver(bought_aid);
    bought_msg.setContent(bought.toString());
    sold_msg.addReceiver(sold_aid);
    sold_msg.setContent(sold.toString());
    this.agent.send(bought_msg);
    this.agent.send(sold_msg);
  }

  public boolean done() {
    return false;
  }

  private void printOrders(String start, ConcurrentHashMap<String, PriorityBlockingQueue<Order>> orders) {
    System.out.println(" --- " + start + " ---  ");
    for (Map.Entry<String, PriorityBlockingQueue<Order>> entry : orders.entrySet()) {
      System.out.println(entry.getKey());
      for (Order order : entry.getValue()) {
        System.out.println("  " + order.toString());
      }
    }
  }
}
