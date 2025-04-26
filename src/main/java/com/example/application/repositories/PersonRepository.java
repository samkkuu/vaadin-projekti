package com.example.application.repositories;

import com.example.application.data.Person;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface PersonRepository extends CrudRepository<Person, Long> {

    // Hakee henkilöt, joiden etunimi sisältää hakusanan
    List<Person> findByFirstNameContainingIgnoreCase(String firstName);

    List<Person> findByEmailContainingIgnoreCase(String email);

    // Hakee henkilöt, joiden sukunimi sisältää hakusanan
    List<Person> findByLastNameContainingIgnoreCase(String lastName);
}
