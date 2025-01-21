package by.baby.paymentmicroservice.controller.rest;

import by.baby.dto.CreatedPaymentDto;
import by.baby.paymentmicroservice.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentRestController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody CreatedPaymentDto createdPaymentDto) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", HttpStatus.CREATED.value());
        response.put("message", transferService.transfer(createdPaymentDto));
        response.put("senderId", createdPaymentDto.getFromUserId());
        response.put("receiverId", createdPaymentDto.getToUserId());
        response.put("amount", createdPaymentDto.getAmount());
        response.put("timestamp", new Date());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
