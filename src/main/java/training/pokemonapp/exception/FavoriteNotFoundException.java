package training.pokemonapp.exception;

public class FavoriteNotFoundException extends NotFoundException {
    public FavoriteNotFoundException(String message) {
        super("This favorite " + message + " is not found");
    }
}
