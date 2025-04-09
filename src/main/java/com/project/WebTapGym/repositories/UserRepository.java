package com.project.WebTapGym.repositories;

import com.project.WebTapGym.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>
{
    boolean existsByPhone(String phone);

    Optional<User> findByPhone(String phone);
}
