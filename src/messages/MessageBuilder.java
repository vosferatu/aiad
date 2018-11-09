package messages;

public final class MessageBuilder {
  private static final String BUY = "BUY";
  private static final String SELL = "SELL";
  private static final String BOUGHT = "BOUGHT";
  private static final String SOLD = "SOLD";

  private MessageBuilder() {}

  public static StockMessage buyStockMsg(String company, float price, int amount) {
    return new StockMessage(BUY, company, price, amount);
  }

  public static StockMessage sellStockMsg(String company, float price, int amount) {
    return new StockMessage(SELL, company, price, amount);
  }

  public static StockMessage boughtStockMsg(String company, float price, int amount) {
    return new StockMessage(BOUGHT, company, price, amount);
  }

  public static StockMessage soldStockMsg(String company, float price, int amount) {
    return new StockMessage(SOLD, company, price, amount);
  }


}
