package com.example.application.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("home")  // Päänäkymä
public class HomeView extends VerticalLayout {

    public HomeView() {
        add("Welcome to the Home Page!");  // Päänäkymä kaikille käyttäjille
    }
}

