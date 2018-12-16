package market.behaviour;

import jade.core.AID;

public class SellOrder extends Order {
  public SellOrder(AID owner, String company, double price, int amount) {
    super(owner, company, price, amount);
  }

  @Override
  public int compareTo(final Order order) {
    return Double.compare(this.price, order.price);
  }

  @Override
  public String toString() {
    return this.owner.getLocalName() + " SELL -> (" + String.format("%.2f", this.price) + ", " + this.amount + ")";
  }

  @Override
  public String toMsg() {
    return "SELL;" + this.company + ";" + String.format("%.2f", this.price) + ";" + this.amount;
  }
}
