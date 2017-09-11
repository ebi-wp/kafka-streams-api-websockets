package uk.ac.ebi.streaming;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.TimeWindows;

import java.io.*;
import java.util.Map;
import java.util.Properties;


public class KafkaStreamingMain {

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streaming-example");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1500);

//        To get data produced before process started
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        KStreamBuilder builder = new KStreamBuilder();

        KStream<String, String> source = builder.stream("data-in");

        KStream<String, String> stats = source.groupByKey()
                .aggregate(KafkaStreamingStatistics::new,
                    (k, v, clusterstats) -> clusterstats.add(v),
                    TimeWindows.of(60000).advanceBy(10000),
                    Serdes.serdeFrom(new MySerde(), new MySerde()),
                    "data-store")
                .toStream((key, value) -> key.key().toString() + " " + key.window().start())
                .mapValues((job) -> job.computeAvgTime().toString());

        stats.to(Serdes.String(), Serdes.String(),  "data-out");

        KafkaStreams streams = new KafkaStreams(builder, props);

        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    /**
     * Do not use this serializer in production.
     */
    static class MySerde implements Serializer<KafkaStreamingStatistics>, Deserializer<KafkaStreamingStatistics>{

        @Override
        public byte[] serialize(String s, KafkaStreamingStatistics cStats){
            try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
                try(ObjectOutputStream o = new ObjectOutputStream(b)){
                    o.writeObject(cStats);
                }
                return b.toByteArray();
            } catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public KafkaStreamingStatistics deserialize(String topic, byte[] bytes) {
            try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
                try(ObjectInputStream o = new ObjectInputStream(b)){
                    return (KafkaStreamingStatistics) o.readObject();
                }
            }catch (IOException | ClassNotFoundException e ){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void configure(Map<String, ?> map, boolean b) {
        }

        @Override
        public void close() {

        }
    }
}
