package com.style_store.demo.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.style_store.demo.model.Venta;
import com.style_store.demo.service.TiendaService;

@Controller
public class VentaController {

    private final TiendaService tienda;

    public VentaController(TiendaService tienda) {
        this.tienda = tienda;
    }

    @GetMapping("/ventas")
    public String ventas(Model model) {
        List<Venta> lista = tienda.listarVentas();
        BigDecimal total = tienda.totalVentas();
        model.addAttribute("ventas", lista);
        model.addAttribute("totalGeneral", total);
        return "ventas";
    }

    @GetMapping("/ventas/{id}")
    public String detalle(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        Optional<Venta> venta = tienda.buscarVenta(id);
        if (venta.isEmpty()) {
            flash.addFlashAttribute("error", "La venta no existe");
            return "redirect:/ventas";
        }
        model.addAttribute("venta", venta.get());
        return "venta-detalle";
    }

    @GetMapping("/ventas/nueva")
    public String nueva(@RequestParam(name = "productoId", required = false) Long productoId, Model model) {
        model.addAttribute("productos", tienda.listarProductos());
        model.addAttribute("usuarios", tienda.listarUsuarios());
        model.addAttribute("productoSel", productoId);
        return "venta-form";
    }

    @PostMapping("/ventas")
    public String registrar(@RequestParam("usuarioId") Long usuarioId,
                            @RequestParam Map<String, String> params,
                            RedirectAttributes flash) {
        try {
            tienda.registrarVenta(usuarioId, params);
        } catch (IllegalArgumentException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/ventas/nueva";
        }
        flash.addFlashAttribute("mensaje", "Venta registrada correctamente");
        return "redirect:/ventas";
    }

    // elimina la venta y devuelve el stock
    @PostMapping("/ventas/{id}/eliminar")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes flash) {
        try {
            tienda.eliminarVenta(id);
        } catch (IllegalArgumentException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
            return "redirect:/ventas";
        }
        flash.addFlashAttribute("mensaje", "Venta eliminada y stock recuperado correctamente");
        return "redirect:/ventas";
    }
}
