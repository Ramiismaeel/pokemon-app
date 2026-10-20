package training.pokemonapp.dto;

import java.util.List;

public record PokemonDto( String pokemonId, String pokemonName, String pictureUrl, int height, int weight, List<String> types) {
}
