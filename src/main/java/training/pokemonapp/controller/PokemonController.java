package training.pokemonapp.controller;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import training.pokemonapp.dto.PokemonDto;
import training.pokemonapp.service.PokemonService;

@RestController
@RequestMapping("/api/pokemon")
@Validated
public class PokemonController {

    private final PokemonService pokemonService;


    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/{name}")
    public PokemonDto getByNameOrId(@PathVariable @NotBlank String name) {
        return pokemonService.getPokemonByName(name);
    }


}
