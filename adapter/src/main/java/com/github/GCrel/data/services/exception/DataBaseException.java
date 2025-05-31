package com.github.GCrel.data.services.exception;

public class DataBaseException extends org.springframework.dao.DataAccessException {
    public DataBaseException(String message) {
        super(message);
    }
}
