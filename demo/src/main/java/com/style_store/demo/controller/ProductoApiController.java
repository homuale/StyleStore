package com.style_store.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.style_store.demo.model.Producto;
import com.style_store.demo.service.TiendaService;

@RestController
@RequestMapping("/api/productos")
public class ProductoApiController {

    private final TiendaService tienda;

    public ProductoApiController(TiendaService tienda) {
        this.tienda = tienda;
    }

    @GetMapping
    public List<Producto> listar() {
        return tienda.listarProductos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable("id") Long id) {
        return tienda.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}