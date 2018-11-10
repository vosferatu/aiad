# AIAD Stock Market

## Setup

You need maven to be able to compile the project.

Afterwards simply run

    mvn compile



## Running
After having compiled the whole project.

 - Simply running the GUI
    
     
       java -cp lib/jade.jar:target/classes jade.Boot -gui
        
 - Running the GUI along with some agents

        java -cp lib/jade.jar:target/classes jade.Boot -gui -agents "<agent1_name>:<agent1_class>;<agent2_name>:<agent2_class>"

## Components

The project is composed of mainly 3 sets of agents.

### Company

This group of agents act as a simulation of the companies. The stock agents communicate with these to know insights about the given company and make certain prediction on whether the company value will skyrocket or plummet.

### Stock Market

This is a single agent which will keep track of every buy and sell order placed by the stock agents. Stock agents communicate directly with this agent in order to both place and remove orders. On a successful purchase, the stock market notifies both the seller and the buyer.

The agent that represents the stock market must be named 'MARKET'.

### Shareholders

Represents the multiple people that might be buying and selling the stocks. They communicate with both the companies and the stock market and are the main focus of this project.

### Messages 

#### BOUGHT
Used to warn a shareholder that a certain share he was buying was just bought.

        BOUGHT;<company>;<price>;<amount>
        
#### BUY
Shareholder warns the stock market that he wants to buy a share.

        BUY;<company>;<price>;<amount>
        
#### BUY_ORDERS
Shareholder requests all the buy orders from the stock market
The reply will contain a ConcurrentHashMap<String, PriorityBlockingQueue<Order>> in the content and should be retrived using getContentObject().

        BUY_ORDERS
        
#### COMPANIES
Shareholder requests a list of the companies in the stock market.
The reply will contain a ConcurrentHashMap<String, PriorityBlockingQueue<Order>> in the content and should be retrived using getContentObject().

        COMPANIES
        
#### ORDERS
Shareholder requests all of the orders (buy/sell) in the stock market
The reply will contain a Map.Entry<ConcurrentHashMap<String, PriorityBlockingQueue<Order>>, ConcurrentHashMap<String, PriorityBlockingQueue<Order>>> (sell_orders, buy_orders) in the content and should be retrived using getContentObject().
    
        ORDERS
        
#### SELL
Shareholder warns the stock market that he wants to sell a specific share

        SELL;<company>;<price>;<amount>
        
#### SELL_ORDERS
Shareholder requests all the sell orders from the stock market
The reply will contain a ConcurrentHashMap<String, PriorityBlockingQueue<Order>> in the content and should be retrived using getContentObject().
    
        SELL_ORDERS
        

#### SOLD
Used to warn a shareholder that a certain share he was selling was just bought.

        SOLD;<company>;<price>;<amount>
