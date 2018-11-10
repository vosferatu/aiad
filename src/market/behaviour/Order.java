package market.behaviour;

import jade.core.AID;

public class Order implements Comparable<Order>{
  private AID owner;
  private String company;
  private double price;
  private int amount;


  public Order(AID owner, String company, double price, int amount) {
    this.owner = owner;
    this.company = company;
    this.price = price;
    this.amount = amount;
  }

  @Override
  public int compareTo(final Order order) {
    return Double.compare(this.price, order.price);
  }

  public AID getOwner() {
    return this.owner;
  }

  public String getCompany() {
    return this.company;
  }

  public double getPrice() {
    return this.price;
  }

  public int getAmount() {
    return this.amount;
  }

  @Override
  public String toString() {
    return this.owner.getLocalName() + " -> (" + String.format("%.2f", this.price) + ", " + this.amount + ")";
  }
}
