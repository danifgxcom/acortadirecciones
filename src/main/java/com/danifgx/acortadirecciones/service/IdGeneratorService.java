package com.danifgx.acortadirecciones.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdGeneratorService {

    public String generateRandomId() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }
}
