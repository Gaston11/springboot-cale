package com.cale.demo.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(
        name = "Admin",
        description = "Panel de administrador"
)
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @GetMapping
    public String admin() {
        return "Panel admin";
    }
}
