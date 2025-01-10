package by.baby.usermicroservice.exception.handler;

import by.baby.usermicroservice.exception.UnableToCreateUserException;
import by.baby.usermicroservice.exception.UnableToDeleteUserException;
import by.baby.usermicroservice.exception.UnableToUpdateUserException;
import by.baby.usermicroservice.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(UnableToCreateUserException.class)
    public ResponseEntity<String> handleUnableToCreateUserException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(UnableToUpdateUserException.class)
    public ResponseEntity<String> handleUnableToUpdateUserException() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(UnableToDeleteUserException.class)
    public ResponseEntity<String> handleUnableToDeleteUserException() {
        return ResponseEntity.internalServerError().build();
    }

}
