package com.style_store.demo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Venta {
    private final Long id;
    private final LocalDate fecha;
    private final String vendedor;
    private final int items;
    private final BigDecimal total;

    public Venta(Long id, LocalDate fecha, String vendedor, int items, BigDecimal total) {
        this.id = id;
        this.fecha = fecha;
        this.vendedor = vendedor;
        this.items = items;
        this.total = total;
    }

    public Long getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public String getVendedor() { return vendedor; }
    public int getItems() { return items; }
    public BigDecimal getTotal() { return total; }
}
