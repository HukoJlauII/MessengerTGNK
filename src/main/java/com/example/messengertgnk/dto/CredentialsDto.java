package com.example.messengertgnk.dto;

import com.example.messengertgnk.entity.Role;
import com.example.messengertgnk.entity.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link User} entity
 */
@Data
public class CredentialsDto implements Serializable {
    private final String username;
    private final String password;
    private final Set<Role> roles;

    private final String email;
}