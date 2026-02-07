package com.vitamax.api_web_flux.exception;

import com.vitamax.api.exception.HttpErrorInfo;
import com.vitamax.api.exception.InvalidInputException;
import com.vitamax.api.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<Object>> handleNotFoundExceptions(final ServerWebExchange request, final NotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(createHttpErrorInfo(HttpStatus.NOT_FOUND, request, ex.getMessage())));
    }

    @ExceptionHandler(InvalidInputException.class)
    public Mono<ResponseEntity<Object>> handleInvalidInputException(final ServerWebExchange request, final InvalidInputException ex) {
        return Mono.just(ResponseEntity.unprocessableEntity().body(createHttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage())));
    }

    protected Mono<ResponseEntity<Object>> handleExceptionInternal(
            Exception ex, @Nullable Object body, @Nullable HttpHeaders headers, HttpStatusCode status,
            ServerWebExchange exchange) {
        log.error(ex.getMessage(), ex);

        return super.handleExceptionInternal(ex, body, headers, status, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> createResponseEntity(
            @Nullable Object body, @Nullable HttpHeaders headers, HttpStatusCode statusCode,
            ServerWebExchange exchange) {
        final var message = body instanceof ProblemDetail problemDetail ? problemDetail.getDetail() : "";
        final var errorBody = createHttpErrorInfo(HttpStatus.valueOf(statusCode.value()), exchange, message);
        return Mono.just(new ResponseEntity<>(errorBody, headers, statusCode));
    }

    private HttpErrorInfo createHttpErrorInfo(
            final HttpStatus httpStatus, final ServerWebExchange request, final String message) {

        final String path = request.getRequest().getURI().getPath();

        log.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
        return new HttpErrorInfo(httpStatus, path, message);
    }
}
