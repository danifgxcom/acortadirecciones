package com.danifgx.acortadirecciones.entity;

import com.danifgx.acortadirecciones.security.profile.Permission;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "roles_permissions")
@Data
@NoArgsConstructor
public class RolePermission {
    @Id
    private String id;

    private String roleName;
    private Set<Permission> permissions;

    public RolePermission(String roleName, Set<Permission> permissions) {
        this.roleName = roleName;
        this.permissions = permissions;
    }

}
