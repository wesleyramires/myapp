package com.project.myapp.repository;

import com.project.myapp.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByEmail(String email);

    @Query("select c from Contact c where person_id = ?1 and c.email = ?2")
    Optional<Contact> findByEmailAndByPersonId(Long idPerson, String email);

    @Query("select c from Contact c where person_id = ?1 and c.phone = ?2")
    Optional<Contact> findByPhoneAndByPersonId(Long idPerson, String phone);

    @Query("select c from Contact c where c.id = ?1 and person_id = ?2")
    Optional<Contact> findByIdAndPersonId(Long idContact, Long idPerson);
 }
