#!/usr/bin/env bash

KAFKA_DIR=kafka_2.11-0.11.0.0
PROJECT_DIR=kafka-streaming-websockets

# Download kafka
if [ -d $KAFKA_DIR ]; then
    echo "Kafka already downloaded";
else
    wget http://apache.mirrors.nublue.co.uk/kafka/0.11.0.0/kafka_2.11-0.11.0.0.tgz
    tar zxf kafka_2.11-0.11.0.0.tgz
fi

# Checkout and build project
if [ -d $PROJECT_DIR ]; then
    echo "Project already built";
else
    git clone https://github.com/sajmmon/kafka-streaming-websockets.git
    mvn clean package -f kafka-streaming-websockets/pom.xml
fi

# cd KAFKA_DIR
cd $KAFKA_DIR

# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties &

# Start Kafka
bin/kafka-server-start.sh config/server.properties &

# Add topics (if it exists we get some errors)
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic data-in
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic data-out

# cd PROJECT_DIR
cd ../$PROJECT_DIR


# Run commented commands below in separate terminals or in background

# Generate data with Java producer
# java -cp target/shade-kafka-streaming-websockets-0.1.0.jar uk.ac.ebi.produce.KafkaExampleProducer

# Process data with Kafka Streming API
# java -cp target/shade-kafka-streaming-websockets-0.1.0.jar uk.ac.ebi.streaming.KafkaStreamingMain

# Start Tomcat via Spring Boot
# java -cp target/shade-kafka-streaming-websockets-0.1.0.jar uk.ac.ebi.Application


