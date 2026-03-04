package io.github.ruanvasco.api.repository;

import io.github.ruanvasco.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
