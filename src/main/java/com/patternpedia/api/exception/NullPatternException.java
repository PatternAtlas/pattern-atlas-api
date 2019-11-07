package com.patternpedia.api.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NullPatternException extends RuntimeException {
    public NullPatternException(String message) {
        super(message);
    }
}