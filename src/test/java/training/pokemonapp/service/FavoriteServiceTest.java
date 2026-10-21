package training.pokemonapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import training.pokemonapp.dto.CreateFavoriteRequest;
import training.pokemonapp.dto.PokemonDto;
import training.pokemonapp.dto.UpdateFavoriteRequest;
import training.pokemonapp.exception.FavoriteNotFoundException;
import training.pokemonapp.model.Favorite;
import training.pokemonapp.repo.FavoriteRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;


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

    Favorite favorite = new Favorite("1", "25", "pikachu", "MyStar", "photo.png", 4, 60, List.of("electric"));

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

    @Test
    void getAllFavorite_shouldReturnAllFavoritesFromRepository() {
        Mockito.when(mockRepo.findAll()).thenReturn(List.of(favorite));

        assertEquals(List.of(favorite), service.getAllFavorite());

        Mockito.verify(mockRepo).findAll();
        Mockito.verifyNoInteractions(pokemonService);
    }

    @Test
    void getAllFavorite_shouldReturnEmptyList_whenRepositoryIsEmpty() {
        Mockito.when(mockRepo.findAll()).thenReturn(List.of());

        assertTrue(service.getAllFavorite().isEmpty());
    }


    @Test
    void getById_shouldReturnFavorite_whenIdExists() {
        Mockito.when(mockRepo.findById("1")).thenReturn(Optional.of(favorite));

        assertEquals(favorite, service.getById("1"));

        Mockito.verifyNoInteractions(pokemonService);
    }

    @Test
    void getById_shouldThrow_whenIdDoesNotExist() {
        Mockito.when(mockRepo.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(FavoriteNotFoundException.class, () -> service.getById("unknown"));
    }



    @Test
    void deleteFavorite_shouldDelete_whenIdExists() {
        Mockito.when(mockRepo.existsById("1")).thenReturn(true);

        String result = service.deleteFavorite("1");

        assertEquals("Deleted successfully", result);
        Mockito.verify(mockRepo).deleteById("1");
    }

    @Test
    void deleteFavorite_shouldThrowAndDeleteNothing_whenIdDoesNotExist() {
        Mockito.when(mockRepo.existsById("unknown")).thenReturn(false);

        assertThrows(FavoriteNotFoundException.class, () -> service.deleteFavorite("unknown"));

        Mockito.verify(mockRepo, Mockito.never()).deleteById(anyString());
    }


    @Test
    void updateFavorite_shouldChangeOnlyNickname() {
        Mockito.when(mockRepo.findById("1")).thenReturn(Optional.of(favorite));

        Mockito.when(mockRepo.save(any(Favorite.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Favorite result = service.updateFavorite("1", new UpdateFavoriteRequest("Arena Champion"));


        ArgumentCaptor<Favorite> captor = ArgumentCaptor.forClass(Favorite.class);
        Mockito.verify(mockRepo).save(captor.capture());
        Favorite saved = captor.getValue();

        Favorite expected = new Favorite("1", "25", "pikachu", "Arena Champion", "photo.png", 4, 60, List.of("electric"));
        assertEquals(expected, saved);
        assertEquals(expected, result);
        Mockito.verifyNoInteractions(pokemonService);
    }

    @Test
    void updateFavorite_shouldThrowAndSaveNothing_whenIdDoesNotExist() {
        Mockito.when(mockRepo.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(FavoriteNotFoundException.class,
                () -> service.updateFavorite("unknown", new UpdateFavoriteRequest("Arena Champion")));

        Mockito.verify(mockRepo, never()).save(any());
    }
}
