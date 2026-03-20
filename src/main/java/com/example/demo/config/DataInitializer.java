package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Migrate existing roles to have ROLE_ prefix if they don't
        ensureRole("ROLE_ADMIN", "ADMIN", "Administrator role with full access");
        ensureRole("ROLE_USER", "USER", "Regular user role for customers");
        ensureRole("ROLE_MANAGER", "MANAGER", "Manager role with product editing rights");
    }

    private void ensureRole(String newName, String oldName, String description) {
        // If neither new name nor old name exists, create it
        if (roleRepository.findByName(newName).isEmpty()) {
            roleRepository.findByName(oldName).ifPresentOrElse(
                role -> {
                    role.setName(newName);
                    roleRepository.save(role);
                },
                () -> {
                    Role newRole = new Role();
                    newRole.setName(newName);
                    newRole.setDescription(description);
                    roleRepository.save(newRole);
                }
            );
        }
    }
}
