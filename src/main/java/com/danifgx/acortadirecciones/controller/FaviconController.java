package com.danifgx.acortadirecciones.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {

    @GetMapping("/favicon.ico")
    public ResponseEntity<Resource> favicon() {
        Resource resource = new ClassPathResource("/static/favicon.ico");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/x-icon")
                .body(resource);
    }
}
