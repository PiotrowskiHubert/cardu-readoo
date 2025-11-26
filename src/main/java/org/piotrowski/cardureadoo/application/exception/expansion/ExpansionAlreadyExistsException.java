package org.piotrowski.cardureadoo.application.exception.expansion;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ExpansionAlreadyExistsException extends RuntimeException {
    public ExpansionAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
