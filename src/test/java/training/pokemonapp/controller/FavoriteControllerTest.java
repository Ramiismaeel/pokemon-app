package training.pokemonapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import training.pokemonapp.model.Favorite;
import training.pokemonapp.repo.FavoriteRepository;
import training.pokemonapp.service.IdService;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class FavoriteControllerTest {

    private static final String POKEAPI_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final String FEAROW_ARTWORK =
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/22.png";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    FavoriteRepository favRepo;

    @Autowired
    MockRestServiceServer mockServer;


    Favorite favorite = new Favorite("1", "2", "Pikachu", "MyStar", "photo.png", 20, 4, List.of("electric"));
    @BeforeEach
    void setUp() {
        favRepo.deleteAll();
    }

    @Test
    void addFavorite_shouldReturnNewPokemonCollectionWithRandomId() throws Exception {
        mockServer.expect(requestTo(POKEAPI_URL + "fearow"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                            "id": 22,
                            "name": "fearow",
                            "height": 12,
                            "weight": 380,
                            "sprites": {
                                "other": {
                                    "official-artwork": { "front_default": "%s" }
                                }
                            },
                            "types": [
                                { "type": { "name": "normal" } },
                                { "type": { "name": "flying" } }
                            ]
                        }
                        """.formatted(FEAROW_ARTWORK), MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pokemonName": "fearow", "nickname": "Best"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                            "pokemonId": "22",
                            "pokemonName": "fearow",
                            "nickname": "Best",
                            "pictureUrl": "%s",
                            "height": 12,
                            "weight": 380,
                            "types": ["normal", "flying"]
                        }
                        """.formatted(FEAROW_ARTWORK)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty());

        mockServer.verify();
        // really stored in MongoDB?
        assertEquals(1, favRepo.count());

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

    @Test
    void addFavorite_shouldReturn404AndSaveNothing_whenPokemonDoesNotExist() throws Exception {
        mockServer.expect(
                requestTo(POKEAPI_URL + "notapokemon"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pokemonName": "notapokemon", "nickname": "Ghost"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404));

        mockServer.verify();
        assertEquals(0, favRepo.count());
    }

    @Test
    void addFavorite_shouldReturn400_whenNicknameIsBlank() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pokemonName": "fearow", "nickname": ""}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation failed"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.nickname", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.pokemonName").doesNotExist());

        // validation fails before the service runs -> no PokéAPI call, nothing saved
        assertEquals(0, favRepo.count());
    }

    @Test
    void addFavorite_shouldReturn400_whenBothFieldsAreBlank() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pokemonName": "", "nickname": ""}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.pokemonName").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.nickname").exists());
    }

    @Test
    void addFavorite_shouldReturn400_whenNicknameIsTooShort() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/collection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pokemonName": "pikachu", "nickname": "A"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.details.nickname", hasSize(1)));
    }

    // ---------------------------------------------------------------- GET all

    @Test
    void getAll_shouldReturnEmptyList_whenCollectionIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/collection"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    void getAll_shouldReturnAllFavorites() throws Exception {
        Favorite second = new Favorite("2", "22", "fearow", "Best", FEAROW_ARTWORK, 12, 380, List.of("normal", "flying"));
        favRepo.saveAll(List.of(favorite, second));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/collection"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[*].id", containsInAnyOrder("1", "2")));
    }

    // ---------------------------------------------------------------- GET by id

    @Test
    void getById_shouldReturnFavorite_whenIdExists() throws Exception {
        favRepo.save(favorite);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/collection/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                            "id": "1",
                            "pokemonId": "2",
                            "pokemonName": "Pikachu",
                            "nickname": "MyStar",
                            "pictureUrl": "photo.png",
                            "height": 20,
                            "weight": 4,
                            "types": ["electric"]
                        }
                        """));
    }

    @Test
    void getById_shouldReturn404_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/collection/unknown-id"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").isNotEmpty());
    }

    // ---------------------------------------------------------------- PUT

    @Test
    void updateFavorite_shouldChangeOnlyNickname() throws Exception {
        favRepo.save(favorite);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/collection/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nickname": "Arena Champion"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                            "id": "1",
                            "pokemonId": "2",
                            "pokemonName": "Pikachu",
                            "nickname": "Arena Champion",
                            "pictureUrl": "photo.png",
                            "height": 20,
                            "weight": 4,
                            "types": ["electric"]
                        }
                        """));

        // updated in the DB, not inserted as a second entry
        assertEquals(1, favRepo.count());
        assertEquals("Arena Champion", favRepo.findById("1").orElseThrow().nickname());
    }

    @Test
    void updateFavorite_shouldReturn404_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/collection/unknown-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nickname": "Arena Champion"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        assertEquals(0, favRepo.count());
    }

    @Test
    void updateFavorite_shouldReturn400AndKeepOldNickname_whenNicknameIsTooShort() throws Exception {
        favRepo.save(favorite);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/collection/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nickname": "A"}
                                """))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("400 BAD_REQUEST \"Validation failure\""));

        assertEquals("MyStar", favRepo.findById("1").orElseThrow().nickname());
    }

    // ---------------------------------------------------------------- DELETE

    @Test
    void deleteFavorite_shouldRemoveFavorite_whenIdExists() throws Exception {
        favRepo.save(favorite);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/collection/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertFalse(favRepo.existsById("1"));
    }

    @Test
    void deleteFavorite_shouldReturn404_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/collection/unknown-id"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404));
    }


}