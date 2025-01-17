package market.behaviour;

import market.StockMarketAgent;
import messages.*;

import jade.core.AID;
import java.util.Map;
import java.util.HashSet;
import java.util.LinkedList;
import jade.lang.acl.ACLMessage;
import java.util.concurrent.TimeUnit;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.Behaviour;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;


public class WaitForOrder extends Behaviour {
  private StockMarketAgent agent;
  MessageTemplate expected_msgs;
  int agents_exited = 0;
  private ConcurrentHashMap<String, PriorityBlockingQueue<Order> > buy_orders;
  private ConcurrentHashMap<String, PriorityBlockingQueue<Order> > sell_orders;


  public WaitForOrder(StockMarketAgent agent, ConcurrentHashMap<String, PriorityBlockingQueue<Order> > buy_orders, ConcurrentHashMap<String, PriorityBlockingQueue<Order> > sell_orders) {
    this.agent         = agent;
    this.buy_orders    = buy_orders;
    this.sell_orders   = sell_orders;
    this.expected_msgs = MessageTemplate.MatchPerformative(ACLMessage.UNKNOWN);
  }

  public void action() {
    ACLMessage message = this.agent.blockingReceive(this.expected_msgs);

    if (message != null) {
      AID          sender = message.getSender();
      StockMessage msg    = StockMessage.fromString(message.getContent());
      if (msg == null) {
        return;
      }
      String msg_type = msg.getType();
      // System.out.println("MARKET /|\\ Got msg: '" + message.getContent() + "' from '" + sender.getLocalName() + "'");
      System.out.print(".");

      if (msg_type.equals(MessageBuilder.SELL)) {
        this.handleSellRequest(sender, msg.getCompany(), msg.getPrice(), msg.getAmount());
        String orders_str = this.printOrders("BUY", this.buy_orders);
        orders_str += this.printOrders("SELL", this.sell_orders);
        // System.out.println(orders_str);
      }
      else if (msg_type.equals(MessageBuilder.BUY)) {
        this.handleBuyRequest(sender, msg.getCompany(), msg.getPrice(), msg.getAmount());
        String orders_str = this.printOrders("BUY", this.buy_orders);
        orders_str += this.printOrders("SELL", this.sell_orders);
        // System.out.println(orders_str);
      }
      else if (msg_type.equals(MessageBuilder.SELL_ORDERS)) {
        this.handleSellOrdersRequest(sender);
      }
      else if (msg_type.equals(MessageBuilder.BUY_ORDERS)) {
        this.handleBuyOrdersRequest(sender);
      }
      else if (msg_type.equals(MessageBuilder.ORDERS)) {
        this.handleOrdersRequest(sender);
      }
      else if (msg_type.equals(MessageBuilder.COMPANIES)) {
        this.handleCompaniesRequest(sender);
      }
      else if (msg_type.equals("EXIT")) {
        this.agents_exited++;
        System.out.print(this.agents_exited);
      }
    }
    // System.out.println(" Exited ---------> " + this.agents_exited);
    if (this.agents_exited >= this.agent.getAgentsN()) {
      this.agent.takeDown();
    }
  }

  private void handleSellOrdersRequest(AID sender) {
    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

    try {
      reply.setContentObject(this.sell_orders);
      reply.addReceiver(sender);
      this.agent.send(reply);
    }
    catch (Exception e) {
      System.err.println("Failed to reply to sell_orders request!");
    }
  }

  private void handleBuyOrdersRequest(AID sender) {
    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

    try {
      reply.setContentObject(this.buy_orders);
      reply.addReceiver(sender);
      this.agent.send(reply);
    }
    catch (Exception e) {
      System.err.println("Failed to reply to buy_orders request");
    }
  }

  private void handleOrdersRequest(AID sender) {
    SimpleEntry<ConcurrentHashMap<String, PriorityBlockingQueue<Order> >, ConcurrentHashMap<String, PriorityBlockingQueue<Order> > > orders = new SimpleEntry(this.sell_orders, this.buy_orders);
    ACLMessage reply = new ACLMessage(ACLMessage.REQUEST);

    try {
      reply.setContentObject(orders);
      reply.addReceiver(sender);
      this.agent.send(reply);
    }
    catch (Exception e) {
      System.err.println("Failed to reply to orders request!");
    }
  }

  private void handleCompaniesRequest(AID sender) {
    HashSet<String> companies = this.getAllCompanies();
    ACLMessage      reply     = new ACLMessage(ACLMessage.CONFIRM);

    try {
      reply.setContentObject(companies);
      reply.addReceiver(sender);
      this.agent.send(reply);
    }
    catch (Exception e) {
      System.err.println("Failed to reply to companies request!");
    }
  }

  private synchronized void handleSellRequest(AID sender, String company, double price, int amount) {
    LinkedList<Order> compatibles = this.compatibleBuyOrder(company, price, amount);

    if (compatibles == null || compatibles.size() == 0) { // Add order to queue
      SellOrder order = new SellOrder(sender, company, price, amount);
      this.addOrder(company, this.sell_orders, order);
    }
    else { // Remove order from queue and warn buyer and seller
      int total_sold = 0;
      synchronized (compatibles) {
        for (Order order : compatibles) {
          if (order == null) {
            continue;
          }
          synchronized (order) {
            AID owner = order.getOwner();
            total_sold += order.getAmount();
            this.warnBothParties(owner, sender, company, order.getPrice(), order.getAmount());
          }
        }
        if (total_sold < amount) { //need to put a sell order with remaining amount
          SellOrder new_order = new SellOrder(sender, company, price, amount - total_sold);
          this.getOrdersOf(company, this.sell_orders).add(new_order);
        }
      }
    }
  }

