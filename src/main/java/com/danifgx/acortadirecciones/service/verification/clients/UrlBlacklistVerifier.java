package com.danifgx.acortadirecciones.service.verification.clients;

import com.danifgx.acortadirecciones.service.verification.VerificationResponse;
import com.danifgx.acortadirecciones.service.verification.iface.UrlVerifier;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class UrlBlacklistVerifier implements UrlVerifier {

    @Value("${blacklist.file.path}")
    private String blacklistFilePath;
    private Set<String> blacklistedUrls;


    @PostConstruct
    public void init() {
        loadBlacklistedUrls();
    }

    @Override
    public VerificationResponse verify(String url) {
        log.info("Verifying URL against blacklist: {}", url);
        if (blacklistedUrls.contains(url)) {
            log.warn("URL verification failed: {}. Blacklisted.", url);
            return new VerificationResponse(false, "URLBlacklistVerifier");
        }
        log.info("URL passed blacklist verification: {}", url);
        return new VerificationResponse(true, "");
    }

    private void loadBlacklistedUrls() {
        blacklistedUrls = new HashSet<>();
        try {
            ClassPathResource resource = new ClassPathResource(blacklistFilePath);
            try (BufferedReader br = new BufferedReader(new FileReader(resource.getFile()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    blacklistedUrls.add(line.trim());
                }
                log.info("Successfully loaded blacklisted URLs from file.");
            }
        } catch (IOException e) {
            log.error("Failed to load blacklisted URLs from file: {}", e.getMessage());
        }
    }
}
