package by.baby.usermicroservice.exception;

public class UnableToDeleteUserException extends RuntimeException
{
    public UnableToDeleteUserException(String message) {
        super(message);
    }
}
