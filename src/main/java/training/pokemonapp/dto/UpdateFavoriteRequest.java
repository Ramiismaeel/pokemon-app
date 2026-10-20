package training.pokemonapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateFavoriteRequest(@NotBlank @Size(min = 3 , message = "must be at least 3 characters long") String nickname) {
}
