package training.pokemonapp.service;

import org.springframework.stereotype.Service;
import training.pokemonapp.dto.CreateFavoriteRequest;
import training.pokemonapp.dto.PokemonDto;
import training.pokemonapp.dto.UpdateFavoriteRequest;
import training.pokemonapp.exception.FavoriteNotFoundException;
import training.pokemonapp.model.Favorite;
import training.pokemonapp.repo.FavoriteRepository;

import java.util.List;

@Service
public class FavoriteService {

    private final IdService idService;
    private final FavoriteRepository favoriteRepository;
    private final PokemonService pokemonService;

    public FavoriteService(IdService idService, FavoriteRepository favoriteRepository, PokemonService pokemonService) {
        this.idService = idService;
        this.favoriteRepository = favoriteRepository;
        this.pokemonService = pokemonService;
    }


    public Favorite addFavorite(CreateFavoriteRequest favDto) {
        PokemonDto pokemonDto = pokemonService.getPokemonByName(favDto.pokemonName());

        Favorite dto = new Favorite(
                idService.randomId(),
                pokemonDto.pokemonId(),
                pokemonDto.pokemonName(),
                favDto.nickname(),
                pokemonDto.pictureUrl(),
                pokemonDto.height(),
                pokemonDto.weight(),
                pokemonDto.types()
        );

        return favoriteRepository.save(dto);
    }

    public List<Favorite> getAllFavorite() {
        return favoriteRepository.findAll();
    }

    public Favorite getById(String id) {
        return favoriteRepository.findById(id).orElseThrow(()-> new FavoriteNotFoundException(id));
    }

    public String deleteFavorite(String id) {
        if(!favoriteRepository.existsById(id)) {
            throw new FavoriteNotFoundException(id);
        }
        favoriteRepository.deleteById(id);
        return "Deleted successfully";
    }

    public Favorite updateFavorite(String id, UpdateFavoriteRequest request) {
        Favorite newFavorite = favoriteRepository.findById(id).orElseThrow(() -> new FavoriteNotFoundException(id));
        return favoriteRepository.save(newFavorite.withNickname(request.nickname()));
    }
}
