package training.pokemonapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class PokemonControllerTest {

    @Autowired
    MockRestServiceServer mockRestServiceServer;

    @Autowired
    MockMvc mockMvc;

    @Test
    void getByName_shouldReturnPokemonDto_byGivenPikachu() throws Exception {

        mockRestServiceServer.expect(requestTo("https://pokeapi.co/api/v2/pokemon/pikachu"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                    """
                                {
                                    "id": 25,
                                    "name": "pikachu",
                                    "height": 4,
                                    "weight": 60,
                                     "sprites": {
                                            "other": {
                                                    "official-artwork": {
                                                            "front_default": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png"
                                                    }
                                            }
                                    },
                                    "types": [
                                        {
                                            "type": {
                                                "name": "electric"
                                            }
                                        }
                                    ]
                                }
                        """, MediaType.APPLICATION_JSON
                ));


        mockMvc.perform(MockMvcRequestBuilders.get("/api/pokemon/pikachu"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
            """
                            {
                                "pokemonId": "25",
                                "pokemonName": "pikachu",
                                "pictureUrl": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png",
                                "height": 4,
                                "weight": 60,
                                "types": [
                                    "electric"
                                ]
                            }

                        """
                ));

        mockRestServiceServer.verify();


    }
}