package com.danifgx.acortadirecciones.controller;

import com.danifgx.acortadirecciones.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/url")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<String> shortenUrl(@RequestBody String url) {
        String id = urlService.shortenUrl(url);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> getAndRedirect(@PathVariable String id, HttpServletResponse httpServletResponse) throws IOException {
        String originalUrl = urlService.getOriginalUrl(id);
        httpServletResponse.sendRedirect(originalUrl);
        return new ResponseEntity<>(HttpStatus.FOUND);
    }
}
