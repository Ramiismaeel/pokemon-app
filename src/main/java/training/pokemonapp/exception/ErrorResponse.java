package training.pokemonapp.exception;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp,
        Map<String, List<String>> details
) {
}
