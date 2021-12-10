package ru.ustinov.voting.error;

public class NotFoundException extends IllegalRequestDataException {

    public NotFoundException(String msg, String... params) {
        super(msg, params);
    }
}