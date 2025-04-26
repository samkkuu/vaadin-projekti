package com.example.application.views;

import com.example.application.data.Person;
import com.example.application.data.Tag;
import com.example.application.services.PersonService;
import com.example.application.services.TagService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Route("persons")
@CssImport("themes/my-app/custom-styles.css")
public class PersonView extends VerticalLayout {

    private final PersonService personService;
    private final TagService tagService;
    private final MessageSource messageSource;
    private ListDataProvider<Person> dataProvider;

    // Komponentit
    private TextField firstNameFilter;
    private TextField lastNameFilter;
    private TextField emailFilter;
    private ComboBox<Tag> tagFilter;
    private DatePicker birthDateFilter;
    private TextField addressFilter;
    private TextField phoneNumberFilter;
    private Grid<Person> grid;
    private TextField newFirstNameField;
    private TextField newLastNameField;
    private TextField newEmailField;
    private Button addPersonButton;
    private Button editPersonButton;

    public PersonView(PersonService personService, TagService tagService, MessageSource messageSource) {
        this.personService = personService;
        this.tagService = tagService;
        this.messageSource = messageSource;
        buildLayout();
    }

    private void buildLayout() {
        removeAll(); // Tyhjennä vanha näkymä

        // Luo komponentit ja aseta käännökset
        firstNameFilter = new TextField(tr("filter.firstName"));
        lastNameFilter = new TextField(tr("filter.lastName"));
        emailFilter = new TextField(tr("filter.email"));
        tagFilter = new ComboBox<>(tr("filter.tag"));
        birthDateFilter = new DatePicker(tr("filter.birthDate"));
        addressFilter = new TextField(tr("filter.address"));
        phoneNumberFilter = new TextField(tr("filter.phone"));
        tagFilter.setItems(tagService.getAllTags());
        tagFilter.setItemLabelGenerator(Tag::getName);

        grid = new Grid<>(Person.class, false);
        grid.addColumn(Person::getFirstName).setHeader(tr("field.firstName"));
        grid.addColumn(Person::getLastName).setHeader(tr("field.lastName"));
        grid.addColumn(Person::getEmail).setHeader(tr("field.email"));
        grid.addColumn(person -> person.getTags().stream()
                        .map(Tag::getName).collect(Collectors.joining(", ")))
                .setHeader(tr("column.tags"));

        List<Person> personList = personService.getAllPersons("", "", "", null, null, "", "");
        dataProvider = new ListDataProvider<>(personList);
        grid.setDataProvider(dataProvider);

        newFirstNameField = new TextField(tr("field.firstName"));
        newLastNameField = new TextField(tr("field.lastName"));
        newEmailField = new TextField(tr("field.email"));
        addPersonButton = new Button(tr("button.add"), e -> addPerson());
        Button deleteButton = new Button(tr("button.delete"), e -> deletePerson());
        editPersonButton = new Button(tr("button.edit"), e -> editPerson());
        editPersonButton.setEnabled(false);

        firstNameFilter.addValueChangeListener(e -> applyFilter());
        lastNameFilter.addValueChangeListener(e -> applyFilter());
        emailFilter.addValueChangeListener(e -> applyFilter());
        tagFilter.addValueChangeListener(e -> applyFilter());
        birthDateFilter.addValueChangeListener(e -> applyFilter());
        addressFilter.addValueChangeListener(e -> applyFilter());
        phoneNumberFilter.addValueChangeListener(e -> applyFilter());

        grid.asSingleSelect().addValueChangeListener(e -> {
            editPersonButton.setEnabled(e.getValue() != null);
            if (e.getValue() != null) setPersonFields(e.getValue());
            else clearForm();
        });

        Button langFi = new Button("Suomeksi", e -> setLocale("fi"));
        Button langEn = new Button("In English", e -> setLocale("en"));

        add(new FormLayout(firstNameFilter, lastNameFilter, emailFilter, tagFilter, birthDateFilter, addressFilter, phoneNumberFilter),
                grid, newFirstNameField, newLastNameField, newEmailField, addPersonButton, deleteButton, editPersonButton,
                langFi, langEn
        );
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        // Springin sessiolocale
        VaadinSession.getCurrent().getSession().setAttribute(
                SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale
        );
        // Vaadinin locale
        VaadinSession.getCurrent().setLocale(locale);

        // **HUOM: reload lataa uuden näkymän uudella localella**
        getUI().ifPresent(ui -> ui.getPage().reload());
    }

    private String tr(String key) {
        Locale locale = (Locale) VaadinSession.getCurrent().getSession()
                .getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
        if (locale == null) {
            locale = VaadinSession.getCurrent().getLocale();
        }
        return messageSource.getMessage(key, null, locale);
    }

    // --- CRUD-metodit, ei muutoksia ---
    private void applyFilter() {
        String firstName = firstNameFilter.getValue().isEmpty() ? "" : firstNameFilter.getValue().toLowerCase();
        String lastName = lastNameFilter.getValue().isEmpty() ? "" : lastNameFilter.getValue().toLowerCase();
        String email = emailFilter.getValue().isEmpty() ? "" : emailFilter.getValue().toLowerCase();
        Tag selectedTag = tagFilter.getValue();
        LocalDate birthDate = birthDateFilter.getValue();
        String address = addressFilter.getValue().isEmpty() ? "" : addressFilter.getValue().toLowerCase();
        String phoneNumber = phoneNumberFilter.getValue().isEmpty() ? "" : phoneNumberFilter.getValue().toLowerCase();

        List<Person> filteredPersons = personService.getAllPersons(
                firstName, lastName, email, selectedTag, birthDate, address, phoneNumber
        );
        dataProvider.getItems().clear();
        dataProvider.getItems().addAll(filteredPersons);
        grid.getDataProvider().refreshAll();
    }

    private void deletePerson() {
        Person selectedPerson = grid.asSingleSelect().getValue();
        if (selectedPerson != null) {
            personService.deletePerson(selectedPerson.getId());
            Notification.show(tr("notification.personDeleted"));
            applyFilter();
        } else {
            Notification.show(tr("notification.noPersonSelected"));
        }
    }

    private void editPerson() {
        Person selectedPerson = grid.asSingleSelect().getValue();
        if (selectedPerson != null) {
            selectedPerson.setFirstName(newFirstNameField.getValue());
            selectedPerson.setLastName(newLastNameField.getValue());
            selectedPerson.setEmail(newEmailField.getValue());
            personService.savePerson(selectedPerson);
            Notification.show(tr("notification.personEdited"));
            applyFilter();
        } else {
            Notification.show(tr("notification.noPersonSelected"));
        }
    }

    private void clearForm() {
        newFirstNameField.clear();
        newLastNameField.clear();
        newEmailField.clear();
    }

    private void setPersonFields(Person person) {
        newFirstNameField.setValue(person.getFirstName());
        newLastNameField.setValue(person.getLastName());
        newEmailField.setValue(person.getEmail());
    }

    private void addPerson() {
        Person person = personService.addPerson(newFirstNameField.getValue(), newLastNameField.getValue(), newEmailField.getValue());
        Notification.show(tr("notification.personAdded"));
        applyFilter();
    }
}
