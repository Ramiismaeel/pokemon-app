package training.pokemonapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import training.pokemonapp.model.Favorite;
import training.pokemonapp.repo.FavoriteRepository;
import training.pokemonapp.service.IdService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class FavoriteControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    FavoriteRepository favRepo;


    Favorite favorite = new Favorite("1", "2", "Pikachu", "MyStar", "photo.png", 20, 4, List.of("electric"));
    @BeforeEach
    void setUp() {
        favRepo.deleteAll();
    }

    @Test
    void addFavorite_shouldReturnNewPokemonCollectionWithRandomId() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders.post("/api/collection").contentType(MediaType.APPLICATION_JSON).content(
                        """
                            {"pokemonName" : "fearow" , "nickname": "Best"}
                        """
                ))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
            """
                            {
                            "pokemonId": "22",
                               "pokemonName": "fearow",
                               "nickname": "Best",
                               "pictureUrl": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/22.png",
                               "height": 12,
                               "weight": 380,
                               "types": [
                                   "normal",
                                   "flying"
                               ]
                            }
                        """
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty());

    }

    @Test
    void addFavorite_shouldThrowExceptionByNotValidRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/collection").contentType(MediaType.APPLICATION_JSON).content(
                        """
                            {"pokemonName" : "fearow" , "nickname": ""}
                        """
                ))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"));

    }
}