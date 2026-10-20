package training.pokemonapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import training.pokemonapp.dto.PokemonDto;
import training.pokemonapp.service.PokemonService;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final PokemonService service;

    public PokemonController(PokemonService service) {
        this.service = service;
    }

    @GetMapping("/{name}")
    public PokemonDto getByName(@PathVariable String name) {
        return service.getPokemonByName(name);
    }
}
