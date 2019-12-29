#!/bin/bash
# optional:
# $DEBUG - any nonzero value will trigger a set -x in the script
#          in case the variable is unset, no harm is done
if [ -n "${DEBUG}" ]; then
   echo Debugging activated.
   set -x
fi

BERLINAPPLE_ARGS=

java -showversion -Xmx1G -jar /app/berlinapple.jar $BERLINAPPLE_ARGS
