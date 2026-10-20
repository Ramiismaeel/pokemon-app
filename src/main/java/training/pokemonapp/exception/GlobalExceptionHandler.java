package training.pokemonapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PokemonNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(PokemonNotFoundException ex) {
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

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, Map<String, List<String>> details) {
        ErrorResponse response = new ErrorResponse(status.value(), status.getReasonPhrase(), message, Instant.now(), details);

        return ResponseEntity.status(status).body(response);
    }
}
