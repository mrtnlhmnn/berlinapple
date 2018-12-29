#!/bin/bash
# optional:
# $DEBUG - any nonzero value will trigger a set -x in the script
#          in case the variable is unset, no harm is done
if [ -n "${DEBUG}" ]; then
   echo Debugging activated.
   set -x
fi

BERLINALE_ARGS=

java -jar /app/berlinapple.jar -XmX1G $BERLINALE_ARGS
