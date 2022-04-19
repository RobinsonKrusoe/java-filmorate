package ru.yandex.practicum.filmorate.exceptions;

//непроверяемое исключение
public class ValidationException extends RuntimeException {
    public ValidationException (){}

    public ValidationException(final String message) {
        super(message);
    }
}
