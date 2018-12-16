package shareholder.strategy;

import market.behaviour.*;
import shareholder.ShareholderAgent;
import shareholder.behaviour.Holding;

import java.util.Map;
import java.util.Random;
import java.util.LinkedList;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class AloofStrategy extends HolderStrategy {
  Random rand = new Random();

  public AloofStrategy(ShareholderAgent agent, int agent_n) {
    super(agent, agent_n);
  }

  @Override
  public void initalStrategy() {
    ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);

    msg.addReceiver(this.agent.getMarket());
    msg.setSender(this.agent.getAID());

    for (Map.Entry<String, Holding> entry : this.agent.getHoldings().entrySet()) {
      Holding share  = entry.getValue();
      String  price  = this.genPrice(share.getLastBuyPrice(), 0.9, 1.1);
      String  amount = this.genAmount(share.getRealAmount(), 0.25, 0.75);

      msg.setContent("SELL;" + entry.getKey() + ";" + price + ";" + amount);
      this.agent.send(msg);
    }
  }

  @Override
  Order handleSellOrder(PriorityBlockingQueue<Order> orders, Holding hold) {
    synchronized (orders) {
      Order order = orders.peek();
      if (order == null) {
        return null;
      }
      synchronized (order) {
        int    order_amount = order.getAmount();
        double price_diff   = this.priceDiff(hold.getLastBuyPrice(), order.getPrice());
        double diff         = this.genPerc(0.0, 0.3);

        if (price_diff > 0) {
          int amount = (int)Math.ceil(order_amount * this.genPerc(0.1, 0.25)); // Small difference
          if (price_diff >= diff) {
            amount = (int)Math.ceil(order_amount * this.genPerc(0.25, 0.75));
          }
          return new BuyOrder(this.agent.getAID(), hold.getCompany(), order.getPrice(), amount);
        }
        else {
          int amount = (int)Math.ceil(order_amount * this.genPerc(0.1, 0.25));
          if (price_diff <= -diff) {
            amount = (int)Math.ceil(order_amount * this.genPerc(0.25, 0.75));
          }
          return new SellOrder(this.agent.getAID(), hold.getCompany(), order.getPrice(), amount);
        }
      }
    }
  }

  @Override
  Order handleBuyOrder(PriorityBlockingQueue<Order> orders, Holding hold) {
    synchronized (orders) {
      Order order = orders.peek();
      if (order == null) {
        return null;
      }
      synchronized (order) {
        int    order_amount = order.getAmount();
        double price_diff   = this.priceDiff(hold.getLastSellPrice(), order.getPrice());
        double diff         = this.genPerc(0.0, 0.3);

        if (price_diff < 0) {
          int amount = (int)Math.ceil(order_amount * this.genPerc(0.1, 0.25));;
          if (price_diff <= diff) {
            amount = (int)Math.ceil(order_amount * this.genPerc(0.25, 0.75));
          }
          return new SellOrder(this.agent.getAID(), hold.getCompany(), order.getPrice(), amount);
        }
        return null;
      }
    }
  }
}
