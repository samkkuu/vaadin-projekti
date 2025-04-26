package com.example.application.views;

import com.example.application.data.Person;
import com.example.application.data.PersonDetails;
import com.example.application.services.PersonDetailsService;
import com.example.application.services.PersonService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Location;

@Route("person-details")
public class PersonDetailsView extends VerticalLayout {

    private PersonDetailsService personDetailsService;
    private PersonService personService;
    private TextField addressField;
    private TextField phoneNumberField;
    private Button saveButton;
    private Button deleteButton;
    private Long personId;

    public PersonDetailsView(PersonDetailsService personDetailsService, PersonService personService) {
        this.personDetailsService = personDetailsService;
        this.personService = personService;

        addressField = new TextField("Address");
        phoneNumberField = new TextField("Phone Number");
        saveButton = new Button("Save");
        deleteButton = new Button("Delete");

        saveButton.addClickListener(e -> savePersonDetails());
        deleteButton.addClickListener(e -> deletePersonDetails());

        FormLayout formLayout = new FormLayout(addressField, phoneNumberField, saveButton, deleteButton);
        add(formLayout);


        personId = extractPersonIdFromRoute();
        if (personId != null) {
            loadPersonDetails(personId);
        }
    }

    private Long extractPersonIdFromRoute() {
        Location location = UI.getCurrent().getInternals().getActiveViewLocation();
        if (location != null && location.getQueryParameters().getParameters().containsKey("personId")) {
            try {
                return Long.parseLong(location.getQueryParameters().getParameters().get("personId").get(0));
            } catch (NumberFormatException e) {
                Notification.show("Invalid person ID in URL");
            }
        }
        return null;
    }

    private void loadPersonDetails(Long personId) {
        PersonDetails personDetails = personDetailsService.getPersonDetails(personId);
        if (personDetails != null) {
            addressField.setValue(personDetails.getAddress());
            phoneNumberField.setValue(personDetails.getPhoneNumber());
        }
    }

    private void savePersonDetails() {

        if (personId != null) {
            Person person = personService.getPersonById(personId); // Hae henkilö ID:llä
            if (person != null) {
                PersonDetails personDetails = new PersonDetails();
                personDetails.setAddress(addressField.getValue());
                personDetails.setPhoneNumber(phoneNumberField.getValue());
                personDetails.setPerson(person); // Liitä tiedot henkilöön
                personDetailsService.savePersonDetails(personId, personDetails); // Tallentaa tiedot
            } else {
                Notification.show("Person not found.");
            }
        }
    }

    private void deletePersonDetails() {
        if (personId != null) {
            personDetailsService.deletePersonDetails(personId); // Poistaa tiedot
        }
    }
}
