package io.github.ust.quantil.patternatlas.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NullIssueException extends RuntimeException {
    public NullIssueException(String message) {
        super(message);
    }
}