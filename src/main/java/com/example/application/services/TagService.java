package com.example.application.services;

import com.example.application.data.Person;
import com.example.application.data.Tag;
import com.example.application.repositories.PersonRepository;
import com.example.application.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PersonRepository personRepository;

    // Tallentaa
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    // Hakee
    public Tag getTag(Long id) {
        return tagRepository.findById(id).orElse(null);
    }

    // Hakee kaikki tagit
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    // Poistaa
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    public void addTagToPerson(Long personId, Long tagId) {

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        if (!person.getTags().contains(tag)) {
            person.getTags().add(tag);
            personRepository.save(person);
            System.out.println("Tag " + tag.getName() + " added to person " + person.getFullName());
        } else {
            throw new IllegalArgumentException("Tag already assigned to person");
        }
    }

    // Suodattaa Tagit nimen perusteella
    public List<Tag> filterTagsByName(String name) {
        return tagRepository.findByNameContainingIgnoreCase(name);
    }
}
