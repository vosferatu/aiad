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
      String reply_with = msg.getReplyWith();
      if (reply_with != null) {
        ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
        reply.setInReplyTo(reply_with);
        reply.addReceiver(msg.getSender());
        this.agent.send(reply);
        System.out.println("    Sent reply");
      }
      else {
        System.out.println("Got msg: '" + msg.getContent() + "'");
      }
    }
  }

  public boolean done() {
    return false;
  }
}
