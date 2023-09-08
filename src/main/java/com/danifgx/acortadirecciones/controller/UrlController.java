package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.dao.UrlRequest;
import com.danifgx.acortadirecciones.service.iface.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class UrlController {

    private final Logger logger = LoggerFactory.getLogger(UrlController.class);
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @RequestMapping("/url")
    @PostMapping
    public ResponseEntity<Map<String, String>> shortenUrl(@RequestBody @Valid UrlRequest urlRequest) {
        String originalUrl = urlRequest.getUrl().trim();
        int expirationHours = urlRequest.getExpirationHours();
        int length = urlRequest.getLength();

        logger.info("Received request to shorten URL: {}", originalUrl);
        String id = urlService.shortenUrl(originalUrl, expirationHours, length);
        logger.info("Shortened URL with ID: {}", id);

        Map<String, String> response = new HashMap<>();
        response.put("shortUrl", id);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Void> getAndRedirect(@PathVariable String id, HttpServletResponse httpServletResponse) throws IOException {
        logger.info("Received request to redirect for ID: {}", id);
        String originalUrl = urlService.getOriginalUrl(id);
        logger.info("Redirecting to original URL: {}", originalUrl);
        httpServletResponse.sendRedirect(originalUrl);
        return new ResponseEntity<>(HttpStatus.FOUND);
    }

    @GetMapping("/resolve/{id}")
    public ResponseEntity<Map<String, String>> resolveUrl(@PathVariable String id) {
        logger.info("Received request to resolve URL for ID: {}", id);
        String originalUrl = urlService.getOriginalUrl(id);
        logger.info("Resolved to original URL: {}", originalUrl);
        Map<String, String> response = new HashMap<>();
        response.put("longUrl", originalUrl);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
