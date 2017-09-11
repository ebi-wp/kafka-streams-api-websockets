#!/usr/bin/env bash


KAFKA_DIR=kafka_2.11-0.11.0.0

# Stop Kafka
bin/kafka-server-stop.sh

# Stop Zookeeper
bin/zookeeper-server-stop.sh

### You need to manually stop following Java processes (either CTRL+C or kill {PID}):
# ps -ef | grep uk.ac.ebi.Application
# ps -ef | grep KafkaExampleProducer
# ps -ef | grep KafkaStreamingMain

