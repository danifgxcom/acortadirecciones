package com.danifgx.acortadirecciones.persistence.dao;

import com.danifgx.acortadirecciones.entity.Role;

import java.util.Optional;

public interface RoleDao {
    Optional<Role> findByName(String name);
    Role save(Role role);
}
