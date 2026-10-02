package com.style_store.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.style_store.demo.model.Categoria;
import com.style_store.demo.model.Producto;
import com.style_store.demo.model.Venta;

@Service
public class TiendaService {

    private final List<Categoria> categorias = List.of(
            new Categoria(1L, "Camisas"),
            new Categoria(2L, "Pantalones"),
            new Categoria(3L, "Chaquetas"),
            new Categoria(4L, "Accesorios"));

    private final List<Producto> productos = List.of(
            new Producto(1L, "Camisa Oxford Clásica", new BigDecimal("89.90"), 18, "M", categorias.get(0), "imagen2.jpeg"),
            new Producto(2L, "Pantalón Slim Fit", new BigDecimal("119.90"), 6, "32", categorias.get(1), "imagen3.jpeg"),
            new Producto(3L, "Chaqueta Acolchada", new BigDecimal("159.90"), 0, "L", categorias.get(2), "imagen1.jpeg"),
            new Producto(4L, "Gorro de Lana", new BigDecimal("39.90"), 24, "Única", categorias.get(3), "imagen2.jpeg"));

    private final List<Venta> ventas = List.of(
            new Venta(1L, LocalDate.of(2026, 9, 1), "vendedor1", 2, new BigDecimal("209.80")),
            new Venta(2L, LocalDate.of(2026, 9, 2), "vendedor1", 1, new BigDecimal("89.90")),
            new Venta(3L, LocalDate.of(2026, 9, 3), "vendedor2", 3, new BigDecimal("199.70")));

    public List<Categoria> listarCategorias() { return categorias; }

    public List<Venta> listarVentas() { return ventas; }

    public List<Producto> listarProductos() { return productos; }

    public BigDecimal totalVentas() {
        return ventas.stream().map(Venta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Producto> destacados() {
        return productos.stream().filter(p -> p.getStock() > 0).limit(3).toList();
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productos.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public List<Producto> buscarProductos(String q, Long categoriaId) {
        return productos.stream()
                .filter(p -> q == null || q.isBlank()
                        || p.getNombre().toLowerCase().contains(q.trim().toLowerCase()))
                .filter(p -> categoriaId == null || p.getCategoria().getId().equals(categoriaId))
                .toList();
    }
}
