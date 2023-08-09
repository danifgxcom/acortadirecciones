package com.danifgx.acortadirecciones.repository;

import com.danifgx.acortadirecciones.entity.Url;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@Testcontainers
public class UrlRepositoryIntegrationTest {

    @Container
    public GenericContainer mongo = new GenericContainer("mongo:4.0.10")
            .withExposedPorts(27017);

    @Autowired
    private UrlRepository urlRepository;

    @BeforeEach
    public void setUp() {
        String uri = "mongodb://" + mongo.getContainerIpAddress() + ":" + mongo.getMappedPort(27017) + "/test";
        System.setProperty("MONGO_URI", uri);
    }

    @AfterEach
    public void cleanUp() {
        urlRepository.deleteAll();
    }

    @Test
    public void testFindByShortenedUrlId() {
        Url url = new Url();
        url.setShortenedUrlId("testId");
        urlRepository.save(url);

        Optional<Url> retrievedUrl = urlRepository.findByShortenedUrlId("testId");

        assertTrue(retrievedUrl.isPresent());
        assertEquals("testId", retrievedUrl.get().getShortenedUrlId());
    }
}
