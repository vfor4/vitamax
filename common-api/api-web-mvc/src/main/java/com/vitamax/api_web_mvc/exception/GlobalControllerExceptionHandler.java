package com.vitamax.api_web_mvc.exception;

import com.vitamax.api.exception.HttpErrorInfo;
import com.vitamax.api.exception.InvalidInputException;
import com.vitamax.api.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<HttpErrorInfo> handleNotFoundExceptions(final HttpServletRequest request, final NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createHttpErrorInfo(HttpStatus.NOT_FOUND, request, ex.getMessage()));
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<HttpErrorInfo> handleInvalidInputException(final HttpServletRequest request, final InvalidInputException ex) {
        return ResponseEntity.unprocessableEntity().body(createHttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error(ex.getMessage(), ex);

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(
            @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        final var message = body instanceof ProblemDetail problemDetail ? problemDetail.getDetail() : "";
        final var errorBody = createHttpErrorInfo(HttpStatus.valueOf(statusCode.value()), ((ServletWebRequest) request).getRequest(), message);
        return new ResponseEntity<>(errorBody, headers, statusCode);
    }

    private HttpErrorInfo createHttpErrorInfo(
            final HttpStatus httpStatus, final HttpServletRequest request, final String message) {

        final String path = request.getRequestURI();

        log.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
        return new HttpErrorInfo(httpStatus, path, message);
    }
}
