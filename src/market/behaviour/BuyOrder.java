package market.behaviour;

import jade.core.AID;

public class BuyOrder extends Order {
  public BuyOrder(AID owner, String company, double price, int amount) {
    super(owner, company, price, amount);
  }

  @Override
  public int compareTo(final Order order) {
    return Double.compare(order.price, this.price);
  }

  @Override
  public String toString() {
    return this.owner.getLocalName() + " BUY -> (" + String.format("%.2f", this.price) + ", " + this.amount + ")";
  }

  @Override
  public String toMsg() {
    return "BUY;" + this.company + ";" + String.format("%.2f", this.price) + ";" + this.amount;
  }
}
