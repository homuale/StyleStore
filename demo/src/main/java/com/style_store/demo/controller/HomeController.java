package com.style_store.demo.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.style_store.demo.Repository.CategoriaRepository;
import com.style_store.demo.Repository.ProductoRepository;
import com.style_store.demo.model.Categoria;

@Controller
public class HomeController {

    private final ProductoRepository productos;
    private final CategoriaRepository categorias;

    public HomeController(ProductoRepository productos, CategoriaRepository categorias) {
        this.productos = productos;
        this.categorias = categorias;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("destacados", productos.findTop3ByStockGreaterThanOrderByIdDesc(0));
        
        //Tarjetas de categorias
        Map<Long, String> imagenCategoria = new LinkedHashMap<>();
        for (Categoria categoria : categorias.findAll()) {
            productos.findFirstByCategoriaIdOrderByIdAsc(categoria.getId())
                    .ifPresent(producto -> imagenCategoria.put(categoria.getId(), producto.getImagen()));
        }
        // Solo se muestran las categorias que tienen productos
        model.addAttribute("categorias",
                categorias.findAll().stream().filter(c -> imagenCategoria.containsKey(c.getId())).toList());
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