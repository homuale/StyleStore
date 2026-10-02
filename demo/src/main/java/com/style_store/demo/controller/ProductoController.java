package com.style_store.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.style_store.demo.model.Producto;
import com.style_store.demo.service.TiendaService;

@Controller
public class ProductoController {

    private final TiendaService tienda;

    public ProductoController(TiendaService tienda) {
        this.tienda = tienda;
    }

    @GetMapping("/productos")
    public String catalogo(@RequestParam(name = "q", required = false) String q,
                           @RequestParam(name = "categoria", required = false) Long categoria,
                           Model model) {
        model.addAttribute("productos", tienda.buscarProductos(q, categoria));
        model.addAttribute("categorias", tienda.listarCategorias());
        model.addAttribute("q", q);
        model.addAttribute("categoriaSel", categoria);
        return "productos";
    }

    @GetMapping("/productos/{id}")
    public String detalle(@PathVariable("id") Long id, Model model) {
        Producto producto = tienda.buscarPorId(id).orElse(null);
        if (producto == null) {
            return "redirect:/productos";
        }
        model.addAttribute("producto", producto);
        return "producto-detalle";
    }

    @GetMapping("/admin/productos")
    public String inventario(Model model) {
        model.addAttribute("productos", tienda.listarProductos());
        model.addAttribute("categorias", tienda.listarCategorias());
        return "admin-productos";
    }
}
