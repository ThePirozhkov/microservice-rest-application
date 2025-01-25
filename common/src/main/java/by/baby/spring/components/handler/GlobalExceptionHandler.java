package by.baby.spring.components.handler;

import by.baby.exception.UnableToCreateException;
import by.baby.exception.UnableToDeleteException;
import by.baby.exception.UnableToUpdateException;
import by.baby.exception.NotFoundException;
import by.baby.util.HttpResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException e) {
        return HttpResponseBuilder.buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnableToCreateException.class)
    public ResponseEntity<Map<String, Object>> handleUnableToCreateException(UnableToCreateException e) {
        return HttpResponseBuilder.buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnableToUpdateException.class)
    public ResponseEntity<Map<String, Object>> handleUnableToUpdateException(UnableToUpdateException e) {
        return HttpResponseBuilder.buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnableToDeleteException.class)
    public ResponseEntity<Map<String, Object>> handleUnableToDeleteException(UnableToDeleteException e) {
        return HttpResponseBuilder.buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
