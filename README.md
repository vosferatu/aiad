# AIAD Stock Market

## Setup

You need maven to be able to compile the project.

Afterwards simply run 'mvn compile'



## Running

java -cp lib/jade.jar:classes jade.Boot -gui -agents "\<agent1_name>:\<agent1_class>;\<agent2_name>:\<agent2_class>"

## Components

The project is composed of mainly 3 sets of agents.

### Company

This group of agents act as a simulation of the companies. The stock agents communicate with these to know insights about the given company and make certain prediction on whether the company value will skyrocket or plummet.

### Stock Market

This is a single agent which will keep track of every buy and sell order placed by the stock agents. Stock agents communicate directly with this agent in order to both place and remove orders. On a successful purchase, the stock market notifies both the seller and the buyer.

The agent that represents the stock market must be named 'MARKET'.

### Stock Agent

Represents the multiple people that might be buying and selling the stocks. They communicate with both the companies and the stock market and are the main focus of this project.
