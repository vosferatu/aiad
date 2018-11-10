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
  private ConcurrentHashMap<String, Holding> shares = new ConcurrentHashMap<String, Holding>();

  public void setup() {
    this.market = new AID(MARKET_NAME, false);
    ACLMessage msg = new ACLMessage(0);
    msg.addReceiver(this.market);
    msg.setSender(this.getAID());

    this.initialSetup();
    for (Map.Entry<String, Holding> entry : this.shares.entrySet()) {
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
      this.shares.put(company, new Holding(company, shares_number, initial_price));
    }
  }

  public void takeDown() {
    System.out.println(this.getLocalName() + ": Exited!");
  }
}
