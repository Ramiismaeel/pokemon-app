package training.pokemonapp.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import training.pokemonapp.model.Favorite;

public interface FavoriteRepository extends MongoRepository<Favorite, String> {
}
