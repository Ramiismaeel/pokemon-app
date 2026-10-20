package training.pokemonapp.exception;

public class PokemonNotFoundException extends NotFoundException{

    public PokemonNotFoundException(String name) {
        super("Pokemon: " + name + " is not found");
    }
}
