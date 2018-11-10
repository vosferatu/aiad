package shareholder.behaviour;

import java.util.LinkedList;

public class Holding {
  public String company_name;
  public int amount;
  public LinkedList<Double> buy_prices; //First element is latest buy price
  public LinkedList<Double> sell_prices; //First element is latest sell price

  public Holding(String name, int amount, double buy_price) {
    this.company_name = name;
    this.amount = amount;
    this.sell_prices = new LinkedList<Double>();
    this.buy_prices = new LinkedList<Double>();
    this.buy_prices.addFirst(Double.valueOf(buy_price));
  }

  public double sell(int amount, double price) {
    this.amount-=amount;
    this.sell_prices.addFirst(Double.valueOf(price));
    return amount*price;
  }

  public double buy(int amount, double price) {
    this.amount+=amount;
    this.buy_prices.addFirst(Double.valueOf(price));
    return amount*price;
  }

  public double getLastBuyPrice() {
    return this.buy_prices.getFirst();
  }

  public double getLastSellPrice() {
    return this.sell_prices.getFirst();
  }

  public int getAmount() {
    return this.amount;
  }

  public String getCompany() {
    return this.company_name;
  }

}
