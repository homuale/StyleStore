package com.style_store.demo.model;

import java.math.BigDecimal;

public class Producto {
    private final Long id;
    private final String nombre;
    private final BigDecimal precio;
    private final int stock;
    private final String talla;
    private final Categoria categoria;
    private final String imagen;

    public Producto(Long id, String nombre, BigDecimal precio, int stock,
                    String talla, Categoria categoria, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.talla = talla;
        this.categoria = categoria;
        this.imagen = imagen;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public int getStock() { return stock; }
    public String getTalla() { return talla; }
    public Categoria getCategoria() { return categoria; }
    public String getImagen() { return imagen; }
}
