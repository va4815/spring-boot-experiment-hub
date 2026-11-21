package com.va4815.springresourcebackend.controller;

import com.va4815.springresourcebackend.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {

    private static final String BASE_URL = "/restaurants";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestaurantService restaurantService;

    // ---------- GET /restaurants ----------

    @Test
    void getRestaurants_shouldReturnAllRestaurants() throws Exception {
    }

    @Test
    void getRestaurants_shouldReturnEmptyList() throws Exception {
    }

    // ---------- GET /restaurants/{restaurant_id} ----------

    @Test
    void getRestaurant_shouldReturnRestaurantById() throws Exception {
    }

    @Test
    void getRestaurant_shouldReturnError_whenRestaurantDoesNotExist() throws Exception {
    }

    @Test
    void getRestaurant_shouldReturnBadRequest_whenIdIsNotANumber() throws Exception {
    }

    // ---------- POST /restaurants ----------

    @Test
    void createRestaurant_shouldCreateSuccess() throws Exception {
    }

    @Test
    void createRestaurant_shouldReturnBadRequest_whenRestaurantAlreadyExists() throws Exception {
    }

    @Test
    void createRestaurant_shouldReturnBadRequest_whenRequestBodyIsInvalid() throws Exception {
    }

    @Test
    void createRestaurant_shouldReturnBadRequest_whenRequestBodyIsMalformed() throws Exception {
    }

}
