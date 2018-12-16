package shareholder.strategy;

import market.behaviour.Order;
import shareholder.ShareholderAgent;
import shareholder.behaviour.Holding;

import java.util.Map;
import java.util.Random;
import java.util.LinkedList;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public abstract class HolderStrategy {
  private final String[] companies = { "intel", "amd", "nvidia", "asus", "samsung", "lenovo", "nos", "nowo", "cabovisao", "vodafone", "inesctec", "inegi", "blip", "mindera", "github", "microsoft", "mozilla", "ford", "volvo", "renault" };
  ShareholderAgent agent;
  Random rand = new Random();


  HolderStrategy(ShareholderAgent agent, int agent_n) {
    this.agent = agent;
    this.setupEnvironment(agent_n);
  }

  private void setupEnvironment(int agent_n) {
    int    companies_n  = this.companies.length;
    Random rand         = new Random();
    int    num_holdings = agent_n - rand.nextInt((int)Math.ceil(agent_n * 0.25));

    this.agent.setMoney((double)(150.0 + rand.nextDouble() * 150 * agent_n * 0.5));
    for (int i = 0; i < num_holdings; i++) {
      String company       = this.companies[rand.nextInt((agent_n > companies_n ? this.companies.length : agent_n))];
      int    shares_n      = agent_n + rand.nextInt((int)Math.ceil(agent_n * 0.33));
      double initial_price = 5.0 + rand.nextDouble() * 20.0;
      this.agent.addHolding(company, new Holding(company, initial_price, shares_n));
    }
  }

  abstract public void initalStrategy();

  abstract Order handleSellOrder(PriorityBlockingQueue<Order> orders, Holding hold);

  abstract Order handleBuyOrder(PriorityBlockingQueue<Order> orders, Holding hold);

  public synchronized LinkedList<Order> traverseSellOrders(ConcurrentHashMap<String, PriorityBlockingQueue<Order> > sell_orders) {
    double            money_left = this.agent.getMoney();
    LinkedList<Order> result     = new LinkedList<Order>();

    for (Map.Entry<String, PriorityBlockingQueue<Order> > entry : sell_orders.entrySet()) {
      Holding hold = this.agent.getHolding(entry.getKey());
      PriorityBlockingQueue<Order> orders = entry.getValue();

      if (hold != null && orders != null) {
        Order order;
        if ((order = this.handleSellOrder(orders, hold)) != null) {
          result.addFirst(order);
        }
      }
    }

    return result;
  }

  public synchronized LinkedList<Order> traverseBuyOrders(ConcurrentHashMap<String, PriorityBlockingQueue<Order> > buy_orders) {
    LinkedList<Order> result = new LinkedList<Order>();

    for (Map.Entry<String, PriorityBlockingQueue<Order> > entry : buy_orders.entrySet()) {
      Holding hold = this.agent.getHolding(entry.getKey());
      PriorityBlockingQueue<Order> orders = entry.getValue();

      if (hold != null && orders != null) {
        Order order;
        if ((order = this.handleBuyOrder(orders, hold)) != null) {
          result.addFirst(order);
        }
      }
    }

    return result;
  }

  String genPrice(double price, double min_diff, double max_diff) {
    double price_diff = min_diff + rand.nextDouble() * (max_diff - min_diff);

    return String.format("%.2f", price * price_diff);
  }

  String genAmount(int amount, double min_p, double max_p) {
    int final_amount = (int)Math.ceil(amount * min_p + rand.nextDouble() * (max_p - min_p));

    return String.valueOf(final_amount);
  }

  double genPerc(double min, double max) {
    return min + rand.nextDouble() * (max - min);
  }

  double priceDiff(double my_price, double theirs_price) {
    return (my_price - theirs_price) / my_price;
  }
}
