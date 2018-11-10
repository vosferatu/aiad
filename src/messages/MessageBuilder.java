package messages;

public final class MessageBuilder {
  public static final String BUY = "BUY";
  public static final String SELL = "SELL";
  public static final String BOUGHT = "BOUGHT";
  public static final String SOLD = "SOLD";
  public static final String SELL_ORDERS = "SELL_ORDERS";
  public static final String BUY_ORDERS = "BUY_ORDERS";
  public static final String ORDERS = "ORDERS";
  public static final String COMPANIES = "COMPANIES";

  private MessageBuilder() {}

  public static StockMessage buyStockMsg(String company, double price, int amount) {
    return new StockMessage(BUY, company, price, amount);
  }

  public static StockMessage sellStockMsg(String company, double price, int amount) {
    return new StockMessage(SELL, company, price, amount);
  }

  public static StockMessage boughtStockMsg(String company, double price, int amount) {
    return new StockMessage(BOUGHT, company, price, amount);
  }

  public static StockMessage soldStockMsg(String company, double price, int amount) {
    return new StockMessage(SOLD, company, price, amount);
  }


}
