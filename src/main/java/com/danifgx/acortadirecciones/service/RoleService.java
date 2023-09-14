package com.danifgx.acortadirecciones.service;

import com.danifgx.acortadirecciones.entity.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(String name);
}
