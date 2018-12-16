#!/bin/bash

ALOOF_N=0
BOLD_N=1
FRIGHTFUL_N=2
START="java -cp lib/jade.jar:target/classes jade.Boot -agents \""
AGENTS="market:market.StockMarketAgent("
SHAREHOLDER_CLASS=":shareholder.ShareholderAgent"

if [ $# -eq 3 ]; then
  ALOOF_AGENTS=$1
  BOLD_AGENTS=$2
  FRIGHTFUL_AGENTS=$3
  TOTAL_AGENTS=$(( $ALOOF_AGENTS + $BOLD_AGENTS + $FRIGHTFUL_AGENTS ))
  strat_arr=()

  for ((n=0; n<$TOTAL_AGENTS; n++)) do
    if [[ n -lt $ALOOF_AGENTS ]]; then
      strat_arr[n]=$ALOOF_N
    elif [[ n -ge $ALOOF_AGENTS ]] &&  [[ n -lt $(( $BOLD_AGENTS + $ALOOF_AGENTS )) ]]; then
      strat_arr[n]=$BOLD_N
    elif [[ n -ge $(( $BOLD_AGENTS + $ALOOF_AGENTS )) ]] &&  [[ n -lt $TOTAL_AGENTS ]]; then
      strat_arr[n]=$FRIGHTFUL_N
    fi
  done

  for ((number=0; number<$TOTAL_AGENTS; number++)) do
    AGENTS="${AGENTS}${TOTAL_AGENTS});holder${number}${SHAREHOLDER_CLASS}(${TOTAL_AGENTS},${strat_arr[${number}]});"
  done
  eval "$START$AGENTS\""
else
  echo 'Usage:'
  echo '  sh run_simulation.sh <aloof_number> <bold_number> <frightful_number>'
  echo ''
  echo 'Arguments:'
  echo '  aloof_number       Number of aloof agents'
  echo '  bold_number        Number of bold agents'
  echo '  frightful_number   Number of frightful agents'
fi
