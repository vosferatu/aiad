package behaviour;

public class Order implements Comparable<Order>{
  private String company;
  private String agent;
  private float price;
  private int amount;


  public Order(String company, String agent, float price, int amount) {
    this.company = company;
    this.agent = agent;
    this.price = price;
    this.amount = amount;
  }

  @Override
  public int compareTo(final Order order) {
    return Float.compare(this.price, order.price);
  }

  public String getCompany() {
    return this.company;
  }

  public String getAgent() {
    return this.agent;
  }

  public float getPrice() {
    return this.price;
  }

  public int getAmount() {
    return this.amount;
  }
}
