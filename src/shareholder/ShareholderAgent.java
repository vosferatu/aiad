package shareholder;

import shareholder.behaviour.*;

import jade.core.AID;
import java.util.Map;
import jade.core.Agent;
import java.util.Random;
import java.util.Map.Entry;
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

  private final String[] companies = {"intel", "amd", "nvidia", "asus", "samsung"};

  private double money;

  // Company_Name -> Share
  private ConcurrentHashMap<String, Holding> holdings = new ConcurrentHashMap<String, Holding>();

  public void boughtShare(String company, double price, int amount) {
    if (!this.holdings.containsKey(company)) {
      Holding hold = new Holding(company, price, amount);
      this.holdings.put(company, hold);
    }
    else {
      Holding hold = this.holdings.get(company);
      this.money -= hold.buy(price, amount);
    }
  }

  public void soldShare(String company, double price, int amount) {
    if (this.holdings.containsKey(company)) {
      Holding hold = this.holdings.get(company);
      this.money += hold.sell(price, amount);
      if (hold.getAmount() <= 0) {
        this.holdings.remove(company);
      }
    }
    else {
      System.err.println("WTF?");
    }
  }

  public void setup() {
    this.market = new AID(MARKET_NAME, false);
    ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);
    msg.addReceiver(this.market);
    msg.setSender(this.getAID());

    this.addBehaviour(new WaitForResponses(this));

    this.initialSetup();
    for (Map.Entry<String, Holding> entry : this.holdings.entrySet()) {
      Holding share = entry.getValue();
      String price = String.format("%.2f", share.getLastBuyPrice()*1.05);
      msg.setContent("SELL;" + entry.getKey() + ";" + price +";"+(int)(share.getAmount()*0.5));
      this.send(msg);
    }
  }

  private void initialSetup() {
    Random rand = new Random();
    this.money = 150 + (rand.nextFloat() * 150); // Generates random number between [150, 300]

    int num_holdings = 3 + rand.nextInt(3);
    for (int i = 0; i < num_holdings; i++) {
      String company = this.companies[rand.nextInt(5)];
      int shares_number = 10 + rand.nextInt(11);
      double initial_price = 5.0 + rand.nextFloat()*15.0;
      this.holdings.put(company, new Holding(company, initial_price, shares_number));
    }
  }

  public void takeDown() {
    System.out.println(this.getLocalName() + ": Exited!");
  }

  public void printHoldings() {
    String print_str = "\n--- Agent '" + this.getLocalName() + "' holdings (" + String.format("%.2f", this.money) + "€) --- \n";
    for (Holding hold : this.holdings.values()) {
      print_str += "  " + hold.toString() + "\n";
    }
    System.out.println(print_str + " --- Agent '" + this.getLocalName() + "' --- ");
  }
}
