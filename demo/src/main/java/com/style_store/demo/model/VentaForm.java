package com.style_store.demo.model;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Formulario de nueva venta, validado con Spring Validator (Bean Validation). */
public class VentaForm {

    @NotNull(message = "Seleccione un vendedor válido")
    private Long usuarioId;

    /** productoId -> cantidad (campos del formulario: cantidades[ID]) */
    private Map<Long, @Min(value = 0, message = "La cantidad no puede ser negativa")
                      @Max(value = 1000, message = "La cantidad no puede superar 1000 unidades") Integer> cantidades =
            new LinkedHashMap<>();

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Map<Long, Integer> getCantidades() { return cantidades; }
    public void setCantidades(Map<Long, Integer> cantidades) { this.cantidades = cantidades; }
}
