package market.behaviour;

import jade.core.AID;
import java.io.Serializable;

public abstract class Order implements Serializable, Comparable<Order> {
  static final long serialVersionUID = 42L;
  protected AID owner;
  protected String company;
  protected double price;
  protected int amount;


  Order(AID owner, String company, double price, int amount) {
    this.owner   = owner;
    this.company = company;
    this.price   = price;
    this.amount  = amount;
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

  public void addAmount(int amount) {
    this.amount += amount;
  }

  public void subAmount(int amount) {
    this.amount -= amount;
  }

  public double getTotal() {
    return this.price * this.amount;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Order) {
      Order order = (Order)obj;
      return order.company.equals(this.company) && Double.valueOf(order.price).equals(this.price);
    }
    return false;
  }

  abstract public String toMsg();
}
