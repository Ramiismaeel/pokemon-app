package training.pokemonapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OfficialArtwork(
        @JsonProperty("front_default") String frontDefault
) {}
