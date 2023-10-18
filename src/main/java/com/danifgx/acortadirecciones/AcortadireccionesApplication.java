package com.danifgx.acortadirecciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AcortadireccionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcortadireccionesApplication.class, args);
	}

}
