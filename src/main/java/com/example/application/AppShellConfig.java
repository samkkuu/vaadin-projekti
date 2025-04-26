package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.springframework.stereotype.Component;
import com.vaadin.flow.theme.Theme;


@Theme(value = "my-app")
@Push
@Component
public class AppShellConfig implements AppShellConfigurator {
}

