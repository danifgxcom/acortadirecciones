package com.danifgx.acortadirecciones.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private String name;
    private List<Integer> urlLengths;
    private List<Integer> expirationTimes;

}
