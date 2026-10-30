package com.style_store.demo.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.style_store.demo.model.Categoria;
import com.style_store.demo.service.TiendaService;

@Controller
public class HomeController {

    private final TiendaService tienda;

    public HomeController(TiendaService tienda) {
        this.tienda = tienda;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("destacados", tienda.destacados());
        
        //Tarjetas de categorias
        Map<Long, String> imagenCategoria = new LinkedHashMap<>();
        var categorias = tienda.listarCategoriasConProductos();
        for (Categoria categoria : categorias) {
            tienda.primerProductoPorCategoria(categoria.getId())
                    .ifPresent(producto -> imagenCategoria.put(categoria.getId(), producto.getImagen()));
        }
        model.addAttribute("categorias", categorias);
        model.addAttribute("imagenCategoria", imagenCategoria);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String ingresar(@RequestParam(name = "rol", defaultValue = "vendedor") String rol) {
        return "admin".equals(rol) ? "redirect:/admin/productos" : "redirect:/productos";
    }
}