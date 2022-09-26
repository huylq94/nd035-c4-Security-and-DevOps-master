package com.example.demo.controller;


import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "userRepository", userRepository);
    }

    @Test
    public void testAddToCart() {
        Cart userCart = new Cart();
        User user = createUser();
        user.setCart(userCart);
        Item item = createItem();
        List<Item> listItems = new ArrayList<>();
        listItems.add(item);
        listItems.add(item);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setUsername("test");

        modifyCartRequest.setItemId(item.getId());
        modifyCartRequest.setQuantity(2);
        ResponseEntity<Cart> responseEntity = cartController.addToCart(modifyCartRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Cart cart = responseEntity.getBody();

        assertNotNull(cart);
        assertEquals(new BigDecimal(398), cart.getTotal());
        assertEquals(listItems, cart.getItems());

    }

    @Test
    public void testRemoveFromCart() {
        Cart userCart = new Cart();

        User user = createUser();
        user.setCart(userCart);

        Item item = createItem();
        List<Item> listItems = new ArrayList<>();
        listItems.add(item);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setUsername("test");

        ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);
        Cart cart = response.getBody();

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(0, cart.getItems().size());
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .username("test")
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .id(1L)
                .name("Adidas Super Star")
                .price(new BigDecimal(199))
                .build();
    }
}
