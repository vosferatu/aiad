package shareholder;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;

public class ShareholderAgent extends Agent {
  private static final String MARKET_NAME = "market";

  private AID market;
  private AID companies;

  public void setup() {
    System.out.println("Hello World!");
    AMSAgentDescription market_desc = this.searchForAgent(MARKET_NAME);
    if (market_desc == null) {
      System.exit(1);
    }

    this.market = market_desc.getName();

    // this.addBehaviour(new WorkingBehaviour());
  }

  private AMSAgentDescription searchForAgent(String name) {
    AID target = new AID(name, false);
    try {
      SearchConstraints c = new SearchConstraints();
      c.setMaxResults(Long.valueOf(1));
      AMSAgentDescription[] agents = AMSService.search(this, target, new AMSAgentDescription(), c);
      if (agents.length >= 1 && agents[0] != null) {
        return agents[0];
      }
      else {
        System.err.println("Failed to get agent '" + name + "', maybe not started?");
        return null;
      }
    }

    catch (Exception e) {
      System.err.println("Failed to get agent '" + name + "', maybe not started?\n - " + e.getMessage());
      return null;
    }
  }

  public void takeDown() {
    System.out.println(this.getLocalName() + ": Exited!");
  }
}
