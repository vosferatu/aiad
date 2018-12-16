package shareholder.behaviour;

import messages.*;
import market.behaviour.Order;
import market.StockMarketAgent;
import shareholder.ShareholderAgent;
import shareholder.strategy.HolderStrategy;

import java.util.HashSet;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.Behaviour;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

//Waits for a reply from the market, either confirming sell or buy
public class ListenOrderReplies extends Behaviour {
  MessageTemplate expected_msgs;
  ShareholderAgent agent;
  HolderStrategy strategy;


  public ListenOrderReplies(ShareholderAgent agent, HolderStrategy strategy) {
    super();
    this.agent    = agent;
    this.strategy = strategy;

    this.expected_msgs = MessageTemplate.MatchPerformative(ACLMessage.UNKNOWN);
  }

  public void action() {
    ACLMessage message = this.agent.blockingReceive(this.expected_msgs, 100);

    if (message != null) {
      int perf = message.getPerformative();
      if (perf == ACLMessage.INFORM) { //HashMap of sell orders
        try {
          ConcurrentHashMap<String, PriorityBlockingQueue<Order> > sell_orders = (ConcurrentHashMap<String, PriorityBlockingQueue<Order> >)message.getContentObject();
          // System.out.println("Agent '" + this.agent.getLocalName() + "' sell_orders = " + sell_orders);
        }
        catch (Exception e) {
          System.err.println("Failed to read sell orders?");
        }
      }
      else if (perf == ACLMessage.INFORM_IF) { //Hashmap of buy orders
        try {
          ConcurrentHashMap<String, PriorityBlockingQueue<Order> > buy_orders = (ConcurrentHashMap<String, PriorityBlockingQueue<Order> >)message.getContentObject();
          // System.out.println("Agent '" + this.agent.getLocalName() + "' buy_orders = " + buy_orders);
        }
        catch (Exception e) {
          System.err.println("Failed to read buy orders?");
        }
      }
      else if (perf == ACLMessage.REQUEST) { //Pair of orders
        try {
          SimpleEntry<ConcurrentHashMap<String, PriorityBlockingQueue<Order> >, ConcurrentHashMap<String, PriorityBlockingQueue<Order> > > orders = (SimpleEntry<ConcurrentHashMap<String, PriorityBlockingQueue<Order> >, ConcurrentHashMap<String, PriorityBlockingQueue<Order> > >)message.getContentObject();
          // System.out.println("Agent '" + this.agent.getLocalName() + "' orders = " + orders);
        }
        catch (Exception e) {
          System.err.println("Failed to read all orders?");
        }
      }
      else if (perf == ACLMessage.CONFIRM) { // HashSet of companies
        try {
          HashSet<String> companies = (HashSet<String>)message.getContentObject();
          // System.out.println("Agent '" + this.agent.getLocalName() + "' companies = " + companies);
        }
        catch (Exception e) {
          System.err.println("Failed to read companies list?");
        }
      }
      else { //Simple text content
        StockMessage msg = StockMessage.fromString(message.getContent());
        // System.out.println(this.agent.getLocalName() + " /|\\ Got '" + msg.toString() + "' from '" + message.getSender().getLocalName() + "'");

        if (msg.getType().equals(MessageBuilder.BOUGHT)) {
          this.agent.boughtShare(msg.getCompany(), msg.getPrice(), msg.getAmount());
        }
        else if (msg.getType().equals(MessageBuilder.SOLD)) {
          this.agent.soldShare(msg.getCompany(), msg.getPrice(), msg.getAmount());
        }
      }

      // this.agent.printHoldings();
    }
  }

  public boolean done() {
    return false;
  }
}
