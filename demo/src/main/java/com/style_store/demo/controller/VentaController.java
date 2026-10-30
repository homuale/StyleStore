package com.style_store.demo.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.style_store.demo.Repository.ProductoRepository;
import com.style_store.demo.Repository.UsuarioRepository;
import com.style_store.demo.Repository.VentaRepository;
import com.style_store.demo.model.DetalleVenta;
import com.style_store.demo.model.Producto;
import com.style_store.demo.model.Usuario;
import com.style_store.demo.model.Venta;

@Controller
public class VentaController {

    private final VentaRepository ventas;
    private final ProductoRepository productos;
    private final UsuarioRepository usuarios;

    public VentaController(VentaRepository ventas, ProductoRepository productos, UsuarioRepository usuarios) {
        this.ventas = ventas;
        this.productos = productos;
        this.usuarios = usuarios;
    }

    @GetMapping("/ventas")
    public String ventas(Model model) {
        List<Venta> lista = ventas.findAllByOrderByIdAsc();
        BigDecimal total = lista.stream().map(Venta::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("ventas", lista);
        model.addAttribute("totalGeneral", total);
        return "ventas";
    }

    @GetMapping("/ventas/{id}")
    public String detalle(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        Optional<Venta> venta = ventas.findById(id);
        if (venta.isEmpty()) {
            flash.addFlashAttribute("error", "La venta no existe");
            return "redirect:/ventas";
        }
        model.addAttribute("venta", venta.get());
        return "venta-detalle";
    }

    @GetMapping("/ventas/nueva")
    public String nueva(@RequestParam(name = "productoId", required = false) Long productoId, Model model) {
        model.addAttribute("productos", productos.findAll());
        model.addAttribute("usuarios", usuarios.findAll());
        model.addAttribute("productoSel", productoId);
        return "venta-form";
    }

    @PostMapping("/ventas")
    @Transactional
    public String registrar(@RequestParam("usuarioId") Long usuarioId,
                            @RequestParam Map<String, String> params,
                            RedirectAttributes flash) {
        Optional<Usuario> usuario = usuarios.findById(usuarioId);
        if (usuario.isEmpty()) {
            flash.addFlashAttribute("error", "Seleccione un vendedor válido");
            return "redirect:/ventas/nueva";
        }


        Map<Producto, Integer> pedido = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (!e.getKey().startsWith("cant_")) {
                continue;
            }
            int cantidad;
            try {
                cantidad = Integer.parseInt(e.getValue().trim());
            } catch (NumberFormatException ex) {
                continue;
            }
            if (cantidad <= 0) {
                continue;
            }
            Long productoId = Long.valueOf(e.getKey().substring(5));
            Optional<Producto> p = productos.findById(productoId);
            if (p.isEmpty()) {
                flash.addFlashAttribute("error", "Un producto de la venta ya no existe");
                return "redirect:/ventas/nueva";
            }
            if (p.get().getStock() < cantidad) {
                flash.addFlashAttribute("error", "Stock insuficiente para " + p.get().getNombre()
                        + " (disponible: " + p.get().getStock() + ")");
                return "redirect:/ventas/nueva";
            }
            pedido.put(p.get(), cantidad);
        }
        if (pedido.isEmpty()) {
            flash.addFlashAttribute("error", "Indique al menos un producto");
            return "redirect:/ventas/nueva";
        }

        Venta venta = new Venta(LocalDate.now(), usuario.get());
        List<Producto> modificados = new ArrayList<>();
        for (Map.Entry<Producto, Integer> e : pedido.entrySet()) {
            Producto p = e.getKey();
            p.setStock(p.getStock() - e.getValue());
            modificados.add(p);
            venta.agregarDetalle(new DetalleVenta(p, e.getValue()));
        }
        productos.saveAll(modificados);
        ventas.save(venta);
        flash.addFlashAttribute("mensaje", "Venta registrada correctamente");
        return "redirect:/ventas";
    }

    // elimina la venta y devuelve el stock
    @PostMapping("/ventas/{id}/eliminar")
    @Transactional
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes flash) {
        Optional<Venta> venta = ventas.findById(id);
        if (venta.isEmpty()) {
            flash.addFlashAttribute("error", "La venta no existe");
            return "redirect:/ventas";
        }
        for (DetalleVenta d : venta.get().getDetalles()) {
            Producto p = d.getProducto();
            p.setStock(p.getStock() + d.getCantidad());
            productos.save(p);
        }
        ventas.delete(venta.get());
        flash.addFlashAttribute("mensaje", "Venta eliminada y stock recuperado correctamente");
        return "redirect:/ventas";
    }
}
