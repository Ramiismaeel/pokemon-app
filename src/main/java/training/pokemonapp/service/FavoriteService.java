package training.pokemonapp.service;

import org.springframework.stereotype.Service;
import training.pokemonapp.dto.CreateFavoriteRequest;
import training.pokemonapp.dto.PokemonDto;
import training.pokemonapp.model.Favorite;
import training.pokemonapp.repo.FavoriteRepository;

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
}
