package com.danifgx.acortadirecciones.persistence.dao.impl;

import com.danifgx.acortadirecciones.entity.Role;
import com.danifgx.acortadirecciones.persistence.dao.RoleDao;
import com.danifgx.acortadirecciones.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RoleDaoImpl implements RoleDao {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }
}
