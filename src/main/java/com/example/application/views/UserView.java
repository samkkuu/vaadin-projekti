package com.example.application.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("user")
@RolesAllowed("USER")
public class UserView extends VerticalLayout {

    public UserView() {
        add("Welcome to the User page!");
    }
}
