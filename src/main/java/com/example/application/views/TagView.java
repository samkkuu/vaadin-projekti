package com.example.application.views;

import com.example.application.data.Person;
import com.example.application.data.Tag;
import com.example.application.services.PersonService;
import com.example.application.services.TagService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import java.util.List;

@Route("tag")
@CssImport("themes/my-app/custom-styles.css")
public class TagView extends VerticalLayout {

    private final TagService tagService;
    private final PersonService personService;
    private final TextField nameField = new TextField("Tag Name");
    private final TextField searchField = new TextField("Search by name");
    private final Button saveButton = new Button("Save");
    private final Button deleteButton = new Button("Delete");
    private final Button addTagToPersonButton = new Button("Add Tag to Person");
    private final Grid<Tag> tagGrid = new Grid<>(Tag.class);
    private Tag selectedTag = null;
    private ComboBox<Person> personComboBox = new ComboBox<>("Select Person");

    public TagView(TagService tagService, PersonService personService) {
        this.tagService = tagService;
        this.personService = personService;

        // Haku
        searchField.addValueChangeListener(e -> updateGrid());
        searchField.setPlaceholder("Search...");
        searchField.setClearButtonVisible(true);
        add(searchField);

        // Lomake ja napit
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, deleteButton, addTagToPersonButton);
        FormLayout formLayout = new FormLayout(nameField, buttonLayout);
        saveButton.addClickListener(e -> saveTag());
        deleteButton.addClickListener(e -> deleteTag());
        addTagToPersonButton.addClickListener(e -> addTagToPerson());

        add(formLayout);

        // Grid
        tagGrid.setColumns("id", "name");
        tagGrid.asSingleSelect().addValueChangeListener(e -> editTag(e.getValue()));
        add(tagGrid);

        // PersonComboBox
        personComboBox.setItems(personService.getAllPersons("", "", "", null, null, "", ""));
        personComboBox.setItemLabelGenerator(Person::getFullName);
        add(personComboBox);

        updateGrid();
    }

    private void saveTag() {
        if (nameField.getValue().isEmpty()) {
            nameField.setInvalid(true);
            nameField.setErrorMessage("Tag name cannot be empty");
            return;
        } else {
            nameField.setInvalid(false);
        }

        if (selectedTag == null) {
            selectedTag = new Tag();
        }

        selectedTag.setName(nameField.getValue());
        tagService.saveTag(selectedTag);

        clearForm();
        updateGrid();
    }

    private void deleteTag() {
        if (selectedTag != null && selectedTag.getId() != null) {
            tagService.deleteTag(selectedTag.getId());
            clearForm();
            updateGrid();
        }
    }

    private void addTagToPerson() {
        Person selectedPerson = personComboBox.getValue();
        if (selectedPerson != null && selectedTag != null) {
            tagService.addTagToPerson(selectedPerson.getId(), selectedTag.getId());
            Notification.show("Tag added to person!");
        } else {
            Notification.show("Please select both a person and a tag.");
        }
    }

    private void editTag(Tag tag) {
        if (tag != null) {
            selectedTag = tag;
            nameField.setValue(tag.getName() != null ? tag.getName() : "");
        }
    }

    public void updateGrid() {
        List<Tag> tags = tagService.filterTagsByName(searchField.getValue());
        tagGrid.setItems(tags);
    }

    private void clearForm() {
        selectedTag = null;
        nameField.clear();
    }
}
