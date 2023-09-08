package com.danifgx.acortadirecciones.repository;

import com.danifgx.acortadirecciones.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class UserRepositoryIntegrationTest {

    @Container
    public GenericContainer mongo = new GenericContainer("mongo:6.0.6")
            .withExposedPorts(27017);

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        String uri = "mongodb://" + mongo.getContainerIpAddress() + ":" + mongo.getMappedPort(27017) + "/test";
        System.setProperty("MONGO_URI", uri);
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setEmail("test@example.com");
        User savedUser = userRepository.save(user);
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    public void testFindByEmail() {
        User user = new User();
        user.setEmail("findme@example.com");
        userRepository.save(user);

        Optional<User> retrievedUser = userRepository.findByEmail("findme@example.com");

        assertTrue(retrievedUser.isPresent());
        assertEquals("findme@example.com", retrievedUser.get().getEmail());
    }
}
