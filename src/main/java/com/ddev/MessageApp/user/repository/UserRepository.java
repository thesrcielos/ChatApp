package com.ddev.MessageApp.user.repository;

import com.ddev.MessageApp.user.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<UserEntity> findByEmailContainingIgnoreCase(String name, Pageable pageable);
}
