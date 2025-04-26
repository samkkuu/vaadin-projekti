package com.example.application.services;

import com.example.application.data.Person;
import com.example.application.data.PersonDetails;
import com.example.application.repositories.PersonDetailsRepository;
import com.example.application.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PersonDetailsService {

    @Autowired
    private PersonDetailsRepository personDetailsRepository;

    @Autowired
    private PersonRepository personRepository;

    public PersonDetails savePersonDetails(Long personId, PersonDetails personDetails) {

        // Haetaan henkilö ID:llä
        Optional<Person> personOpt = personRepository.findById(personId);

        if (personOpt.isPresent()) {
            // Asetetaan henkilö PersonDetails-olioille
            personDetails.setPerson(personOpt.get());

            // Tallennetaan PersonDetails
            return personDetailsRepository.save(personDetails);
        } else {
            // Jos henkilöä ei löydy, palautetaan null
            return null;
        }
    }

    public PersonDetails getPersonDetails(Long personId) {
        return personDetailsRepository.findByPersonId(personId).orElse(null);
    }

    public void deletePersonDetails(Long personId) {
        PersonDetails personDetails = getPersonDetails(personId);
        if (personDetails != null) {
            personDetailsRepository.delete(personDetails); // Poistaa tiedot
        }
    }
}
