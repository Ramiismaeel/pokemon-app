package training.pokemonapp.dto;

import org.springframework.data.annotation.Id;

import java.util.List;

public record PokemonDto(@Id String pokemonId, String pokemonName, String pictureUrl, int height, int weight, List<String> types) {
}
