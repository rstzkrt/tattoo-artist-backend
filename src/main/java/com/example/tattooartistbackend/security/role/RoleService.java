package com.example.tattooartistbackend.security.role;

public interface RoleService {
    void addRole(String uid, Role role) throws Exception;

    void removeRole(String uid, Role role);

    boolean isAdmin(String uid);
}
