package training.pokemonapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import training.pokemonapp.dto.CreateFavoriteRequest;
import training.pokemonapp.dto.PokemonDto;
import training.pokemonapp.model.Favorite;
import training.pokemonapp.repo.FavoriteRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {


    @Mock
    IdService mockId;

    @Mock
    FavoriteRepository mockRepo;

    @Mock
    PokemonService pokemonService;

    @InjectMocks
    FavoriteService service;

    @BeforeEach
    void setup() {
        service = new FavoriteService( mockId, mockRepo, pokemonService);
    }

    @Test
    void addFavorite_shouldReturnPokemonItemWithRandomId() {

        Mockito.when(mockId.randomId()).thenReturn("1");

        CreateFavoriteRequest dto = new CreateFavoriteRequest("pikachu", "MyStar");

        Favorite favorite = new Favorite("1", "2", dto.pokemonName(), dto.nickname(), "photo.png", 20, 4, List.of("electric"));
        PokemonDto pokemonDto = new PokemonDto("2", dto.pokemonName(), "photo.png", 20, 4, List.of("electric"));

        Mockito.when(pokemonService.getPokemonByName("pikachu")).thenReturn(pokemonDto);
        Mockito.when(mockRepo.save(favorite)).thenReturn(favorite);

        assertEquals(favorite, service.addFavorite(dto));

        Mockito.verify(mockRepo).save(favorite);
        Mockito.verify(mockId).randomId();
        Mockito.verifyNoMoreInteractions(mockId, mockRepo);
    }
}