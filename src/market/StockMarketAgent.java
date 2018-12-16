package market;

import market.behaviour.*;

import jade.core.Agent;
import jade.content.lang.Codec;
import jade.lang.acl.ACLMessage;
import jade.content.onto.Ontology;
import jade.content.lang.sl.SLCodec;
import jade.core.behaviours.Behaviour;
import jade.content.onto.basic.Action;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import jade.domain.JADEAgentManagement.ShutdownPlatform;
import jade.domain.JADEAgentManagement.JADEManagementOntology;

public class StockMarketAgent extends Agent {
  int agents_n;

  private ConcurrentHashMap<String, PriorityBlockingQueue<Order> > buy_orders;
  private ConcurrentHashMap<String, PriorityBlockingQueue<Order> > sell_orders;

  public void setup() {
    super.setup();
    this.buy_orders  = new ConcurrentHashMap<String, PriorityBlockingQueue<Order> >();
    this.sell_orders = new ConcurrentHashMap<String, PriorityBlockingQueue<Order> >();
    this.agents_n    = Integer.valueOf((String)this.getArguments()[0]);

    // Maybe initialize the orders with an external file?
    this.addBehaviour(new WaitForOrder(this, this.buy_orders, this.sell_orders));
  }

  public void takeDown() {
    System.out.println("Market has been taken down!");
    try {
      this.getContainerController().kill();
      Codec    codec = new SLCodec();
      Ontology jmo   = JADEManagementOntology.getInstance();
      this.getContentManager().registerLanguage(codec);
      this.getContentManager().registerOntology(jmo);
      ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
      msg.addReceiver(this.getAMS());
      msg.setLanguage(codec.getName());
      msg.setOntology(jmo.getName());
      this.getContentManager().fillContent(msg, new Action(getAID(), new ShutdownPlatform()));
      this.send(msg);
    }
    catch (Exception e) {}
  }

  public int getAgentsN() {
    return this.agents_n;
  }
}
