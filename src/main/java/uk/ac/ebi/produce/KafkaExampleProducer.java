package uk.ac.ebi.produce;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.Random;

/**
 * Generate input to Kafka topic.
 * Example (key,value) messages:
 * (PG,120)
 * (HX,111)
 * (PG,105)
 * (OY,121)
 *
 * key - represents LSF cluster name
 * value - represents waiting time for particular job submission
 *
 */
public class KafkaExampleProducer {

    public static KafkaProducer<String, String> producer = null;

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();

        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);
        Runtime.getRuntime().addShutdownHook(new Thread(producer::close));

        String[] clusterNames = {"PG", "OY", "HX", "HH"};
        Random random = new Random();


        while (true) {

            for (String clusterName : clusterNames) {

                double ifSkip = random.nextInt(10);

                if (clusterName.equals("PG") && ifSkip < 2) continue;
                if (clusterName.equals("OY") && ifSkip < 4) continue;
                if (clusterName.equals("HX") && ifSkip < 6) continue;
                if (clusterName.equals("HH") && ifSkip < 8) continue;

                int waitTime = 0;

                if (clusterName.equals("PG")) waitTime = random.nextInt(30) + 100;
                if (clusterName.equals("OY")) waitTime = random.nextInt(30) + 110;
                if (clusterName.equals("HX")) waitTime = random.nextInt(30) + 120;
                if (clusterName.equals("HH")) waitTime = random.nextInt(30) + 130;

                ProducerRecord<String, String> record = new ProducerRecord<>("data-in", clusterName, String.valueOf(waitTime));

                producer.send(record, (RecordMetadata r, Exception e) -> {
                    if (e != null) {
                        System.out.println("Error producing to topic " + r.topic());
                        e.printStackTrace();
                    }
                });

                Thread.sleep(100);
            }
        }
    }
}
