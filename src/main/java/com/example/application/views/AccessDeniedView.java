package com.example.application.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;


@Route("access-denied")
public class AccessDeniedView extends VerticalLayout {

    public AccessDeniedView() {
        add("You do not have permission to access this page.");
    }
}

