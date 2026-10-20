package training.pokemonapp.controller;


import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import training.pokemonapp.dto.CreateFavoriteRequest;
import training.pokemonapp.model.Favorite;
import training.pokemonapp.service.FavoriteService;

@RestController
@RequestMapping("/api/collection")
@Validated
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public Favorite addFavorite(@RequestBody @Valid CreateFavoriteRequest favoriteDto) {
        return favoriteService.addFavorite(favoriteDto);
    }
}
