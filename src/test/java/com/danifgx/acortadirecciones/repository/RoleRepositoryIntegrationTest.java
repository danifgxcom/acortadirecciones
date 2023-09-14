package com.danifgx.acortadirecciones.repository;

import com.danifgx.acortadirecciones.entity.Role;
import com.danifgx.acortadirecciones.persistence.repository.RoleRepository;
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
public class RoleRepositoryIntegrationTest {

    @Container
    public GenericContainer mongo = new GenericContainer("mongo:6.0.6")
            .withExposedPorts(27017);

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        String uri = "mongodb://" + mongo.getContainerIpAddress() + ":" + mongo.getMappedPort(27017) + "/test";
        System.setProperty("MONGO_URI", uri);
    }

    @AfterEach
    public void cleanUp() {
        roleRepository.deleteAll();
    }

    @Test
    public void testSaveRole() {
        Role role = new Role();
        role.setName("ADMIN");
        Role savedRole = roleRepository.save(role);
        assertEquals("ADMIN", savedRole.getName());
    }

    @Test
    public void testFindByName() {
        Role role = new Role();
        role.setName("USER");
        roleRepository.save(role);

        Optional<Role> retrievedRole = roleRepository.findByName("USER");

        assertTrue(retrievedRole.isPresent());
        assertEquals("USER", retrievedRole.get().getName());
    }
}
