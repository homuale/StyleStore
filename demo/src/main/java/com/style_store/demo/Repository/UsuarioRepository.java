package com.style_store.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.style_store.demo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}