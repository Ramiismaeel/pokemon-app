package training.pokemonapp.exception;

public class PokemonNotFoundException extends RuntimeException{

    public PokemonNotFoundException(String name) {
        super("Pokemon: " + name + " is not found");
    }
}
