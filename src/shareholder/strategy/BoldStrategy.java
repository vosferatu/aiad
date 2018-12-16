package shareholder.strategy;

import market.behaviour.*;
import shareholder.ShareholderAgent;
import shareholder.behaviour.Holding;

import java.util.Map;
import java.util.LinkedList;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class BoldStrategy extends HolderStrategy {
  public BoldStrategy(ShareholderAgent agent, int agent_n) {
    super(agent, agent_n);
  }

  @Override
  public void initalStrategy() {
    ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);

    msg.addReceiver(this.agent.getMarket());
    msg.setSender(this.agent.getAID());

    for (Map.Entry<String, Holding> entry : this.agent.getHoldings().entrySet()) {
      Holding share  = entry.getValue();
      String  price  = this.genPrice(share.getLastBuyPrice(), 1.1, 1.33);
      String  amount = this.genAmount(share.getRealAmount(), 0.5, 0.8);

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
        double diff         = this.genPerc(0.15, 0.3);

        if (price_diff > 0) {
          int amount = (int)Math.ceil(order_amount * this.genPerc(0.5, 0.75)); // Small difference
          if (price_diff >= diff) {
            amount = order_amount;
          }
          return new BuyOrder(this.agent.getAID(), hold.getCompany(), order.getPrice(), amount);
        }
        else {
          int amount = (int)Math.ceil(order_amount * this.genPerc(0.5, 0.75));
          if (price_diff <= -diff) {
            amount = order_amount;
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
        double diff         = this.genPerc(0.15, 0.3);

        if (price_diff < 0) {
          int amount = (int)Math.ceil(order_amount * this.genPerc(0.5, 0.75));;
          if (price_diff <= diff) {
            amount = order_amount;
          }
          return new SellOrder(this.agent.getAID(), hold.getCompany(), order.getPrice(), amount);
        }
        return null;
      }
    }
  }
}
