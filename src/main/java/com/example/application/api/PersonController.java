package com.example.application.api;

import com.example.application.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    // Poistaa henkilön ID:n perusteella
    @DeleteMapping("/{id}")  // DELETE-pyyntö reitille /persons/{id}
    public void deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
    }
}

