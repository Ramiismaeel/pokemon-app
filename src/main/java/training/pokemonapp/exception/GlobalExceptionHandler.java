package training.pokemonapp.exception;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(PokemonApiUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleBadGateWay(PokemonApiUnavailableException ex) {
        return build(HttpStatus.BAD_GATEWAY, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, List<String>> fieldErrors = new TreeMap<>();

        for(FieldError error: ex.getBindingResult().getFieldErrors()) {
            String field = error.getField();
            String message = error.getDefaultMessage();

            fieldErrors.computeIfAbsent(field, k-> new ArrayList<>()).add(message);
        }
        return build(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(HandlerMethodValidationException ex) {
        Map<String, List<String>> parameterErrors = new TreeMap<>();

        for (ParameterValidationResult result : ex.getParameterValidationResults()) {
            String parameter = result.getMethodParameter().getParameterName();

            for (MessageSourceResolvable error : result.getResolvableErrors()) {
                parameterErrors.computeIfAbsent(parameter, k -> new ArrayList<>())
                        .add(error.getDefaultMessage());
            }
        }
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), parameterErrors);
    }
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, Map<String, List<String>> details) {
        ErrorResponse response = new ErrorResponse(status.value(), status.getReasonPhrase(), message, Instant.now(), details);

        return ResponseEntity.status(status).body(response);
    }
}
