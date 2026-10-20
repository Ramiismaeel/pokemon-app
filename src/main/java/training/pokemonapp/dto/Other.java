package training.pokemonapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Other(
        @JsonProperty("official-artwork") OfficialArtwork officialArtwork
) {}
