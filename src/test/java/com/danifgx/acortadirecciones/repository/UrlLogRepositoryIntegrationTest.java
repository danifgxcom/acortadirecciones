package com.danifgx.acortadirecciones.repository;

import com.danifgx.acortadirecciones.entity.UrlLog;
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
public class UrlLogRepositoryIntegrationTest {

    @Container
    public GenericContainer mongo = new GenericContainer("mongo:6.0.6")
            .withExposedPorts(27017);

    @Autowired
    private UrlLogRepository urlLogRepository;

    @BeforeEach
    public void setUp() {
        String uri = "mongodb://" + mongo.getContainerIpAddress() + ":" + mongo.getMappedPort(27017) + "/test";
        System.setProperty("MONGO_URI", uri);
    }

    @AfterEach
    public void cleanUp() {
        urlLogRepository.deleteAll();
    }

    @Test
    public void testSaveUrlLog() {
        UrlLog urlLog = new UrlLog();
        urlLog.setOriginalUrl("https://example.com");
        urlLog.setShortenedUrlId("shortId");
        UrlLog savedUrlLog = urlLogRepository.save(urlLog);

        assertEquals("https://example.com", savedUrlLog.getOriginalUrl());
        assertEquals("shortId", savedUrlLog.getShortenedUrlId());
    }

    @Test
    public void testFindByOriginalUrl() {
        UrlLog urlLog = new UrlLog();
        urlLog.setOriginalUrl("https://searchme.com");
        urlLogRepository.save(urlLog);

        Optional<UrlLog> retrievedUrlLog = urlLogRepository.findByOriginalUrl("https://searchme.com");

        assertTrue(retrievedUrlLog.isPresent());
        assertEquals("https://searchme.com", retrievedUrlLog.get().getOriginalUrl());
    }

    @Test
    public void testFindByShortenedUrlId() {
        UrlLog urlLog = new UrlLog();
        urlLog.setShortenedUrlId("findmeId");
        urlLogRepository.save(urlLog);

        Optional<UrlLog> retrievedUrlLog = urlLogRepository.findByShortenedUrlId("findmeId");

        assertTrue(retrievedUrlLog.isPresent());
        assertEquals("findmeId", retrievedUrlLog.get().getShortenedUrlId());
    }
}
