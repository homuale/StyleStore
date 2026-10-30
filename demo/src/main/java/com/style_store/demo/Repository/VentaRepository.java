package com.style_store.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.style_store.demo.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    //obtiene todas las ventas de forma ascendente
    List<Venta> findAllByOrderByIdAsc();
}