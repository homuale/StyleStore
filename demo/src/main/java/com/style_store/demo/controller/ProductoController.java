package com.style_store.demo.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.style_store.demo.model.Categoria;
import com.style_store.demo.model.Producto;
import com.style_store.demo.service.TiendaService;

import jakarta.validation.Valid;

@Controller
public class ProductoController {

    private final TiendaService tienda;

    public ProductoController(TiendaService tienda) {
        this.tienda = tienda;
    }

    // Vista del vendedor

    @GetMapping("/productos")
    public String catalogo(@RequestParam(name = "q", required = false) String q,
                           @RequestParam(name = "categoria", required = false) Long categoria,
                           @RequestParam(name = "orden", required = false) String orden,
                           Model model) {
        List<Producto> resultado = tienda.buscarProductos(q, categoria);
        if ("precio_asc".equals(orden)) {
            resultado = resultado.stream().sorted(Comparator.comparing(Producto::getPrecio)).toList();
        } else if ("precio_desc".equals(orden)) {
            resultado = resultado.stream().sorted(Comparator.comparing(Producto::getPrecio).reversed()).toList();
        } else {
            resultado = resultado.stream().sorted(Comparator.comparing(Producto::getId).reversed()).toList();
        }
        model.addAttribute("productos", resultado);
        model.addAttribute("orden", orden);
        model.addAttribute("categorias",
                tienda.listarCategoriasConProductos());
        model.addAttribute("q", q);
        model.addAttribute("categoriaSel", categoria);
        return "productos";
    }

    @GetMapping("/productos/{id}")
    public String detalle(@PathVariable("id") Long id, Model model) {
        Optional<Producto> producto = tienda.buscarPorId(id);
        if (producto.isEmpty()) {
            return "redirect:/productos";
        }
        Producto p = producto.get();
        model.addAttribute("producto", p);
        model.addAttribute("relacionados",
                tienda.productosRelacionados(p));
        return "producto-detalle";
    }

    @GetMapping("/admin/productos")
    public String inventario(Model model) {
        model.addAttribute("productos", tienda.listarInventario());
        return "admin-productos";
    }

    @GetMapping("/admin/productos/nuevo")
    public String nuevo(Model model) {
        Producto producto = new Producto();
        producto.setCategoria(new Categoria());
        return formulario(model, producto);
    }

    @PostMapping("/admin/productos")
    public String guardar(@Valid @ModelAttribute("producto") Producto producto, BindingResult result,
                          Model model, RedirectAttributes flash) {
        producto.setId(null);
        validarCategoria(producto, result);
        if (result.hasErrors()) {
            return formulario(model, producto);
        }
        tienda.guardarProducto(producto, producto.getCategoria().getId());
        flash.addFlashAttribute("mensaje", "Producto registrado correctamente");
        return "redirect:/admin/productos";
    }

    @GetMapping("/admin/productos/{id}/editar")
    public String editar(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        Optional<Producto> producto = tienda.buscarPorId(id);
        if (producto.isEmpty()) {
            flash.addFlashAttribute("error", "El producto no existe");
            return "redirect:/admin/productos";
        }
        return formulario(model, producto.get());
    }

    @PostMapping("/admin/productos/{id}/editar")
    public String actualizar(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("producto") Producto producto, BindingResult result,
                             Model model, RedirectAttributes flash) {
        if (!tienda.existeProducto(id)) {
            flash.addFlashAttribute("error", "El producto no existe");
            return "redirect:/admin/productos";
        }
        producto.setId(id);
        validarCategoria(producto, result);
        if (result.hasErrors()) {
            return formulario(model, producto);
        }
        tienda.guardarProducto(producto, producto.getCategoria().getId());
        flash.addFlashAttribute("mensaje", "Producto actualizado correctamente");
        return "redirect:/admin/productos";
    }

    @PostMapping("/admin/productos/{id}/eliminar")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes flash) {
        if (!tienda.existeProducto(id)) {
            flash.addFlashAttribute("error", "El producto no existe");
            return "redirect:/admin/productos";
        }
        try {
            tienda.eliminarProducto(id);
            flash.addFlashAttribute("mensaje", "Producto eliminado correctamente");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("error", "No se puede eliminar: el producto tiene ventas registradas");
        }
        return "redirect:/admin/productos";
    }

    private String formulario(Model model, Producto producto) {
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", tienda.listarCategorias());
        return "producto-form";
    }

    private void validarCategoria(Producto producto, BindingResult result) {
        Categoria categoria = producto.getCategoria();
        if (categoria != null && (categoria.getId() == null || !tienda.existeCategoria(categoria.getId()))) {
            result.rejectValue("categoria", "categoria.invalida", "Seleccione una categoría válida");
        }
    }
}
