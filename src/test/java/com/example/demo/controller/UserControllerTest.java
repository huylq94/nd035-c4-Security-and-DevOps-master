package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObject(userController,"userRepository",userRepo);
        TestUtils.injectObject(userController,"cartRepository",cartRepo);
        TestUtils.injectObject(userController,"bCryptPasswordEncoder",encoder);
    }

    @Test
    public void create_user_happy_path() throws  Exception{
        when(encoder.encode("test1234")).thenReturn("thisIsHashed");
        CreateUserRequest r = createUserRequest();

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test",u.getUsername());
        assertEquals("thisIsHashed",u.getPassword());
    }

    @Test
    public void testFindById() {
        when(encoder.encode("test1234")).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest = createUserRequest();
        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test",u.getUsername());
        assertEquals("thisIsHashed",u.getPassword());

        when(userRepo.findById(0L)).thenReturn(Optional.of(u));
        final ResponseEntity<User> response2 = userController.findById(u.getId());
        User userFound = response2.getBody();
        assertEquals(200, response2.getStatusCodeValue());
        assertEquals(0, userFound.getId());
        assertEquals("test", userFound.getUsername());
        assertEquals("thisIsHashed", userFound.getPassword());
    }


    @Test
    public void testFindByUsername() {
        when(encoder.encode("test1234")).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest = createUserRequest();
        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test",u.getUsername());
        assertEquals("thisIsHashed",u.getPassword());

        when(userRepo.findByUsername("test")).thenReturn(u);
        final ResponseEntity<User> response2 = userController.findByUserName(u.getUsername());
        User userFound = response2.getBody();
        assertEquals(200, response2.getStatusCodeValue());
        assertEquals(0, userFound.getId());
        assertEquals("test", userFound.getUsername());
        assertEquals("thisIsHashed", userFound.getPassword());
    }


    private CreateUserRequest createUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("test1234");
        createUserRequest.setConfirmPassword("test1234");
        return createUserRequest;
    }
}
