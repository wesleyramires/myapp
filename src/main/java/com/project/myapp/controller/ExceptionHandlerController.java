package com.project.myapp.controller;

import com.project.myapp.dto.response.ErrorResponseDTO;
import com.project.myapp.dto.response.ResponseDTO;
import com.project.myapp.exception.ServiceException;
import com.project.myapp.util.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerController {

    private final MessageSource messageSource;

    @ExceptionHandler(ServiceException.class)
    protected ResponseEntity<ResponseDTO<Object>> handleException(ServiceException exception) {
        log.debug("Erro ao tentar processar a requisição " + exception);

        String message = messageSource.getMessage(exception.getCodeErrors(), exception.getParameters(), LocaleContextHolder.getLocale());
        ErrorResponseDTO error = new ErrorResponseDTO(exception.getCodeErrors() + ": " + message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.withError(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ResponseDTO<Object>> handleException(MethodArgumentNotValidException exception) {
        log.debug("Erro ao tentar validar a requisição " + exception);

        List<ErrorResponseDTO> errors = new ArrayList<>();
        BindingResult bindingResult = exception.getBindingResult();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();

            errors.add(new ErrorResponseDTO(field, message));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.withErrors(errors));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseDTO<Object>> handleException(Exception exception) {
        log.debug("Erro não esperado ao processar a requisição." + exception);

        String message = messageSource.getMessage(ErrorMessage.INTERNAL_SERVER_ERROR.getMessage(), null, LocaleContextHolder.getLocale());
        ErrorResponseDTO error =
                new ErrorResponseDTO(ErrorMessage.INTERNAL_SERVER_ERROR.getMessage() + ": " + message);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.withError(error));
    }


}
