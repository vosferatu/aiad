package shareholder.behaviour;

import java.util.LinkedList;

public class Holding {
  public String company_name;
  public int amount;
  public int amount_available;
  public LinkedList<Double> buy_prices;  //First element is latest buy price
  public LinkedList<Double> sell_prices; //First element is latest sell price

  public Holding(String name, double buy_price, int amount) {
    this.company_name     = name;
    this.amount           = amount;
    this.amount_available = amount;
    this.sell_prices      = new LinkedList<Double>();
    this.buy_prices       = new LinkedList<Double>();
    this.buy_prices.addFirst(Double.valueOf(buy_price));
    this.sell_prices.addFirst(Double.valueOf(buy_price));
  }

  public double sell(double price, int amount) {
    this.amount -= amount;
    this.sell_prices.addFirst(Double.valueOf(price * 1.05));
    return (double)amount * price;
  }

  public double buy(double price, int amount) {
    this.amount           += amount;
    this.amount_available += amount;
    this.buy_prices.addFirst(Double.valueOf(price * 0.95));
    return (double)amount * price;
  }

  public void subAvailable(int amount) {
    this.amount_available -= amount;
  }

  public double getLastBuyPrice() {
    return this.buy_prices.getFirst();
  }

  public double getLastSellPrice() {
    return this.sell_prices.getFirst();
  }

  public void addAmount(int amount) {
    this.amount           += amount;
    this.amount_available += amount;
  }

  public int getAvailable() {
    return this.amount_available;
  }

  public int getRealAmount() {
    return this.amount;
  }

  public String getCompany() {
    return this.company_name;
  }

  @Override
  public String toString() {
    return "('" + this.company_name + "', " + this.amount + ", " + String.format("%.2f", this.getLastBuyPrice()) + ")";
  }
}
