package com.danifgx.acortadirecciones.service.verification.clients;

import com.danifgx.acortadirecciones.service.UtilsService;
import com.danifgx.acortadirecciones.service.verification.VerificationResponse;
import com.danifgx.acortadirecciones.service.verification.iface.UrlVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class UrlBlacklistVerifier implements UrlVerifier {


    private final String blacklistFilePath;

    private final Set<String> blacklistedUrls;

    private UtilsService utilsService;


    public UrlBlacklistVerifier(@Value("${blacklist.file.path}") String blacklistFilePath, UtilsService utilsService) {
        this.blacklistFilePath = blacklistFilePath;
        this.utilsService = utilsService;
        this.blacklistedUrls = new HashSet<>();
        try {
            ClassPathResource resource = new ClassPathResource(this.blacklistFilePath);
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

    @Override
    public VerificationResponse verify(String url) {
        try {
            String domain = utilsService.extractDomain(url);
            log.info("Verifying domain against blacklist: {}", domain);

            if (blacklistedUrls.contains(domain)) {
                log.warn("Domain verification failed: {}. Blacklisted.", domain);
                return new VerificationResponse(false, "URLBlacklistVerifier");
            }

            log.info("Domain passed blacklist verification: {}", domain);
            return new VerificationResponse(true, "");

        } catch (MalformedURLException e) {
            log.error("Invalid URL provided: {}", url);
            return new VerificationResponse(false, "URLBlacklistVerifier");
        }
    }
}
