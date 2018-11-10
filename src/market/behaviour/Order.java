package market.behaviour;

import jade.core.AID;
import java.io.Serializable;

public abstract class Order implements Comparable<Order>, Serializable{
  static final long serialVersionUID = 42L;
  protected AID owner;
  protected String company;
  protected double price;
  protected int amount;


  Order(AID owner, String company, double price, int amount) {
    this.owner = owner;
    this.company = company;
    this.price = price;
    this.amount = amount;
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
}
