package com.ddev.MessageApp.user.repository;

import com.ddev.MessageApp.user.model.ContactEntity;
import com.ddev.MessageApp.user.model.Status;
import com.ddev.MessageApp.user.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Integer> {
    Page<ContactEntity> findByIdAndStatus(Integer userId, Status status, Pageable pageable);
    @Query("SELECT c.user FROM CONTACTS c WHERE c.id = :cId")
    Optional<UserEntity> findUserFromContact(@Param("profesorId") Integer cId);
}
