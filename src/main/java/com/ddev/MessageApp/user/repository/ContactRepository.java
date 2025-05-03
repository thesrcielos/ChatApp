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
    Page<ContactEntity> findByUserIdAndStatus(Integer userId, Status status, Pageable pageable);
    Page<ContactEntity> findByContactIdAndStatus(Integer userId, Status status, Pageable pageable);
    Optional<ContactEntity> findByUserIdAndContactId(Integer userId, Integer contactId);

    @Query(value = "SELECT c.user FROM ContactEntity c WHERE c.id = :cId")
    Optional<UserEntity> findUserFromContact(@Param("cId") Integer cId);

    @Query("SELECT c FROM ContactEntity c WHERE c.user.id = :userId AND (LOWER(c.contact.email) LIKE LOWER(CONCAT('%', :pattern, '%')) OR LOWER(c.contact.name) LIKE LOWER(CONCAT('%', :pattern, '%')))")
    Page<ContactEntity> searchContacts(@Param("userId") Integer userId, @Param("pattern") String pattern, Pageable pageable);

    @Query(
            value = """
        SELECT * FROM contacts
        WHERE user_id = :userId AND status = :status
        ORDER BY id
        """,
            countQuery = """
        SELECT COUNT(*) FROM contacts
        WHERE user_id = :userId AND status = :status
        """,
            nativeQuery = true
    )
    Page<ContactEntity> findNativeContactsByUserIdAndStatus(
            @Param("userId") Integer userId,
            @Param("status") Status status,
            Pageable pageable
    );

}
