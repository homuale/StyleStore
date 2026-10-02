package com.style_store.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.style_store.demo.service.TiendaService;

@Controller
public class VentaController {

    private final TiendaService tienda;

    public VentaController(TiendaService tienda) {
        this.tienda = tienda;
    }

    @GetMapping("/ventas")
    public String ventas(Model model) {
        model.addAttribute("ventas", tienda.listarVentas());
        model.addAttribute("totalGeneral", tienda.totalVentas());
        return "ventas";
    }
}

