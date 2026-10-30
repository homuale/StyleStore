package com.style_store.demo.controller;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.style_store.demo.model.Categoria;
import com.style_store.demo.service.TiendaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categorias")
public class CategoriaController {

    private final TiendaService tienda;

    public CategoriaController(TiendaService tienda) {
        this.tienda = tienda;
    }

    @GetMapping
    public String listar(Model model) {
        return vista(model, new Categoria());
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("categoria") Categoria categoria, BindingResult result,
                          Model model, RedirectAttributes flash) {
        categoria.setId(null);
        if (!result.hasErrors()) {
            try {
                tienda.guardarCategoria(categoria);
                flash.addFlashAttribute("mensaje", "Categoría registrada correctamente");
                return "redirect:/admin/categorias";
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("nombre", "duplicado", "Ya existe una categoría con ese nombre");
            }
        }
        return vista(model, categoria);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        Optional<Categoria> categoria = tienda.buscarCategoria(id);
        if (categoria.isEmpty()) {
            flash.addFlashAttribute("error", "La categoría no existe");
            return "redirect:/admin/categorias";
        }
        return vista(model, categoria.get());
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("categoria") Categoria categoria, BindingResult result,
                             Model model, RedirectAttributes flash) {
        if (!tienda.existeCategoria(id)) {
            flash.addFlashAttribute("error", "La categoría no existe");
            return "redirect:/admin/categorias";
        }
        categoria.setId(id);
        if (!result.hasErrors()) {
            try {
                tienda.guardarCategoria(categoria);
                flash.addFlashAttribute("mensaje", "Categoría actualizada correctamente");
                return "redirect:/admin/categorias";
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("nombre", "duplicado", "Ya existe una categoría con ese nombre");
            }
        }
        return vista(model, categoria);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes flash) {
        if (!tienda.existeCategoria(id)) {
            flash.addFlashAttribute("error", "La categoría no existe");
            return "redirect:/admin/categorias";
        }
        try {
            tienda.eliminarCategoria(id);
            flash.addFlashAttribute("mensaje", "Categoría eliminada correctamente");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("error", "No se puede eliminar: la categoría tiene productos asociados");
        }
        return "redirect:/admin/categorias";
    }

    private String vista(Model model, Categoria categoria) {
        model.addAttribute("categoria", categoria);
        model.addAttribute("categorias", tienda.listarCategoriasOrdenadas());
        return "admin-categorias";
    }
}
