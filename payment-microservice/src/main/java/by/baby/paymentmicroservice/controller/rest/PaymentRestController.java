package by.baby.paymentmicroservice.controller.rest;

import by.baby.dto.CreatedPaymentDto;
import by.baby.paymentmicroservice.service.TransferService;
import by.baby.util.HttpResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentRestController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(@Valid @RequestBody CreatedPaymentDto createdPaymentDto,
                                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return HttpResponseBuilder.buildErrorResponse(bindingResult, HttpStatus.BAD_REQUEST);
        }
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("message", transferService.transfer(createdPaymentDto));
        response.put("senderId", createdPaymentDto.getFromUserId());
        response.put("receiverId", createdPaymentDto.getToUserId());
        response.put("amount", createdPaymentDto.getAmount());
        return HttpResponseBuilder.buildResponse(HttpStatus.CREATED, response);
    }

}
