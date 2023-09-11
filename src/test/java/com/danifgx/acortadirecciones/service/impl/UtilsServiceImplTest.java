package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.service.UtilsService;
import com.danifgx.acortadirecciones.service.impl.UtilsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UtilsServiceImplTest {

    private UtilsService utilsService;

    @BeforeEach
    void setUp() {
        utilsService = new UtilsServiceImpl();
    }

    @Test
    void generateRandomId_length32_returnsStringLength32() {
        String generatedId = utilsService.generateRandomId(32);
        assertEquals(32, generatedId.length());
    }

    @Test
    void generateRandomId_length24_returnsStringLength24() {
        String generatedId = utilsService.generateRandomId(24);
        assertEquals(24, generatedId.length());
    }

    @Test
    void generateRandomId_length16_returnsStringLength16() {
        String generatedId = utilsService.generateRandomId(16);
        assertEquals(16, generatedId.length());
    }

    @Test
    void generateRandomId_length8_returnsStringLengt8h() {
        String generatedId = utilsService.generateRandomId(8);
        assertEquals(8, generatedId.length());
    }

    @Test
    void generateRandomId_length0_returnsEmptyString() {
        String generatedId = utilsService.generateRandomId(0);
        assertEquals(0, generatedId.length());
    }

    @Test
    void generateRandomId_negativeLength_returnsEmptyString() {
        String generatedId = utilsService.generateRandomId(-1);
        assertEquals(0, generatedId.length());
    }
}
