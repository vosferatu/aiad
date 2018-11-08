package behaviour;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.Behaviour;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;


public class WaitForOrder extends Behaviour {

  private ConcurrentHashMap<String, PriorityBlockingQueue<Order>> stocks;

  public WaitForOrder(ConcurrentHashMap<String, PriorityBlockingQueue<Order>> stocks) {
    this.stocks = stocks;
  }

  public void action() {
    ACLMessage msg = this.getAgent().blockingReceive();
    if (msg != null) {
      Agent agent = this.getAgent();
      System.out.println(msg);
      // Do something with message
    }
  }

  public boolean done() {
    return false;
  }

}
