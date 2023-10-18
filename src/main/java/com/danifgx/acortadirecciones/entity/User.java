package com.danifgx.acortadirecciones.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "users")
@RequiredArgsConstructor
public class User {
    private String id;
    private String email;
    private String username;
    private String password;
    private String source;
    private List<Role> roles;


}