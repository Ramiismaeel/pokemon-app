package training.pokemonapp.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import training.pokemonapp.dto.CreateFavoriteRequest;
import training.pokemonapp.dto.UpdateFavoriteRequest;
import training.pokemonapp.model.Favorite;
import training.pokemonapp.service.FavoriteService;

import java.util.List;

@RestController
@RequestMapping("/api/collection")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public Favorite addFavorite(@RequestBody @Valid CreateFavoriteRequest favoriteDto) {
        return favoriteService.addFavorite(favoriteDto);
    }

    @GetMapping
    public List<Favorite> getAll() {
        return favoriteService.getAllFavorite();
    }

    @GetMapping("/{id}")

    public Favorite getById(@PathVariable @NotBlank String id) {
        return favoriteService.getById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteFavorite(@PathVariable @NotBlank String id) {
        return favoriteService.deleteFavorite(id);
    }

    @PutMapping("/{id}")
    public Favorite updateFavorite(@PathVariable @NotBlank String id, @RequestBody @Valid UpdateFavoriteRequest request) {
        return favoriteService.updateFavorite(id, request);
    }

}
