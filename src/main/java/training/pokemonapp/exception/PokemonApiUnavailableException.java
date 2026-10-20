package training.pokemonapp.exception;

public class PokemonApiUnavailableException extends RuntimeException{

    public PokemonApiUnavailableException(String message) {
        super(message);
    }
}
