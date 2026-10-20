package training.pokemonapp.model;

import java.util.List;

public record Favorite(String id, String pokemonId, String pokemonName, String nickname, String pictureUrl, int height, int weight, List<String> types) {
}
