package com.example.messengertgnk.repository;

import com.example.messengertgnk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Secured("ROLE_ADMIN")
@Repository
@RepositoryRestResource(collectionResourceRel = "users", path = "api/users")
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findUserByUsername(String username);
}
