package com.va4815.springresourcebackend.service;

import com.va4815.springresourcebackend.dto.request.CreateRestaurantRequestDTO;
import com.va4815.springresourcebackend.entity.Restaurant;
import com.va4815.springresourcebackend.enumeration.RestaurantType;
import com.va4815.springresourcebackend.exception.GlobalArgumentException;
import com.va4815.springresourcebackend.model.CurrentUser;
import com.va4815.springresourcebackend.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
        List<Restaurant> expectedRestaurants = List.of();
        when(restaurantRepository.findAll()).thenReturn(expectedRestaurants);

        List<Restaurant> actualRestaurants = restaurantService.getAllRestaurants();

        assertEquals(expectedRestaurants, actualRestaurants);
        verify(restaurantRepository).findAll();
    }

    @Test
    public void getRestaurantById_shouldReturnRestaurantById() {
        long restaurantId = 1L;
        restaurant.setId(restaurantId);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        Restaurant actualRestaurant = restaurantService.getRestaurantById(restaurantId);

        assertEquals(restaurant, actualRestaurant);
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    public void getRestaurantById_shouldThrowNotFound_whenRestaurantDoesNotExist() {
        long restaurantId = 999L;
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        GlobalArgumentException exception = assertThrows(
                GlobalArgumentException.class,
                () -> restaurantService.getRestaurantById(restaurantId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Restaurant not found", exception.getMessage());
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    public void createRestaurant_shouldCreateSuccess() {
        String username = "Tester";
        CreateRestaurantRequestDTO request = new CreateRestaurantRequestDTO(
                "New Restaurant",
                RestaurantType.CAFE,
                "New Location"
        );
        when(restaurantRepository.existsByName(request.name())).thenReturn(false);
        when(currentUser.getUsername()).thenReturn(username);
        when(restaurantRepository.save(any(Restaurant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Restaurant createdRestaurant = restaurantService.createRestaurant(request);

        ArgumentCaptor<Restaurant> restaurantCaptor = ArgumentCaptor.forClass(Restaurant.class);
        verify(restaurantRepository).existsByName(request.name());
        verify(restaurantRepository).save(restaurantCaptor.capture());

        Restaurant savedRestaurant = restaurantCaptor.getValue();
        assertSame(savedRestaurant, createdRestaurant);
        assertEquals(request.name(), savedRestaurant.getName());
        assertEquals(request.restaurantType().name(), savedRestaurant.getRestaurantType());
        assertEquals(request.location(), savedRestaurant.getLocation());
        assertEquals(username, savedRestaurant.getCreatedBy());
        assertEquals(username, savedRestaurant.getUpdatedBy());
        assertNotNull(savedRestaurant.getCreatedAt());
        assertNotNull(savedRestaurant.getUpdatedAt());

    }

    @Test
    public void createRestaurant_shouldCreateRestaurantAlreadyExists() {
        CreateRestaurantRequestDTO request = new CreateRestaurantRequestDTO(
                EXISTING_RESTAURANT_NAME,
                RestaurantType.FAST_FOOD,
                "Test Location"
        );
        when(restaurantRepository.existsByName(request.name())).thenReturn(true);

        GlobalArgumentException exception = assertThrows(
                GlobalArgumentException.class,
                () -> restaurantService.createRestaurant(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Restaurant with the same name already exists", exception.getMessage());
        verify(restaurantRepository).existsByName(request.name());
        verify(restaurantRepository, never()).save(any(Restaurant.class));
        verifyNoInteractions(currentUser);

    }

}
