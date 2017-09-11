package uk.ac.ebi;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class KafkaConsumerListener {

    @Value(value = "${websocket.messages}")
    private String destinationMessages;

    @Autowired
    private SimpMessagingTemplate template;

    @KafkaListener(topics = "${kafka.topic.out}", containerFactory = "rawKafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, String> cr) throws Exception {

        template.convertAndSend(destinationMessages, cr.key() + " " + cr.value());
    }
}
