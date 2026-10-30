package com.style_store.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.style_store.demo.model.Categoria;
import com.style_store.demo.model.Producto;
import com.style_store.demo.service.TiendaService;

import jakarta.validation.Valid;

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

    @PostMapping
    public ResponseEntity<Producto> registrar(@Valid @RequestBody Producto producto) {
        Optional<Categoria> categoria = categoriaDe(producto);
        if (categoria.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        producto.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tienda.guardarProducto(producto, categoria.get().getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable("id") Long id,
                                               @Valid @RequestBody Producto producto) {
        if (!tienda.existeProducto(id)) {
            return ResponseEntity.notFound().build();
        }
        Optional<Categoria> categoria = categoriaDe(producto);
        if (categoria.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        producto.setId(id);
        return ResponseEntity.ok(tienda.guardarProducto(producto, categoria.get().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        if (!tienda.existeProducto(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            tienda.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    private Optional<Categoria> categoriaDe(Producto producto) {
        Categoria categoria = producto.getCategoria();
        if (categoria == null || categoria.getId() == null) {
            return Optional.empty();
        }
        return tienda.buscarCategoria(categoria.getId());
    }
}
