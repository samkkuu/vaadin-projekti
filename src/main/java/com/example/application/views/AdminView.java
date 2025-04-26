package com.example.application.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("admin")
public class AdminView extends VerticalLayout implements BeforeEnterObserver {

    public AdminView() {
        add(new Text("Admin only view"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Tarkistetaan, että käyttäjä on kirjautunut sisään
        if (auth == null || !auth.isAuthenticated()) {
            event.forwardTo("login"); // Ohjataan kirjautumissivulle, jos ei ole kirjautunut
            return;
        }

        // Tarkistetaan rooli
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            event.forwardTo("access-denied"); // Ohjataan pääsy estetty -sivulle
        }
    }
}
