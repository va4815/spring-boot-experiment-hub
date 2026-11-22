package com.va4815.springresourcebackend.controller;

import com.va4815.springresourcebackend.dto.request.CreateRestaurantRequestDTO;
import com.va4815.springresourcebackend.entity.Restaurant;
import com.va4815.springresourcebackend.enumeration.RestaurantType;
import com.va4815.springresourcebackend.exception.GlobalArgumentException;
import com.va4815.springresourcebackend.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.web.OAuth2ResourceServerWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = RestaurantController.class,
        excludeAutoConfiguration = {
                OAuth2ResourceServerAutoConfiguration.class,
                OAuth2ResourceServerWebSecurityAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

    private static final String BASE_URL = "/restaurants";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestaurantService restaurantService;

    private Restaurant createRestaurant(Long id, String name, RestaurantType type, String location) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setName(name);
        restaurant.setRestaurantType(type.name());
        restaurant.setLocation(location);
        return restaurant;
    }

    // ---------- GET /restaurants ----------

    @Test
    void getRestaurants_shouldReturnAllRestaurants() throws Exception {
        List<Restaurant> restaurants = List.of(
                createRestaurant(1L, "Seventh Heaven", RestaurantType.FAST_FOOD, "Midgar"),
                createRestaurant(2L, "Blue Dragon", RestaurantType.PUBS, "Midgar")
        );
        when(restaurantService.getAllRestaurants()).thenReturn(restaurants);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Seventh Heaven"))
                .andExpect(jsonPath("$[0].restaurantType").value("FAST_FOOD"))
                .andExpect(jsonPath("$[0].location").value("Midgar"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Blue Dragon"))
                .andExpect(jsonPath("$[1].restaurantType").value("PUBS"))
                .andExpect(jsonPath("$[1].location").value("Midgar"));

        verify(restaurantService).getAllRestaurants();
        verifyNoMoreInteractions(restaurantService);
    }

    @Test
    void getRestaurants_shouldReturnEmptyList() throws Exception {
        when(restaurantService.getAllRestaurants()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(restaurantService).getAllRestaurants();
        verifyNoMoreInteractions(restaurantService);
    }

    // ---------- GET /restaurants/{restaurant_id} ----------

    @Test
    void getRestaurant_shouldReturnRestaurantById() throws Exception {
        Long restaurantId = 1L;
        Restaurant restaurant = createRestaurant(restaurantId, "Seventh Heaven", RestaurantType.FAST_FOOD, "Midgar");
        when(restaurantService.getRestaurantById(restaurantId)).thenReturn(restaurant);

        mockMvc.perform(get(BASE_URL + "/{restaurant_id}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value(restaurantId))
                .andExpect(jsonPath("$.name").value("Seventh Heaven"))
                .andExpect(jsonPath("$.restaurantType").value("FAST_FOOD"))
                .andExpect(jsonPath("$.location").value("Midgar"));

        verify(restaurantService).getRestaurantById(restaurantId);
        verifyNoMoreInteractions(restaurantService);
    }

    @Test
    void getRestaurant_shouldReturnError_whenRestaurantDoesNotExist() throws Exception {
        Long restaurantId = 999L;
        when(restaurantService.getRestaurantById(restaurantId))
                .thenThrow(new GlobalArgumentException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        mockMvc.perform(get(BASE_URL + "/{restaurant_id}", restaurantId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.title").value("Restaurant not found"))
                .andExpect(jsonPath("$.status").value("404 NOT_FOUND"));

        verify(restaurantService).getRestaurantById(restaurantId);
        verifyNoMoreInteractions(restaurantService);
    }

    @Test
    void getRestaurant_shouldReturnBadRequest_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{restaurant_id}", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(restaurantService);
    }

    // ---------- POST /restaurants ----------

    @Test
    void createRestaurant_shouldCreateSuccess() throws Exception {
        CreateRestaurantRequestDTO request = new CreateRestaurantRequestDTO(
                "Seventh Heaven", RestaurantType.FAST_FOOD, "Midgar");
        Restaurant createdRestaurant = createRestaurant(1L, "Seventh Heaven", RestaurantType.FAST_FOOD, "Midgar");
        when(restaurantService.createRestaurant(request)).thenReturn(createdRestaurant);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Seventh Heaven"))
                .andExpect(jsonPath("$.restaurantType").value("FAST_FOOD"))
                .andExpect(jsonPath("$.location").value("Midgar"));

        verify(restaurantService).createRestaurant(request);
        verifyNoMoreInteractions(restaurantService);
    }

    @Test
    void createRestaurant_shouldReturnBadRequest_whenRestaurantAlreadyExists() throws Exception {
        CreateRestaurantRequestDTO request = new CreateRestaurantRequestDTO(
                "Seventh Heaven", RestaurantType.FAST_FOOD, "Midgar");
        when(restaurantService.createRestaurant(request))
                .thenThrow(new GlobalArgumentException(HttpStatus.BAD_REQUEST, "Restaurant with the same name already exists"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.title").value("Restaurant with the same name already exists"))
                .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));

        verify(restaurantService).createRestaurant(request);
        verifyNoMoreInteractions(restaurantService);
    }

    @Test
    void createRestaurant_shouldReturnBadRequest_whenRequestBodyIsInvalid() throws Exception {
    }

    @Test
    void createRestaurant_shouldReturnBadRequest_whenRequestBodyIsMalformed() throws Exception {
    }

}
