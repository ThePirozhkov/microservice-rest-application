package by.baby.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponseBuilder {

    public static ResponseEntity<Map<String, Object>> buildErrorResponse(BindingResult bindingResult,
                                                                        HttpStatus httpStatus) {
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        response.put("status", httpStatus.value());
        response.put("errors", errors);
        response.put("timestamp", new Date());
        return new ResponseEntity<>(response, httpStatus);
    }

    public static ResponseEntity<Map<String, Object>> buildErrorResponse(String message,
                                                                         HttpStatus status) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<Map<String, Object>> buildResponse(HttpStatus httpStatus,
                                                                    String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", httpStatus.value());
        response.put("message", message);
        response.put("timestamp", new Date());
        return new ResponseEntity<>(response, httpStatus);
    }

    public static ResponseEntity<Map<String, Object>> buildResponse(HttpStatus httpStatus,
                                                                    LinkedHashMap<String, Object> response) {
        LinkedHashMap<String, Object> body = new LinkedHashMap<>();
        body.put("status", httpStatus.value());
        body.putAll(response);
        response.put("timestamp", new Date());
        return new ResponseEntity<>(body, httpStatus);
    }

    public static ResponseEntity<Map<String, Object>> buildResponse(HttpStatus httpStatus,
                                                                    Object returned) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("status", httpStatus.value());
        response.put("returned", returned);
        response.put("timestamp", new Date());
        return new ResponseEntity<>(response, httpStatus);
    }

    public static ResponseEntity<Map<String, Object>> buildResponse(HttpStatus httpStatus) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", httpStatus.value());
        response.put("timestamp", new Date());
        return new ResponseEntity<>(response, httpStatus);
    }

}
