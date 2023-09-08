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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@Testcontainers
public class UrlRepositoryIntegrationTest {

    @Container
    public GenericContainer mongo = new GenericContainer("mongo:6.0.6")
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

    @Test
    public void testSaveUrl() {
        Url url = new Url();
        url.setShortenedUrlId("newId");
        Url savedUrl = urlRepository.save(url);

        assertEquals("newId", savedUrl.getShortenedUrlId());
    }

    @Test
    public void testFindById() {
        Url url = new Url();
        url.setShortenedUrlId("anotherId");
        Url savedUrl = urlRepository.save(url);

        Optional<Url> retrievedUrl = urlRepository.findById(savedUrl.getId());

        assertTrue(retrievedUrl.isPresent());
        assertEquals("anotherId", retrievedUrl.get().getShortenedUrlId());
    }

    @Test
    public void testUpdateUrl() {
        Url url = new Url();
        url.setShortenedUrlId("oldId");
        Url savedUrl = urlRepository.save(url);

        savedUrl.setShortenedUrlId("updatedId");
        urlRepository.save(savedUrl);

        Optional<Url> retrievedUrl = urlRepository.findById(savedUrl.getId());

        assertTrue(retrievedUrl.isPresent());
        assertEquals("updatedId", retrievedUrl.get().getShortenedUrlId());
    }

    @Test
    public void testDeleteUrl() {
        Url url = new Url();
        url.setShortenedUrlId("toDeleteId");
        Url savedUrl = urlRepository.save(url);

        urlRepository.deleteById(savedUrl.getId());

        Optional<Url> retrievedUrl = urlRepository.findById(savedUrl.getId());

        assertTrue(retrievedUrl.isEmpty());
    }

    @Test
    public void testFindByExpiryDateBefore() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);

        Url url1 = new Url();
        url1.setShortenedUrlId("id1");
        url1.setExpiryDate(oneHourAgo);
        urlRepository.save(url1);

        Url url2 = new Url();
        url2.setShortenedUrlId("id2");
        url2.setExpiryDate(now.plusHours(1)); // En el futuro
        urlRepository.save(url2);

        List<Url> expiredUrls = urlRepository.findByExpiryDateBefore(now);

        assertEquals(1, expiredUrls.size());
        assertEquals("id1", expiredUrls.get(0).getShortenedUrlId());
    }
}
