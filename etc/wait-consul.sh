#!/bin/bash
count=0
while [ 1 ];
do
    curl -L -m 1 -s --fail $CONSUL &> /dev/null
    if [ $? -eq 0 ];
    then
        echo "consul is ready"
        break
    fi
    count=$((count+1))
    echo "wait cousul:" $count"s"
    sleep 1
done