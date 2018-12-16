#!/bin/bash

for ((n=0; n<100; n++)) do
  ALOOF=$(( $RANDOM % 60 ))
  BOLD=$(( $RANDOM % $(( 60 - $ALOOF )) ))
  FRIGHTFUL=$(( 60 - $ALOOF - $BOLD))

  echo "sh run_simulation.sh ${ALOOF} ${BOLD} ${FRIGHTFUL}"
done
