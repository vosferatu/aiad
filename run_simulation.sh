#!/bin/bash
AGENTS="market:market.StockMarketAgent;"
SHAREHOLDER_CLASS=":shareholder.ShareholderAgent;"
AGENTS_NUMBER=5

if [ $# -eq 1 ]; then
  AGENTS_NUMBER=$1
fi

for ((number=0; number<$AGENTS_NUMBER; number++))
do
  AGENTS="${AGENTS}holder${number}${SHAREHOLDER_CLASS}"
done

eval "java -cp lib/jade.jar:target/classes jade.Boot -gui \"$AGENTS\""
