package org.piotrowski.cardureadoo.application.exception.expansion;

public class ExpansionAlreadyExistsException extends RuntimeException {
    public ExpansionAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
