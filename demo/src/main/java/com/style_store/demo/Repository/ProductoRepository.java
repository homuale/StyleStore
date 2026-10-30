package com.style_store.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.style_store.demo.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar nombre sin importar si tiene mayúsculas o minúsculas
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    //Busca productos por categoria
    List<Producto> findByCategoriaId(Long categoriaId);

    //Busca productos por nombre y categoria
    List<Producto> findByNombreContainingIgnoreCaseAndCategoriaId(String nombre, Long categoriaId);

    //Busca el top 3 productos ordenados por id descendente
    List<Producto> findTop3ByStockGreaterThanOrderByIdDesc(Integer stock);

    //Obten el primer producto por nombre
    Optional<Producto> findFirstByNombre(String nombre);

    //verifica si existe un producto con la categoria especificada
    boolean existsByCategoriaId(Long categoriaId);

    //devuelve el primer producto de una categoria ordenado por id ascendente
    Optional<Producto> findFirstByCategoriaIdOrderByIdAsc(Long categoriaId);

    //devuelve los 4 productos de la misma categoria que no sean el producto especificado y que tengan stock mayor a 0
    List<Producto> findTop4ByCategoriaIdAndIdNotAndStockGreaterThan(Long categoriaId, Long id, Integer stock);

    default List<Producto> buscar(String q, Long categoriaId) {
        boolean hayTexto = q != null && !q.isBlank();
        if (hayTexto && categoriaId != null) {
            // Si hay texto y categoria, buscar por nombre y categoría
            return findByNombreContainingIgnoreCaseAndCategoriaId(q.trim(), categoriaId);
        }
        if (hayTexto) {
            //busca productos por nombre sin filtrar categoria
            return findByNombreContainingIgnoreCase(q.trim());
        }
        if (categoriaId != null) {
            //devuelve los productos de esa categoria
            return findByCategoriaId(categoriaId);
        }
        //devuelve todos los productos
        return findAll();
    }
}