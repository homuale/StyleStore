package com.stylestore.demo.controller;

import com.stylestore.demo.model.Saludo;
import com.stylestore.demo.service.SaludoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaludoController {

    private final SaludoService saludoService;

    public SaludoController(SaludoService saludoService) {
        this.saludoService = saludoService;
    }

    @GetMapping("/saludo")
    public Saludo saludo() {
        return saludoService.obtenerSaludo();
    }
}
