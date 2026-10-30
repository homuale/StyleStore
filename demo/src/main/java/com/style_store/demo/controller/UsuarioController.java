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

import com.style_store.demo.model.Rol;
import com.style_store.demo.model.Usuario;
import com.style_store.demo.service.TiendaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    private final TiendaService tienda;

    public UsuarioController(TiendaService tienda) {
        this.tienda = tienda;
    }

    @GetMapping
    public String listar(Model model) {
        return vista(model, new Usuario());
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult result,
                          Model model, RedirectAttributes flash) {
        usuario.setId(null);
        if (!result.hasErrors()) {
            try {
                tienda.guardarUsuario(usuario);
                flash.addFlashAttribute("mensaje", "Usuario registrado correctamente");
                return "redirect:/admin/usuarios";
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("correo", "duplicado", "Ya existe un usuario con ese correo");
            }
        }
        return vista(model, usuario);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") Long id, Model model, RedirectAttributes flash) {
        Optional<Usuario> usuario = tienda.buscarUsuario(id);
        if (usuario.isEmpty()) {
            flash.addFlashAttribute("error", "El usuario no existe");
            return "redirect:/admin/usuarios";
        }
        return vista(model, usuario.get());
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("usuario") Usuario usuario, BindingResult result,
                             Model model, RedirectAttributes flash) {
        if (!tienda.existeUsuario(id)) {
            flash.addFlashAttribute("error", "El usuario no existe");
            return "redirect:/admin/usuarios";
        }
        usuario.setId(id);
        if (!result.hasErrors()) {
            try {
                tienda.guardarUsuario(usuario);
                flash.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
                return "redirect:/admin/usuarios";
            } catch (DataIntegrityViolationException e) {
                result.rejectValue("correo", "duplicado", "Ya existe un usuario con ese correo");
            }
        }
        return vista(model, usuario);
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes flash) {
        if (!tienda.existeUsuario(id)) {
            flash.addFlashAttribute("error", "El usuario no existe");
            return "redirect:/admin/usuarios";
        }
        try {
            tienda.eliminarUsuario(id);
            flash.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("error", "No se puede eliminar: el usuario tiene ventas registradas");
        }
        return "redirect:/admin/usuarios";
    }

    private String vista(Model model, Usuario usuario) {
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarios", tienda.listarUsuariosOrdenados());
        model.addAttribute("roles", Rol.values());
        return "admin-usuarios";
    }
}
