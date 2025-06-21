// src/main/java/com/acabra/expenseshare/controller/LoginController.java
package com.acabra.expenseshare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model; // Import Model
import org.springframework.security.web.csrf.CsrfToken; // Import CsrfToken

/**
 * Controller for handling login requests and serving the login page.
 */
@Controller // Marks this class as a Spring MVC controller
public class LoginController {

    /**
     * Handles GET requests to the /login URL.
     * This method adds the CSRF token to the model and returns the logical view name "login",
     * which Spring Boot will resolve to src/main/resources/templates/login.mustache.
     *
     * @param model The Model object to add attributes to.
     * @param csrfToken The CsrfToken automatically injected by Spring Security.
     * @return The view name for the login page.
     */
    @GetMapping("/login")
    public String login(Model model, CsrfToken csrfToken) {
        // Add CSRF token details to the model so Mustache can access them
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        return "login"; // This corresponds to src/main/resources/templates/login.mustache
    }

    /**
     * Handles GET requests to the root URL ("/").
     * This method also adds the CSRF token to the model, as the index page
     * contains a logout form that requires it.
     *
     * @param model The Model object to add attributes to.
     * @param csrfToken The CsrfToken automatically injected by Spring Security.
     * @return The view name for the index page.
     */
    @GetMapping("/")
    public String index(Model model, CsrfToken csrfToken) {
        // Add CSRF token details to the model for the logout form on index.mustache
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        return "index"; // This will be your main application page
    }
}
