package training.pokemonapp.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PokemonNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(PokemonNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    @ExceptionHandler(PokemonApiUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleBadGetWay(PokemonApiUnavailableException ex) {
        return build(HttpStatus.BAD_GATEWAY, ex.getMessage(), List.of());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, List<String> details) {
        ErrorResponse response = new ErrorResponse(status.value(), status.getReasonPhrase(), message, Instant.now(), details);

        return ResponseEntity.status(status).body(response);
    }
}
