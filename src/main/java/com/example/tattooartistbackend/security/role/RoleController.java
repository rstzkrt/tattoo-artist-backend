package com.example.tattooartistbackend.security.role;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PutMapping
    public void addRole(@RequestParam String uid, @RequestParam Role role) throws Exception {
        roleService.addRole(uid, role);
    }

    @DeleteMapping
    public void removeRole(@RequestParam String uid, @RequestParam Role role) {
        roleService.removeRole(uid, role);
    }
}