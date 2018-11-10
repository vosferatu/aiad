package shareholder;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;

public class ShareholderAgent extends Agent {
  private static final String MARKET_NAME = "market";

  private AID market;
  private AID companies;

  public void setup() {
    this.market = new AID(MARKET_NAME, false);
    ACLMessage msg = new ACLMessage(0);
    msg.setContent("Hello from agent '" + this.getLocalName() + "'!");
    msg.addReceiver(this.market);
    msg.setSender(this.getAID());
    this.send(msg);
    // this.addBehaviour(new WorkingBehaviour());
  }

  private AMSAgentDescription searchForAgent(String name) {
    AID target = new AID(name, false);
    try {
      SearchConstraints c = new SearchConstraints();
      c.setMaxResults(Long.valueOf(-1));
      System.out.println("Starting search...");
      AMSAgentDescription[] agents = AMSService.search(this, target, new AMSAgentDescription(), c);
      System.out.println("Finished search");
      if (agents.length >= 1 && agents[0] != null) {
        return agents[0];
      }
      else {
        System.err.println("Failed to get agent '" + name + "', maybe not started? '" + agents + "'");
        return null;
      }
    }

    catch (Exception e) {
      System.err.println("Failed to get agent '" + name + "', maybe not started?\n - " + e.toString());
      return null;
    }
  }

  public void takeDown() {
    System.out.println(this.getLocalName() + ": Exited!");
  }
}
