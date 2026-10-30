package com.style_store.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.style_store.demo.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Consultas derivadas: Spring Data genera el SQL a partir del nombre del método.
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoriaId(Long categoriaId);

    List<Producto> findByNombreContainingIgnoreCaseAndCategoriaId(String nombre, Long categoriaId);

  
    List<Producto> findTop3ByStockGreaterThanOrderByIdDesc(Integer stock);

    Optional<Producto> findFirstByNombre(String nombre);

    boolean existsByCategoriaId(Long categoriaId);

    Optional<Producto> findFirstByCategoriaIdOrderByIdAsc(Long categoriaId);

    List<Producto> findTop4ByCategoriaIdAndIdNotAndStockGreaterThan(Long categoriaId, Long id, Integer stock);

    default List<Producto> buscar(String q, Long categoriaId) {
        boolean hayTexto = q != null && !q.isBlank();
        if (hayTexto && categoriaId != null) {
            return findByNombreContainingIgnoreCaseAndCategoriaId(q.trim(), categoriaId);
        }
        if (hayTexto) {
            return findByNombreContainingIgnoreCase(q.trim());
        }
        if (categoriaId != null) {
            return findByCategoriaId(categoriaId);
        }
        return findAll();
    }
}