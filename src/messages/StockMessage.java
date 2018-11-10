package messages;

import java.util.Arrays;

public class StockMessage {
  private static final String[] MSG_TYPES = {"BOUGHT", "BUY", "SELL", "SOLD"};
  private String type;
  private String company;
  private double price;
  private int amount;

  StockMessage(String type, String company, double price, int amount) {
    this.type = type;
    this.company = company;
    this.price = price;
    this.amount = amount;
  }

  public static StockMessage fromString(String message) {
    String[] parts = message.split(";");
    if (parts.length == 4) {
      String type = parts[0];
      if (Arrays.binarySearch(MSG_TYPES, type) >= 0) {
        String company = parts[1];
        double price;
        int amount;
        try {
          price = Double.parseDouble(parts[2]);
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

  public double getPrice() {
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
