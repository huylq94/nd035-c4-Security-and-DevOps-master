package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testGetItems() {
        Item item = createItem();
        List<Item> items = new ArrayList<>();
        items.add(item);
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> responseBody = response.getBody();
        Assert.assertEquals(items, responseBody);
    }

    @Test
    public void testGetItemById() {
        Item item = createItem();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ResponseEntity<Item> response = itemController.getItemById(item.getId());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item itemFound = response.getBody();
        assertNotNull(itemFound);
        assertEquals(item.getId(), itemFound.getId());
        assertEquals("Adidas Super Star", itemFound.getName());
        assertEquals(item.getPrice(), itemFound.getPrice());
    }

    @Test
    public void testGetItemsByName() {
        Item item = createItem();
        List<Item> items = new ArrayList<>();
        items.add(item);
        when(itemRepository.findByName("Adidas Super Star")).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Adidas Super Star");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> itemsFound = response.getBody();
        assertNotNull(itemsFound);
        assertEquals(item.getId(), itemsFound.get(0).getId());
        assertEquals(item.getName(), itemsFound.get(0).getName());
        assertEquals(item.getPrice(), itemsFound.get(0).getPrice());
    }

    private Item createItem() {
        return Item.builder()
                .id(1L)
                .name("Adidas Super Star")
                .price(new BigDecimal(199))
                .build();
    }
}
