package training.pokemonapp.model;

import lombok.Builder;
import lombok.With;

import java.util.List;

@Builder
public record Favorite(String id, String pokemonId, String pokemonName, @With String nickname, String pictureUrl, int height, int weight, List<String> types) {
}
