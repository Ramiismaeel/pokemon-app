
package training.pokemonapp.dto;

import java.util.List;

public record PokemonResponse(
        int id,
        String name,
        int height,
        int weight,
        Sprites sprites,
        List<TypeSlot> types
) {}
