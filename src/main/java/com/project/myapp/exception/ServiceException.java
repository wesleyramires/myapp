package com.project.myapp.exception;

import com.project.myapp.util.ErrorMessage;
import lombok.Getter;
import java.util.Arrays;

@Getter
public class ServiceException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private final String codeErrors;
    private final Object[] parameters;

    public ServiceException(ErrorMessage message, Object... parameters) {
        super(message + " " + Arrays.asList(parameters));
        this.codeErrors = message.getMessage();
        this.parameters = parameters;
    }

}
