package training.pokemonapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFavoriteRequest(

        @NotBlank
        String pokemonName,
        @NotBlank
        @Size(min = 3, message = "it should be 3 characters at least")
        String nickname
) {
}
