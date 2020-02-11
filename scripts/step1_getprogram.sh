#!/bin/bash

# get all ics files for all screeningIds from 1 to 99999
# a lot of these files will be simply empty, so in the following step we filter them out

BASEURL=https://www.berlinale.de/de/programm/programm/ical.html?download=true\&screeningId=

mkdir -p tmp

id=0
while true; do   
   if [[ "$id" -gt 99999 ]]; then 
      exit 0
   else	   
	  id=$((id+1))
	  echo ${BASEURL}${id} ...
      wget --quiet ${BASEURL}${id} -O tmp/screening_${id}.ics
   fi
done
