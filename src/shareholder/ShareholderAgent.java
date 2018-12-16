package shareholder;

import messages.*;
import market.behaviour.*;
import shareholder.strategy.*;
import shareholder.behaviour.*;


import jade.core.AID;
import java.util.Map;
import jade.core.Agent;
import java.util.Random;
import java.util.Map.Entry;
import java.util.LinkedList;
import jade.domain.AMSService;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.Behaviour;
import java.util.concurrent.ConcurrentHashMap;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;

//Every agent starts with holdings in [3, 5] companies with [10, 20] shares of initial value between [5.0, 20.0]
public class ShareholderAgent extends Agent {
  private static final String MARKET_NAME = "market";
  private AID market;
  private double money;
  private double usable_money;
  // Company_Name -> Share
  private ConcurrentHashMap<String, Holding> holdings = new ConcurrentHashMap<String, Holding>();

  private static final int ALOOF     = 0;
  private static final int BOLD      = 1;
  private static final int FRIGHTFUL = 2;


  public void setup() {
    Object[] args = this.getArguments();
    int      strategy_t = Integer.parseInt((String)args[1]), agents_n = Integer.parseInt((String)args[0]);
    Random   rand = new Random();
    this.usable_money = (this.money = (10 + rand.nextInt(20)) * agents_n);
    this.market       = new AID(MARKET_NAME, false);

    HolderStrategy strat = this.chooseStrategy(agents_n, strategy_t);
    this.printHoldings();

    strat.initalStrategy();
    this.addBehaviour(new ListenOrderReplies(this, strat));
    this.addBehaviour(new CheckMarketChanges(this, 2000 + rand.nextInt(200), strat));
  }

  HolderStrategy chooseStrategy(int agents_n, int strategy_t) {
    if (strategy_t == ALOOF) {
      return new AloofStrategy(this, agents_n);
    }
    else if (strategy_t == BOLD) {
      return new BoldStrategy(this, agents_n);
    }
    else if (strategy_t == FRIGHTFUL) {
      return new FrightfulStrategy(this, agents_n);
    }
    else {
      System.err.println("UNKOWN strategy selected!! (" + strategy_t + ")");
      System.exit(1);
      return null;
    }
  }

  public void boughtShare(String company, double price, int amount) {
    if (!this.holdings.containsKey(company)) {
      Holding hold = new Holding(company, price, amount);
      this.holdings.put(company, hold);
    }
    else {
      Holding hold        = this.holdings.get(company);
      double  money_spent = hold.buy(price, amount);
      this.money -= money_spent;
    }
  }

  public void soldShare(String company, double price, int amount) {
    if (this.holdings.containsKey(company)) {
      Holding hold      = this.holdings.get(company);
      double  new_money = hold.sell(price, amount);
      this.money        += new_money;
      this.usable_money += new_money;
      if (hold.getRealAmount() <= 0) {
        this.holdings.remove(company);
      }
    }
    else {
      System.err.println("WTF?");
    }
  }

  public void handleOrders(LinkedList<Order> orders) {
    ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);

    msg.addReceiver(this.market);
    for (Order order : orders) {
      if (this.canBeExecuted(order)) {
        msg.setContent(order.toMsg());
        double total_price = order.getTotal();

        if (order instanceof BuyOrder && this.usable_money >= total_price) {
          this.usable_money -= total_price;
        }
        this.send(msg);
      }
    }
  }

  boolean canBeExecuted(Order order) {
    if (order instanceof BuyOrder) {
      double total_price = order.getTotal();
      if (this.usable_money >= total_price) {
        this.usable_money -= total_price;
        return true;
      }
      return false;
    }
    else {
      Holding hold   = this.getHolding(order.getCompany());
      int     amount = order.getAmount();
      if (amount <= hold.getAvailable() && amount > 0) {
        hold.subAvailable(amount);
        return true;
      }
      return false;
    }
  }

  public void takeDown() {
    String over = "GAME OVER FOR '" + this.getLocalName() + "' final money = " + String.format("%.2f", this.money) + "€\n";

    for (Holding hold : this.holdings.values()) {
      over += "  " + hold.toString() + "\n";
    }
    System.out.println(over);
  }

  public void printHoldings() {
    String print_str = "\n--- Agent '" + this.getLocalName() + "' holdings (" + String.format("%.2f", this.money) + "€) --- \n";

    for (Holding hold : this.holdings.values()) {
      print_str += "  " + hold.toString() + ",";
    }
    System.out.println(print_str + "\n --- Agent '" + this.getLocalName() + "' --- \n");
  }

  public double getMoney() {
    return this.money;
  }

  public void setMoney(double money) {
    this.money = money;
  }

  public AID getMarket() {
    return this.market;
  }

  public void addHolding(String name, Holding new_hold) {
    Holding hold;

    if ((hold = this.holdings.get(name)) == null) {
      this.holdings.put(name, new_hold);
    }
    else {
      hold.addAmount(new_hold.getRealAmount());
    }
  }

  public ConcurrentHashMap<String, Holding> getHoldings() {
    return this.holdings;
  }

  public Holding getHolding(String company) {
    return this.holdings.get(company);
  }
}
