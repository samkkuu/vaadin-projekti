package com.example.application.api;

import com.example.application.data.Measurement;
import com.example.application.data.Person;
import com.example.application.services.MeasurementService;
import com.example.application.repositories.PersonRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;
    private final PersonRepository personRepository;

    public MeasurementController(MeasurementService measurementService, PersonRepository personRepository) {
        this.measurementService = measurementService;
        this.personRepository = personRepository;
    }

    @GetMapping("/person/{personId}")
    public List<Measurement> getByPerson(@PathVariable Long personId) {
        return measurementService.getMeasurementsByPerson(personId);
    }

    @PutMapping("/{id}")
    public Measurement update(@PathVariable Long id, @RequestBody MeasurementWithPerson measurementWithPerson) {
        return measurementService.updateMeasurement(id, measurementWithPerson.getMeasurement(), measurementWithPerson.getPersonId());
    }

    @PostMapping
    public Measurement create(@RequestBody MeasurementWithPerson measurementWithPerson) {
        Long personId = measurementWithPerson.getPersonId();

        // Etsi henkilö tietokannasta
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException("Person not found with id: " + personId));

        // Luodaan mittaus ja liitetään se henkilöön
        Measurement measurement = measurementWithPerson.getMeasurement();
        measurement.setPerson(person);  // Liitetään mittaus oikeaan henkilöön

        // Tallenna
        return measurementService.saveMeasurement(measurement, person.getId());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        measurementService.deleteMeasurement(id);
    }

    public static class MeasurementWithPerson {
        private Measurement measurement;
        private Long personId;

        // Getters ja setters
        public Measurement getMeasurement() {
            return measurement;
        }

        public void setMeasurement(Measurement measurement) {
            this.measurement = measurement;
        }

        public Long getPersonId() {
            return personId;
        }

        public void setPersonId(Long personId) {
            this.personId = personId;
        }
    }
}