  // Checks the buy orders to see if the highest is higher than proposed price
  // If an order is compatible it is returned
  private synchronized LinkedList<Order> compatibleBuyOrder(String company, double price, int amount) {
    PriorityBlockingQueue<Order> orders = this.buy_orders.get(company);

    LinkedList<Order> compatibles = new LinkedList<Order>();
    Order             highest;

    if (orders != null && (highest = orders.peek()) != null) {
      while (highest.getPrice() >= price && amount > 0) {
        int order_amount = highest.getAmount();
        if (order_amount > amount) {   //Final
          highest.subAmount(amount);
          compatibles.addLast(new BuyOrder(highest.getOwner(), highest.getCompany(), highest.getPrice(), amount));
          amount -= order_amount;
        }
        else if (order_amount <= amount) {
          try {
            Order comp_order = orders.poll(500, TimeUnit.MILLISECONDS);
            if (comp_order != null) {
              compatibles.addLast(comp_order);
              amount -= order_amount;
            }
            else {
              break;
            }
          }
          catch (Exception e) {}
        }
      }
      if (orders.peek() == null) {
        this.buy_orders.remove(company);
      }
      return compatibles;
    }
    return null;
  }

  private synchronized void handleBuyRequest(AID sender, String company, double price, int amount) {
    LinkedList<Order> compatibles = this.compatibleSellOrder(company, price, amount);

    if (compatibles == null || compatibles.size() == 0) {
      BuyOrder order = new BuyOrder(sender, company, price, amount);
      this.addOrder(company, this.buy_orders, order);
    }
    else { //Remove order from queue and warn buyer and seller
      int total_bought = 0;
      synchronized (compatibles) {
        for (Order order : compatibles) {
          if (order == null) {
            continue;
          }
          synchronized (order) {
            AID owner = order.getOwner();
            total_bought += order.getAmount();
            this.warnBothParties(sender, owner, company, order.getPrice(), order.getAmount());
          }
        }
        if (total_bought < amount) { //need to put a buy order with remaining amount
          BuyOrder new_order = new BuyOrder(sender, company, price, amount - total_bought);
          this.getOrdersOf(company, this.buy_orders).add(new_order);
        }
      }
    }
  }

  private LinkedList<Order> compatibleSellOrder(String company, double price, int amount) {
    PriorityBlockingQueue<Order> orders = this.sell_orders.get(company);

    LinkedList<Order> compatibles = new LinkedList<Order>();
    Order             lowest;

    if (orders != null && (lowest = orders.peek()) != null) {
      while (lowest.getPrice() <= price && amount > 0) {
        int order_amount = lowest.getAmount();
        if (order_amount > amount) {   //Final
          lowest.subAmount(amount);
          compatibles.addLast(new BuyOrder(lowest.getOwner(), lowest.getCompany(), lowest.getPrice(), amount));
          amount -= order_amount;
        }
        else if (order_amount <= amount) {
          try {
            Order comp_order = orders.poll(500, TimeUnit.MILLISECONDS);
            if (comp_order != null) {
              compatibles.addLast(comp_order);
              amount -= order_amount;
            }
            else {
              break;
            }
          }
          catch (Exception e) {}
        }
      }
      if (orders.peek() == null) {
        this.buy_orders.remove(company);
      }
      return compatibles;
    }
    return null;
  }

  private PriorityBlockingQueue<Order> getOrdersOf(String company, ConcurrentHashMap<String, PriorityBlockingQueue<Order> > company_orders) {
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

  private void addOrder(String company, ConcurrentHashMap<String, PriorityBlockingQueue<Order> > company_orders, Order order) {
    PriorityBlockingQueue<Order> orders = this.getOrdersOf(company, company_orders);
    boolean exists = false;

    for (Order curr_order : orders) {
      if (curr_order.equals(order)) {
        curr_order.addAmount(order.getAmount());
        exists = true;
        break;
      }
    }
    if (!exists) {
      orders.add(order);
    }
  }

  private void warnBothParties(AID bought_aid, AID sold_aid, String company, double price, int amount) {
    StockMessage bought     = MessageBuilder.boughtStockMsg(company, price, amount);
    StockMessage sold       = MessageBuilder.soldStockMsg(company, price, amount);
    ACLMessage   bought_msg = new ACLMessage(ACLMessage.UNKNOWN);
    ACLMessage   sold_msg   = new ACLMessage(ACLMessage.UNKNOWN);

    bought_msg.addReceiver(bought_aid);
    bought_msg.setContent(bought.toString());
    sold_msg.addReceiver(sold_aid);
    sold_msg.setContent(sold.toString());
    this.agent.send(bought_msg);
    this.agent.send(sold_msg);
  }

  public HashSet<String> getAllCompanies() {
    HashSet<String> companies = new HashSet<String>();

    synchronized (this.sell_orders) {
      for (String company : this.sell_orders.keySet()) {
        companies.add(company);
      }
    }
    synchronized (this.buy_orders) {
      for (String company : this.buy_orders.keySet()) {
        companies.add(company);
      }
    }
    return companies;
  }

  public boolean done() {
    return false;
  }

  private String printOrders(String start, ConcurrentHashMap<String, PriorityBlockingQueue<Order> > orders) {
    String final_str = " --- " + start + " ---  \n";

    for (Map.Entry<String, PriorityBlockingQueue<Order> > entry : orders.entrySet()) {
      final_str += entry.getKey() + "\n";
      for (Order order : entry.getValue()) {
        final_str += "  " + order.toString() + "\n";
      }
    }
    return final_str;
  }
}
