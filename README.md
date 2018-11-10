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
The following messages are exchanged between the shareholders and the stock market. Depending on the content of the messages a different type of performative is used.

#### BOUGHT (ACLMessage.UNKNOWN)
Used to warn a shareholder that a certain share he was buying was just bought.

        BOUGHT;<company>;<price>;<amount>

#### BUY (ACLMessage.UNKNOWN)
Shareholder warns the stock market that he wants to buy a share.

        BUY;<company>;<price>;<amount>

#### BUY_ORDERS (ACLMessage.UNKNOWN)
Shareholder requests all the buy orders from the stock market

        BUY_ORDERS

The stock market will in turn reply with a performative of <i>ACLMessage.INFORM_IF</i> and the contents of the message will be a <i>ConcurrentHashMap<String, PriorityBlockingQueue<Order>></i>, which should be retrieved using <i>getContentObject()</i>.

#### COMPANIES (ACLMessage.UNKNOWN)
Shareholder requests a list of the companies in the stock market.

        COMPANIES

The stock market will in turn reply with a performative of <i>ACLMessage.CONFIRM</i> and the contents of the message will be a <i> LinkedList<String></i>, which should be retrieved using <i>getContentObject()</i>.

#### ORDERS (ACLMessage.UNKNOWN)
Shareholder requests all of the orders (buy/sell) in the stock market

        ORDERS

The stock market will in turn reply with a performative of <i>ACLMessage.REQUEST</i> and the contents of the message will be a <i> Map.Entry<ConcurrentHashMap<String, PriorityBlockingQueue<Order>>, ConcurrentHashMap<String, PriorityBlockingQueue<Order>>></i>, which should be retrieved using <i>getContentObject()</i>.

#### SELL (ACLMessage.UNKNOWN)
Shareholder warns the stock market that he wants to sell a specific share

        SELL;<company>;<price>;<amount>

#### SELL_ORDERS (ACLMessage.UNKNOWN)
Shareholder requests all the sell orders from the stock market

        SELL_ORDERS

The stock market will in turn reply with a performative of <i>ACLMessage.INFORM</i> and the contents of the message will be a <i> ConcurrentHashMap<String, PriorityBlockingQueue<Order>></i>, which should be retrieved using <i>getContentObject()</i>.

#### SOLD (ACLMessage.UNKNOWN)
Used to warn a shareholder that a certain share he was selling was just bought.

        SOLD;<company>;<price>;<amount>
