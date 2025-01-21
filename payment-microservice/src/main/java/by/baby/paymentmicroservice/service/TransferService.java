package by.baby.paymentmicroservice.service;

import by.baby.dto.CreatedPaymentDto;
import by.baby.event.CreatedPaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TransferService {

    private final KafkaTemplate<String, CreatedPaymentEvent> kafkaTemplate;

    public String transfer(CreatedPaymentDto createdPaymentDto) {
        try {
            log.info("Transfer started with data: \n{}", createdPaymentDto);
            CreatedPaymentEvent event = new CreatedPaymentEvent(
                    createdPaymentDto.getFromUserId(), createdPaymentDto.getToUserId(), createdPaymentDto.getAmount()
            );
            ProducerRecord<String, CreatedPaymentEvent> record = new ProducerRecord<>(
                    "payment-created-events-topic", UUID.randomUUID().toString(), event
            );

            String recordMessageId = UUID.randomUUID().toString();
            record.headers().add("messageId", recordMessageId.getBytes());

            kafkaTemplate.send(record)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("Transfer error: ", error);
                        } else {
                            log.info("Transfer succeeded with id {}", recordMessageId);
                        }
                    });
        } catch (Exception e) {
            log.error("Transfer exception: ", e);
            throw new RuntimeException(e);
        }
        return "Transfer processed successfully";
    }
}
