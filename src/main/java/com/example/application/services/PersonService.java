package com.example.application.services;

import com.example.application.data.Person;
import com.example.application.data.PersonDetails;
import com.example.application.data.Tag;
import com.example.application.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public List<Person> getAllPersons(String firstNameFilter, String lastNameFilter, String emailFilter, Tag tagFilter, LocalDate birthDateFilter, String addressFilter, String phoneNumberFilter) {
        Stream<Person> personStream;

        // Sähköpostisuodatin
        if (emailFilter != null && !emailFilter.isEmpty()) {
            return personRepository.findByEmailContainingIgnoreCase(emailFilter);
        } else {

            Iterable<Person> personsIterable = personRepository.findAll();
            personStream = StreamSupport.stream(personsIterable.spliterator(), false);
        }

        // Suodatetaan etunimen mukaan
        if (firstNameFilter != null && !firstNameFilter.isEmpty()) {
            personStream = personStream.filter(p -> p.getFirstName().toLowerCase().contains(firstNameFilter.toLowerCase()));
        }

        // Suodatetaan sukunimen mukaan
        if (lastNameFilter != null && !lastNameFilter.isEmpty()) {
            personStream = personStream.filter(p -> p.getLastName().toLowerCase().contains(lastNameFilter.toLowerCase()));
        }

        // Suodatetaan tagin mukaan
        if (tagFilter != null) {
            personStream = personStream.filter(p -> p.getTags().contains(tagFilter));
        }

        // Suodatetaan syntymäajan mukaan
        if (birthDateFilter != null) {
            personStream = personStream.filter(p -> p.getBirthDate().equals(birthDateFilter));
        }

        // Suodatetaan osoitteen mukaan
        if (addressFilter != null && !addressFilter.isEmpty()) {
            personStream = personStream.filter(p -> {
                PersonDetails details = p.getPersonDetails(); // Hakee PersonDetails
                return details != null && details.getAddress().toLowerCase().contains(addressFilter.toLowerCase());
            });
        }

        // Suodatetaan puhelinnumeron mukaan
        if (phoneNumberFilter != null && !phoneNumberFilter.isEmpty()) {
            personStream = personStream.filter(p -> {
                PersonDetails details = p.getPersonDetails(); // Hakee PersonDetails
                return details != null && details.getPhoneNumber().contains(phoneNumberFilter);
            });
        }

        // Palautetaan listana
        return personStream.collect(Collectors.toList());
    }

    public Person getPersonById(Long id) {
        return personRepository.findById(id).orElse(null);
    }

    // Tallentaa henkilön
    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    // Poistaa henkilön ID:n perusteella
    @DeleteMapping("/{id}")
    public void deletePerson(@PathVariable Long id) {
        personRepository.deleteById(id);
    }

    // Suodatus etunimen mukaan
    public List<Person> filterPersonsByFirstName(String firstName) {
        return personRepository.findByFirstNameContainingIgnoreCase(firstName);
    }

    // Suodatus sukunimen mukaan
    public List<Person> filterPersonsByLastName(String lastName) {
        return personRepository.findByLastNameContainingIgnoreCase(lastName);
    }

    // Lisää henkilön
    public Person addPerson(String firstName, String lastName, String email) {
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmail(email);
        return savePerson(person);
    }
}

