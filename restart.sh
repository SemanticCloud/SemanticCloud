#!/bin/bash
export CONSUL=http://localhost:8500
docker-compose down
docker-compose up -d consul
./etc/wait-consul.sh
./etc/init-consul.sh
docker-compose up -d