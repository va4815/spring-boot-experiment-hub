package com.va4815.springresourcebackend.service;

import com.va4815.springresourcebackend.entity.Restaurant;
import com.va4815.springresourcebackend.enumeration.RestaurantType;
import com.va4815.springresourcebackend.model.CurrentUser;
import com.va4815.springresourcebackend.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {
    private static final String EXISTING_RESTAURANT_NAME = "Seventh Heaven";

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = createRestaurant();
    }

    private CurrentUser currentUser() {
        return new CurrentUser("TEST001", "Tester", "test@example.com");
    }

    private Restaurant createRestaurant() {
        LocalDateTime now = LocalDateTime.now();

        Restaurant restaurant = new Restaurant();
        restaurant.setName(EXISTING_RESTAURANT_NAME);
        restaurant.setRestaurantType(RestaurantType.FAST_FOOD.name());
        restaurant.setLocation("Test Location");
        restaurant.setCreatedAt(now);
        restaurant.setCreatedBy("Test User");
        restaurant.setUpdatedAt(now);
        restaurant.setUpdatedBy("Test User");

        return restaurant;
    }

    @Test
    public void getAllRestaurants_shouldReturnAllRestaurants() {
        List<Restaurant> expectedRestaurants = List.of(restaurant);
        when(restaurantRepository.findAll()).thenReturn(expectedRestaurants);

        List<Restaurant> actualRestaurants = restaurantService.getAllRestaurants();

        assertEquals(expectedRestaurants, actualRestaurants);
        verify(restaurantRepository).findAll();
    }

    @Test
    public void getAllRestaurants_shouldReturnEmptyList() {

    }

    @Test
    public void getRestaurantById_shouldReturnRestaurantById() {

    }

    @Test
    public void getRestaurantById_shouldReturnNull_whenRestaurantDoesNotExist() {

    }

    @Test
    public void createRestaurant_shouldCreateSuccess() {

    }

    @Test
    public void createRestaurant_shouldCreateRestaurantAlreadyExists() {

    }

}
