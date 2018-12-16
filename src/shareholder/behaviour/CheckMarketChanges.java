package shareholder.behaviour;

import market.behaviour.Order;
import shareholder.ShareholderAgent;
import shareholder.strategy.HolderStrategy;

import java.util.Map;
import java.util.Random;
import java.util.LinkedList;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.AbstractMap.SimpleEntry;
import jade.core.behaviours.TickerBehaviour;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class CheckMarketChanges extends TickerBehaviour {
  ShareholderAgent agent;
  HolderStrategy strategy;
  MessageTemplate obj_message;
  ConcurrentHashMap<String, PriorityBlockingQueue<Order> > buy_orders  = null;
  ConcurrentHashMap<String, PriorityBlockingQueue<Order> > sell_orders = null;
  int stagnant = 0;

  public CheckMarketChanges(ShareholderAgent a, long period, HolderStrategy strat) {
    super(a, period); //Checks market changes every [1, 2] seconds
    this.setFixedPeriod(true);
    this.agent    = a;
    this.strategy = strat;
    MessageTemplate inform = MessageTemplate.MatchPerformative(ACLMessage.INFORM),
        inform_if          = MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF),
        request            = MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
        companies          = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
    this.obj_message = MessageTemplate.or(MessageTemplate.or(inform, inform_if),
            MessageTemplate.or(request, companies));
  }

  @Override
  protected void onTick() {
    ACLMessage orders_request = new ACLMessage(ACLMessage.UNKNOWN);

    orders_request.addReceiver(this.agent.getMarket());
    orders_request.setContent("ORDERS");
    this.agent.send(orders_request);
    ACLMessage orders_reply = this.agent.blockingReceive(this.obj_message, 5000);

    if (orders_reply != null) {
      this.extractObject(orders_reply);
      if (this.buy_orders != null && this.sell_orders != null) {
        LinkedList<Order> orders = this.strategy.traverseSellOrders(this.sell_orders);
        orders.addAll(this.strategy.traverseBuyOrders(this.buy_orders));

        this.agent.handleOrders(orders);
      }
    }
  }

  private void extractObject(ACLMessage orders_reply) {
    SimpleEntry<ConcurrentHashMap<String, PriorityBlockingQueue<Order> >, ConcurrentHashMap<String, PriorityBlockingQueue<Order> > > orders;

    try {
      orders = (SimpleEntry<ConcurrentHashMap<String, PriorityBlockingQueue<Order> >, ConcurrentHashMap<String, PriorityBlockingQueue<Order> > >)orders_reply.getContentObject();
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
      return;
    }

    ConcurrentHashMap<String, PriorityBlockingQueue<Order> > new_sell_order = orders.getKey(),
        new_buy_order = orders.getValue();


    if (this.sameCollection(new_sell_order, this.sell_orders) && this.sameCollection(new_buy_order, this.buy_orders)) {
      this.stagnant++;
      if (this.stagnant >= 3) { // Market has gone stagnant, finish program
        ACLMessage exit_msg = new ACLMessage(ACLMessage.UNKNOWN);
        exit_msg.addReceiver(this.agent.getMarket());
        exit_msg.setContent("EXIT");
        this.agent.send(exit_msg);
        this.agent.doDelete();
        this.stop();
      }
    }
    else {
      this.stagnant = 0;
    }
    this.sell_orders = new_sell_order;
    this.buy_orders  = new_buy_order;
  }

  private boolean sameCollection(ConcurrentHashMap<String, PriorityBlockingQueue<Order> > map1, ConcurrentHashMap<String, PriorityBlockingQueue<Order> > map2) {
    if (map1 == null && map2 == null) {
      return true;
    }
    if (map1 == null || map2 == null) {
      return false;
    }
    for (Map.Entry<String, PriorityBlockingQueue<Order> > order1 : map1.entrySet()) {
      if (map2.containsKey(order1.getKey())) {
        PriorityBlockingQueue<Order> orders1 = order1.getValue();
        PriorityBlockingQueue<Order> orders2 = map2.get(order1.getKey());
        if (!orders1.toString().equals(orders2.toString())) {
          return false;
        }
      }
      else {
        return false;
      }
    }

    return true;
  }
}
