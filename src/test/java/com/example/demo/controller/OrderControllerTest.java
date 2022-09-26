package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObject(orderController,"orderRepository",orderRepository);
        TestUtils.injectObject(orderController,"userRepository",userRepository);
    }

    @Test
    public void testSubmit() {
        Item item = createItem();
        List<Item> items = new ArrayList<>();
        items.add(item);
        User user = createUser();
        Cart cart = createCart(items, user);
        user.setCart(cart);
        when(userRepository.findByUsername("test")).thenReturn(user);
        final ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();

        assertEquals(user, order.getUser());
        assertEquals(items, order.getItems());
        assertEquals(item.getPrice(), order.getTotal());

    }

    @Test
    public void testGetOrdersForUser() {
        Item item = createItem();
        List<Item> items = new ArrayList<>();
        items.add(item);
        User user = createUser();
        Cart cart = createCart(items, user);
        user.setCart(cart);
        when(userRepository.findByUsername("test")).thenReturn(user);
        orderController.submit("test");
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
    }

    private Item createItem() {
        return Item.builder()
                .id(1L)
                .name("Adidas Super Star")
                .price(new BigDecimal(199))
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .username("test")
                .build();
    }

    private Cart createCart(List<Item> items, User user) {
        return Cart.builder()
                .id(1L)
                .items(items)
                .user(user)
                .total(items.stream().map(Item::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add))
                .build();
    }
}
