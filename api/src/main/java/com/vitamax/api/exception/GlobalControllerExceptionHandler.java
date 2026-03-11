package com.vitamax.api.exception;

import com.vitamax.api.exception.dto.HttpErrorInfo;
import com.vitamax.api.exception.dto.InvalidInputException;
import com.vitamax.api.exception.dto.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<HttpErrorInfo>> handleNotFoundExceptions(final ServerWebExchange request, final NotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(createHttpErrorInfo(HttpStatus.NOT_FOUND, request, ex.getMessage())));
    }

    @ExceptionHandler(InvalidInputException.class)
    public Mono<ResponseEntity<HttpErrorInfo>> handleInvalidInputException(final ServerWebExchange request, final InvalidInputException ex) {
        return Mono.just(ResponseEntity.unprocessableEntity().body(createHttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, request, ex.getMessage())));
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, ServerWebExchange request) {
        log.error(ex.getMessage(), ex);

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    @Override
    protected Mono<ResponseEntity<Object>> createResponseEntity(
            @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, ServerWebExchange request) {
        final var message = body instanceof ProblemDetail problemDetail ? problemDetail.getDetail() : "";

        final var errorBody = createHttpErrorInfo(HttpStatus.valueOf(statusCode.value()), request, message);

        return Mono.just(new ResponseEntity<>(errorBody, headers, statusCode));
    }

    private HttpErrorInfo createHttpErrorInfo(final HttpStatus httpStatus, final ServerWebExchange request,
                                              final String message) {
        final var path = request.getRequest().getURI().getPath();

        log.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
        return new HttpErrorInfo(httpStatus, path, message);
    }
}
