package co.edu.unicauca.monitoring.tool.backend.notification.ms.handler;


import co.edu.unicauca.monitoring.tool.backend.notification.ms.config.MessageLoader;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.ApiErrorDto;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.domain.ResponseDto;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.exception.BusinessRuleException;
import co.edu.unicauca.monitoring.tool.backend.notification.ms.util.MessagesConstants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;

/**
 * Global Exception Handler to manage various exception types.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles validation errors, including MethodArgumentNotValidException and ConstraintViolationException.
     *
     * @param ex the exception thrown during validation
     * @return a ResponseDto containing the validation errors
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto<List<ApiErrorDto>> handleValidationExceptions(Exception ex) {
        List<ApiErrorDto> result;
        if (ex instanceof MethodArgumentNotValidException methodEx) {
            result = handleMethodArgumentNotValidException(methodEx);
        } else if (ex instanceof ConstraintViolationException constraintEx) {
            result = handleConstraintViolationException(constraintEx);
        } else {
            result = List.of(new ApiErrorDto("unknown", "Unknown validation error"));
        }
        return buildErrorResponse(result);
    }

    /**
     * Handles HttpMessageConversionException.
     *
     * @param ex The HttpMessageConversionException instance.
     * @return Response entity containing error details.
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto<Object> handleHttpMessageConversionException(HttpMessageConversionException ex) {
        log.error("HTTP message conversion error: {}", ex.getMessage(), ex);
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),
            MessageLoader.getInstance().getMessage(MessagesConstants.EM003, ex.getMessage()), MessagesConstants.EM003);
    }

    /**
     * Handles `MethodArgumentTypeMismatchException`.
     *
     * @param ex the exception thrown when a method argument type mismatch occurs.
     * @return a response with the error details.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseDto<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("Method argument type mismatch for property: {}. Expected type: {}",
            ex.getPropertyName(),
            Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),
            MessageLoader.getInstance().getMessage(MessagesConstants.EM011, ex.getPropertyName(),
                Objects.requireNonNull(ex.getRequiredType()).getSimpleName()));
    }


    private List<ApiErrorDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("Validation error occurred: {}", ex.getMessage());
        return ex.getBindingResult().getAllErrors().stream()
            .map(this::mapError)
            .toList();
    }

    /**
     * Handles exceptions about type EBusinessApplicationException.
     * Logs the error message and returns a response for this specific exception.
     *
     * @param e The BusinessRuleException instance.
     * @return Response entity containing error details.
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ResponseDto<Object>> handleBusinessRuleException(
        BusinessRuleException e) {
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), e.getErrorCode()).of();
    }



    private ResponseDto<List<ApiErrorDto>> buildErrorResponse(List<ApiErrorDto> errors) {
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),
            MessageLoader.getInstance().getMessage(MessagesConstants.EM009), errors);
    }

    /**
     * Maps an error object to an `ApiErrorDto`.
     *
     * @param error the error object to be mapped
     * @return an `ApiErrorDto` representing the validation error
     */
    private ApiErrorDto mapError(Object error) {
        if (error instanceof FieldError field) {
            return mapFieldError(field);
        } else if (error instanceof ConstraintViolation<?> violation) {
            return mapConstraintViolation(violation);
        }
        return new ApiErrorDto("unknown", "Unknown validation error");
    }

    private ApiErrorDto mapFieldError(FieldError field) {
        String message = MessageLoader.getInstance().getMessage(field.getDefaultMessage(),
            field.getField());
        return new ApiErrorDto(field.getField(), message);
    }

    private ApiErrorDto mapConstraintViolation(ConstraintViolation<?> violation) {
        String message = MessageLoader.getInstance().getMessage(violation.getMessage(),
            violation.getPropertyPath().toString());
        return new ApiErrorDto(violation.getPropertyPath().toString(), message);
    }

    private List<ApiErrorDto> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        return ex.getConstraintViolations().stream()
            .map(this::mapError)
            .toList();
    }
}


