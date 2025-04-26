package com.example.application.repositories;

import com.example.application.data.PersonDetails;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface PersonDetailsRepository extends CrudRepository<PersonDetails, Long> {
    Optional<PersonDetails> findByPersonId(Long personId);
}
