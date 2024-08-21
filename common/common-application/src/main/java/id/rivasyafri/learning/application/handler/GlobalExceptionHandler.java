package id.rivasyafri.learning.application.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  @ResponseBody
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO handleException(Exception exception) {
    log.error(exception.getMessage(), exception);
    return ErrorDTO.builder()
        .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .message("Unexpected error!")
        .build();
  }

  @ResponseBody
  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDTO handleException(ValidationException validationException) {
    ErrorDTO errorResponse;
    if (validationException instanceof ConstraintViolationException constraintViolationException) {
      String violations = extractViolationsFromException(constraintViolationException);
      log.error(violations, validationException);
      errorResponse = ErrorDTO.builder()
          .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
          .message(violations)
          .build();
    } else {
      log.error(validationException.getMessage(), validationException);
      errorResponse = ErrorDTO.builder()
          .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
          .message(validationException.getMessage())
          .build();
    }
    return errorResponse;
  }

  private String extractViolationsFromException(ConstraintViolationException validationException) {
    return validationException.getConstraintViolations()
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining("--"));
  }
}
