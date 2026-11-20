package com.va4815.springresourcebackend.repository;

import com.va4815.springresourcebackend.entity.Restaurant;
import com.va4815.springresourcebackend.enumeration.RestaurantType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RestaurantRepositoryTest {

    private static final String EXISTING_RESTAURANT_NAME = "Seventh Heaven";
    private static final String NON_EXISTENT_RESTAURANT_NAME = "Unknown Restaurant";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void setUp() {
        Restaurant restaurant = createRestaurant();
        restaurantRepository.saveAndFlush(restaurant);
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
    void whenExistsByName_thenReturnTrue() {
        boolean exists = restaurantRepository.existsByName(EXISTING_RESTAURANT_NAME);

        assertTrue(exists);
    }

    @Test
    void whenNotExistsByName_thenReturnFalse() {
        boolean exists = restaurantRepository.existsByName(NON_EXISTENT_RESTAURANT_NAME);

        assertFalse(exists);
    }

}
