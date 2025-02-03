package by.baby.spring.components.service;

import by.baby.dto.CreatedPaymentDto;
import by.baby.dto.PaymentDto;
import by.baby.event.CreatedPaymentEvent;
import by.baby.exception.NotFoundException;
import by.baby.spring.components.mapper.PaymentDtoMapper;
import by.baby.spring.components.repository.PaymentRepository;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService implements by.baby.spring.components.service.Service<PaymentDto, String> {

    private final PaymentRepository paymentRepository;
    private final PaymentDtoMapper paymentDtoMapper;
    private final KafkaTemplate<String, CreatedPaymentEvent> kafkaTemplate;

    @Override
    public List<PaymentDto> findAll() {
        return paymentRepository.findAll().stream()
                .map(paymentDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PaymentDto> findById(String id) {
        return paymentRepository.findById(id)
                .map(paymentDtoMapper::mapToDto);
    }

    @SneakyThrows
    @Override
    public Optional<PaymentDto> save(PaymentDto dto) {
        throw new ExecutionControl.NotImplementedException("Method not implemented / Use transfer method");
    }

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

    @Override
    public Optional<PaymentDto> update(PaymentDto dto, String id) {
        log.info("Updating payment with id {} and dto {}", id, dto);
        Optional<PaymentDto> newDto = paymentRepository.findById(id)
                .map(paymentEntity -> {
                    if (paymentEntity.getAmount() != null)
                        paymentEntity.setAmount(dto.getAmount());
                    if (paymentEntity.getPaymentDate() != null)
                        paymentEntity.setPaymentDate(dto.getPaymentDate());
                    return paymentEntity;
                })
                .map(paymentRepository::save)
                .map(paymentDtoMapper::mapToDto);
        log.info("Updated payment with id {} and dto {}", id, newDto);
        return newDto;
    }

    @Override
    public boolean deleteById(String id) {
        if (!paymentRepository.existsById(id)) {
            throw new NotFoundException("Payment with id " + id + " not found");
        }
        paymentRepository.deleteById(id);
        return paymentRepository.existsById(id);
    }
}
