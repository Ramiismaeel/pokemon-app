package training.pokemonapp.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import training.pokemonapp.dto.PokemonDto;
import training.pokemonapp.dto.PokemonResponse;
import training.pokemonapp.exception.PokemonApiUnavailableException;
import training.pokemonapp.exception.PokemonNotFoundException;

import java.util.List;

@Service
public class PokemonService {

    private final RestClient restClient;


    public PokemonService(RestClient.Builder restClient) {
        this.restClient = restClient
                .baseUrl("https://pokeapi.co/api/v2/pokemon")
                .build();
    }

    public PokemonDto getPokemonByName(String name) {
        PokemonResponse response = restClient.get()
                .uri("/"+ name)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(),  (req, res) -> {
                    throw new PokemonNotFoundException(name);
                })
                .body(PokemonResponse.class);
        if(response == null) {
            throw new PokemonApiUnavailableException("Bad getWay, the api not respond");
        }
        return getDto(response);

    }

    private PokemonDto getDto(PokemonResponse response) {
        String pictureUrl;
        if(response.sprites() == null || response.sprites().other() == null || response.sprites().other().officialArtwork() == null) {
            pictureUrl = null;
        } else {
            pictureUrl = response.sprites().other().officialArtwork().frontDefault();
        }

        List<String> types = response.types().isEmpty() ? List.of() : response.types()
                .stream()
                .map(t -> t.type().name()).toList();

        return new PokemonDto(String.valueOf(response.id()), response.name(), pictureUrl, response.height(), response.weight(), types);

    }
}
