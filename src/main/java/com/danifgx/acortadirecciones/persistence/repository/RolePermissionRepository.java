package com.danifgx.acortadirecciones.persistence.repository;

import com.danifgx.acortadirecciones.entity.RolePermission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RolePermissionRepository extends MongoRepository<RolePermission, String> {
    Optional<RolePermission> findByRoleName(String roleName);
}
