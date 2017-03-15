#!/bin/bash

if [ -z $CONSUL ]; then
    CONSUL=http://localhost:8500
fi

CONSUL=$CONSUL/v1/kv
curl -L -XPUT $CONSUL/ontology/cloud.owl --data-bin @/opt/SemanticCloud/cloud.owl