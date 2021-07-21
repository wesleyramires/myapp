package com.project.myapp.repository;

import com.project.myapp.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findById(Long id);

    Optional<Person> findByCpf(String cpf);

    Page<Person> findAll(Pageable pageable);

}
