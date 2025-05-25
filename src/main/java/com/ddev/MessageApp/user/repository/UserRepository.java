package com.ddev.MessageApp.user.repository;

import com.ddev.MessageApp.user.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<UserEntity> findByEmailContainingIgnoreCaseAndNameContainingIgnoreCase(String email,String name, Pageable pageable);

    @Query("""
            SELECT u FROM UserEntity u
            WHERE u.id IN :userIds
            """)
    List<UserEntity> getUserInfo(@Param("userIds") List<Integer> ids);
}
