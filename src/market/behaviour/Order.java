package market.behaviour;

import jade.core.AID;

public abstract class Order implements Comparable<Order>{
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

  AID getOwner() {
    return this.owner;
  }

  String getCompany() {
    return this.company;
  }

  double getPrice() {
    return this.price;
  }

  int getAmount() {
    return this.amount;
  }
}
