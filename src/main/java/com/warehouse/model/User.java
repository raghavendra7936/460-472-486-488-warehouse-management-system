package com.warehouse.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Full name is required")
    @Column(nullable = false)
    private String fullName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        foreignKey = @ForeignKey(name = "FK_USER_ROLES")
    )
    @Column(name = "role", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'USER'")
    @Enumerated(EnumType.STRING)
    @NotEmpty(message = "User must have at least one role")
    private Set<Role> roles;

    @Column(nullable = false)
    private boolean enabled = true;

    public User() {
        // Initialize roles in constructor
        this.roles = new HashSet<>();
        // Set default role as USER
        this.roles.add(Role.USER);
    }

    public void addRole(Role role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        if (this.roles != null) {
            this.roles.remove(role);
        }
    }

    public boolean hasRole(Role role) {
        return this.roles != null && this.roles.contains(role);
    }
} 