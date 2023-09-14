package com.danifgx.acortadirecciones.repository;

import com.danifgx.acortadirecciones.entity.BlacklistToken;
import com.danifgx.acortadirecciones.persistence.repository.BlacklistTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class BlacklistTokenRepositoryIntegrationTest {

    @Container
    public GenericContainer mongo = new GenericContainer("mongo:6.0.6")
            .withExposedPorts(27017);

    @Autowired
    private BlacklistTokenRepository blacklistTokenRepository;

    @BeforeEach
    public void setUp() {
        String uri = "mongodb://" + mongo.getContainerIpAddress() + ":" + mongo.getMappedPort(27017) + "/test";
        System.setProperty("MONGO_URI", uri);
    }

    @AfterEach
    public void cleanUp() {
        blacklistTokenRepository.deleteAll();
    }

    @Test
    public void testFindByToken() {
        BlacklistToken token = new BlacklistToken();
        token.setToken("some_token");
        blacklistTokenRepository.save(token);

        Optional<BlacklistToken> retrievedToken = blacklistTokenRepository.findByToken("some_token");

        assertTrue(retrievedToken.isPresent());
        assertEquals("some_token", retrievedToken.get().getToken());
    }

    @Test
    public void testFindAllByExpiryDateBefore() {
        BlacklistToken token1 = new BlacklistToken();
        token1.setExpiryDate(LocalDateTime.now().minusHours(2));
        token1.setToken("expired_token1");
        blacklistTokenRepository.save(token1);

        BlacklistToken token2 = new BlacklistToken();
        token2.setExpiryDate(LocalDateTime.now().plusHours(2));
        token2.setToken("valid_token");
        blacklistTokenRepository.save(token2);

        List<BlacklistToken> retrievedTokens = blacklistTokenRepository.findAllByExpiryDateBefore(LocalDateTime.now());

        assertEquals(1, retrievedTokens.size());
        assertEquals("expired_token1", retrievedTokens.get(0).getToken());
    }
}
