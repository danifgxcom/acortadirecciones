package com.danifgx.acortadirecciones.persistence.dao;

import com.danifgx.acortadirecciones.entity.RolePermission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolePermissionDao extends MongoRepository<RolePermission, String> {
    Optional<RolePermission> findByRoleName(String roleName);
}
