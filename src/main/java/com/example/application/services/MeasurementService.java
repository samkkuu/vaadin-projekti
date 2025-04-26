package com.example.application.services;

import com.example.application.api.PersonNotFoundException;
import com.example.application.data.Measurement;
import com.example.application.data.Person;
import com.example.application.repositories.MeasurementRepository;
import com.example.application.repositories.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepository;
    private final PersonRepository personRepository;

    public MeasurementService(MeasurementRepository measurementRepository, PersonRepository personRepository) {
        this.measurementRepository = measurementRepository;
        this.personRepository = personRepository;
    }

    public List<Measurement> getMeasurementsByPerson(Long personId) {
        return measurementRepository.findByPersonId(personId);
    }

    public Measurement saveMeasurement(Measurement measurement, Long personId) {

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + personId));

        measurement.setPerson(person);

        // Tallennetaan mittaus ja palautetaan se
        return measurementRepository.save(measurement);
    }


    public Measurement updateMeasurement(Long id, Measurement updatedMeasurement, Long personId) {
        Measurement existing = measurementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Measurement not found"));

        existing.setType(updatedMeasurement.getType());
        existing.setMeasurementValue(updatedMeasurement.getMeasurementValue());
        existing.setDate(updatedMeasurement.getDate());

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));
        existing.setPerson(person);

        return measurementRepository.save(existing);
    }

    public void deleteMeasurement(Long id) {
        measurementRepository.deleteById(id);
    }
}

