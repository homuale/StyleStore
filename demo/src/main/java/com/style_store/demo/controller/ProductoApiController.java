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

import com.style_store.demo.Repository.CategoriaRepository;
import com.style_store.demo.Repository.ProductoRepository;
import com.style_store.demo.model.Categoria;
import com.style_store.demo.model.Producto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoApiController {

    private final ProductoRepository productos;
    private final CategoriaRepository categorias;

    public ProductoApiController(ProductoRepository productos, CategoriaRepository categorias) {
        this.productos = productos;
        this.categorias = categorias;
    }

    @GetMapping
    public List<Producto> listar() {
        return productos.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable("id") Long id) {
        return productos.findById(id)
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
        producto.setCategoria(categoria.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(productos.save(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable("id") Long id,
                                               @Valid @RequestBody Producto producto) {
        if (!productos.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Optional<Categoria> categoria = categoriaDe(producto);
        if (categoria.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        producto.setId(id);
        producto.setCategoria(categoria.get());
        return ResponseEntity.ok(productos.save(producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        if (!productos.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            productos.deleteById(id);
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
        return categorias.findById(categoria.getId());
    }
}
