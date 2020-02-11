#!/bin/bash

# filter out ics files with start date 19700101T000000Z to empty (can be deleted)

mkdir -p empty nonempty

pushd tmp > /dev/null
for f in `find . -name "*.ics"`; do 
  g=`grep -H DTSTART:19700101T000000Z $f`
  if [ "$g" == "" ]; then
     # echo $f is empty
     mv $f ../nonempty
  else
     mv $f ../empty
  fi
done
popd > /dev/null
