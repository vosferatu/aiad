package messages;

public class StockMessage {
  private static final String BUY = "BUY";
  private static final String SELL = "SELL";
  private static final String BOUGHT = "BOUGHT";
  private static final String SOLD = "SOLD";
  private String type;
  private String company;
  private float price;
  private int amount;

  StockMessage(String type, String company, float price, int amount) {
    this.type = type;
    this.company = company;
    this.price = price;
    this.amount = amount;
  }

  public static StockMessage fromString(String message) {
    String[] parts = message.split(";");
    if (parts.length == 4) {
      String type = parts[0];
      if (type == BUY || type == SELL || type == BOUGHT || type == SOLD) {
        String company = parts[1];
        float price;
        int amount;
        try {
          price = Float.parseFloat(parts[2]);
          amount = Integer.parseInt(parts[3]);
          return new StockMessage(type, company, price, amount);
        }
        catch (Exception e) {
          System.err.println("Faulty message received! '" + message + "'");
        }
      }
      else {
        System.err.println("Unknown message type received! '" + type + "'");
      }
    }
    else {
      System.err.println("Faulty message received! '" + message + "'");
    }
    return null;
  }

  public String getType() {
    return this.type;
  }

  public String getCompany() {
    return this.company;
  }

  public float getPrice() {
    return this.price;
  }

  public int getAmount() {
    return this.amount;
  }

  @Override
  public String toString() {
    return this.type + ";" + this.company + ";" + this.price + ";" + this.amount;
  }
}
