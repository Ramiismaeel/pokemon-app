package training.pokemonapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import training.pokemonapp.dto.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;



@ExtendWith(MockitoExtension.class)
class PokemonServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    RestClient restClient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    RestClient.Builder builder;


    PokemonService service;

    @BeforeEach
    void setup() {
        Mockito.when(builder.baseUrl("https://pokeapi.co/api/v2/pokemon").build()).thenReturn(restClient);

        service = new PokemonService(builder);
    }

    @Test
    void getPokemonByName_shouldReturnPokemonDto_byGivenPikachu() {

        PokemonResponse response = new PokemonResponse(1, "Pikachu", 4, 60 , new Sprites(new Other( new OfficialArtwork("photo.png"))), List.of(new TypeSlot( new NamedResource("electric"))));

        Mockito.when(restClient.get().uri("/pikachu").retrieve().body(PokemonResponse.class))
                .thenReturn(response);

        PokemonDto result = service.getPokemonByName("pikachu");

        assertEquals(new PokemonDto("1", "Pikachu", "photo.png", 4, 60, List.of("electric")), result);
    }

}