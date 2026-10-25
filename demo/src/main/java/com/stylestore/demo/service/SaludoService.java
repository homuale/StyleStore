package com.stylestore.demo.service;

import org.springframework.stereotype.Service;

import com.stylestore.demo.model.Saludo;

@Service
public class SaludoService {

    public Saludo obtenerSaludo() {
        return new Saludo("Hola, bienvenido a StyleStore!");
    }
}
