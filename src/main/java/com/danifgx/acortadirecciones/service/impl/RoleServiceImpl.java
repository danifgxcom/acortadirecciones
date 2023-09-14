package com.danifgx.acortadirecciones.service.impl;

import com.danifgx.acortadirecciones.entity.Role;
import com.danifgx.acortadirecciones.persistence.repository.RoleRepository;
import com.danifgx.acortadirecciones.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
}
