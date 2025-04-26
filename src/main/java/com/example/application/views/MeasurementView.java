package com.example.application.views;

import com.example.application.data.Measurement;
import com.example.application.data.Person;
import com.example.application.services.MeasurementService;
import com.example.application.services.PersonService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.util.List;

@Route(value = "measurements", layout = MainLayout.class)
@PageTitle("Mittaukset")
@CssImport("themes/my-app/custom-styles.css")
public class MeasurementView extends VerticalLayout {

    private final MeasurementService measurementService;
    private final PersonService personService;

    private ComboBox<Person> personSelector;
    private Grid<Measurement> measurementGrid;

    private TextField typeField;
    private TextField measurementValueField;
    private DatePicker dateField;
    private Button addButton;
    private Button deleteButton;
    private Button editButton;

    private TextField newFirstNameField;
    private TextField newLastNameField;
    private TextField newEmailField;
    private Button addPersonButton;

    private Measurement selectedMeasurement = null;

    public MeasurementView(MeasurementService measurementService, PersonService personService) {
        this.measurementService = measurementService;
        this.personService = personService;

        // Henkilön valinta
        personSelector = new ComboBox<>("Select person");
        personSelector.setItems(personService.getAllPersons(
                "",
                "",
                "",
                null,
                null,
                "",
                ""
        ));
        personSelector.setItemLabelGenerator(person -> person.getFirstName() + " " + person.getLastName());
        personSelector.addClassNames("mb-m", "text-l");

        personSelector.addValueChangeListener(e -> updateGrid());


        personSelector.addValueChangeListener(e -> updateGrid());

        // Grid
        measurementGrid = new Grid<>(Measurement.class, false);
        measurementGrid.addColumn(Measurement::getType).setHeader("Type");
        measurementGrid.addColumn(Measurement::getDate).setHeader("Date");
        measurementGrid.addClassName("bordered");
        measurementGrid.addClassNames("mt-m", "text-s");

        // Mittauksen lisäyskentät
        typeField = new TextField("Type");
        typeField.getStyle().set("background-color", "#f9f9f9");
        typeField.addClassName("text-l");

        measurementValueField = new TextField("Measurement Value");
        measurementValueField.addClassName("text-m");

        dateField = new DatePicker("Date");
        dateField.addClassName("bg-contrast-5");

        // Buttonit
        addButton = new Button("Add measurement", e -> handleAddOrUpdate());
        addButton.getStyle().set("background-color", "grey");
        addButton.getStyle().set("color", "white");
        addButton.addClassNames("rounded-m", "font-bold", "text-s");

        deleteButton = new Button("Delete selected", e -> deleteMeasurement());
        deleteButton.addClassNames("text-s", "bg-error", "text-contrast");
        deleteButton.getStyle().set("background-color", "lightgray");
        deleteButton.getStyle().set("color", "black");

        editButton = new Button("Edit selected", e -> prepareEditMeasurement());
        editButton.addClassNames("text-s", "bg-tertiary", "text-contrast");
        editButton.getStyle().set("color", "black");

        // Henkilön lisäyskentät
        newFirstNameField = new TextField("First Name");
        newFirstNameField.addClassName("text-s");

        newLastNameField = new TextField("Last Name");
        newLastNameField.addClassName("text-s");

        newEmailField = new TextField("Email");
        newEmailField.addClassName("text-s");

        addPersonButton = new Button("Add Person", e -> addPerson());
        addPersonButton.addClassNames("bg-primary", "text-contrast", "rounded-m");

        // Asettelu
        add(
                personSelector,
                typeField, measurementValueField, dateField,
                addButton, deleteButton, editButton,
                measurementGrid,
                newFirstNameField, newLastNameField, newEmailField, addPersonButton
        );
    }
    private void updateGrid() {
        Person selectedPerson = personSelector.getValue();
        if (selectedPerson != null) {
            List<Measurement> measurements = measurementService.getMeasurementsByPerson(selectedPerson.getId());
            measurementGrid.setItems(measurements);
        } else {
            measurementGrid.setItems(List.of());
        }
    }

    private void prepareEditMeasurement() {
        selectedMeasurement = measurementGrid.asSingleSelect().getValue();
        if (selectedMeasurement != null) {
            typeField.setValue(selectedMeasurement.getType());
            measurementValueField.setValue(selectedMeasurement.getMeasurementValue());
            dateField.setValue(selectedMeasurement.getDate());
            addButton.setText("Update measurement");
        } else {
            Notification.show("Select a measurement to edit.");
        }
    }

    private void handleAddOrUpdate() {
        if (typeField.isEmpty() || measurementValueField.isEmpty() || dateField.isEmpty()) {
            Notification.show("Please fill in all fields.");
            return;
        }

        if (selectedMeasurement == null) {
            addMeasurement();
        } else {
            updateMeasurement();
        }
    }

    private void addMeasurement() {
        Measurement newMeasurement = new Measurement();
        newMeasurement.setType(typeField.getValue());
        newMeasurement.setMeasurementValue(measurementValueField.getValue());
        newMeasurement.setDate(dateField.getValue());

        Long personId = getSelectedPersonId();
        if (personId == null) {
            Notification.show("Please select a person first.");
            return;
        }

        measurementService.saveMeasurement(newMeasurement, personId);
        clearFields();
        updateGrid();
    }

    private void updateMeasurement() {
        selectedMeasurement.setType(typeField.getValue());
        selectedMeasurement.setMeasurementValue(measurementValueField.getValue());
        selectedMeasurement.setDate(dateField.getValue());

        measurementService.updateMeasurement(
                selectedMeasurement.getId(),
                selectedMeasurement,
                selectedMeasurement.getPerson().getId()
        );

        clearFields();
        addButton.setText("Add measurement");
        selectedMeasurement = null;
        updateGrid();
    }

    private void deleteMeasurement() {
        Measurement selected = measurementGrid.asSingleSelect().getValue();
        if (selected != null) {

            // Vahvistus
            ConfirmDialog confirmDialog = new ConfirmDialog("Are you sure?", "Do you really want to delete this measurement?", "Delete", e -> {
                measurementService.deleteMeasurement(selected.getId());
                updateGrid();
            });
            confirmDialog.open();
        } else {
            Notification.show("Select a measurement to delete.");
        }
    }

    private void clearFields() {
        typeField.clear();
        measurementValueField.clear();
        dateField.clear();
        selectedMeasurement = null;
        addButton.setText("Add measurement");
    }

    private void clearPersonFields() {
        newFirstNameField.clear();
        newLastNameField.clear();
        newEmailField.clear();
    }

    private Long getSelectedPersonId() {
        Person selected = personSelector.getValue();
        return selected != null ? selected.getId() : null;
    }


    private void addPerson() {
        String firstName = newFirstNameField.getValue();
        String lastName = newLastNameField.getValue();
        String email = newEmailField.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            Notification.show("Please fill in all fields to add a person.");
            return;
        }

        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmail(email);

        personService.savePerson(person);
        clearPersonFields();

        personSelector.setItems(personService.getAllPersons(
                "",
                "",
                "",
                null,
                null,
                "",
                ""
        ));
        updateGrid();

    }
}
