package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.dao.UrlRequest;
import com.danifgx.acortadirecciones.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@RestController
@RequestMapping("/url")
public class UrlController {

    private final Logger logger = LoggerFactory.getLogger(UrlController.class);
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<String> shortenUrl(@RequestBody @Valid UrlRequest urlRequest) {
        String originalUrl = urlRequest.getUrl().trim();
        int expirationHours = urlRequest.getExpirationHours();

        logger.info("Received request to shorten URL: {}", originalUrl);
        String id = urlService.shortenUrl(originalUrl, expirationHours);
        logger.info("Shortened URL with ID: {}", id);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> getAndRedirect(@PathVariable String id, HttpServletResponse httpServletResponse) throws IOException {
        logger.info("Received request to redirect for ID: {}", id);
        String originalUrl = urlService.getOriginalUrl(id);
        logger.info("Redirecting to original URL: {}", originalUrl);
        httpServletResponse.sendRedirect(originalUrl);
        return new ResponseEntity<>(HttpStatus.FOUND);
    }
}
