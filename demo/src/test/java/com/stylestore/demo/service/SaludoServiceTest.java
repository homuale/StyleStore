package com.stylestore.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.stylestore.demo.model.Saludo;
import org.junit.jupiter.api.Test;

class SaludoServiceTest {

    private final SaludoService saludoService = new SaludoService();

    @Test
    void devuelveElSaludoDeBienvenida() {
        Saludo saludo = saludoService.obtenerSaludo();

        assertEquals("Hola, bienvenido a StyleStore!", saludo.mensaje());
    }
}
