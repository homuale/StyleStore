package com.style_store.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.style_store.demo.Repository.CategoriaRepository;
import com.style_store.demo.Repository.ProductoRepository;
import com.style_store.demo.Repository.UsuarioRepository;
import com.style_store.demo.Repository.VentaRepository;
import com.style_store.demo.model.Categoria;
import com.style_store.demo.model.DetalleVenta;
import com.style_store.demo.model.Producto;
import com.style_store.demo.model.Usuario;
import com.style_store.demo.model.Venta;

@Service
public class TiendaService {

    private final CategoriaRepository categorias;
    private final ProductoRepository productos;
    private final UsuarioRepository usuarios;
    private final VentaRepository ventas;

    public TiendaService(CategoriaRepository categorias, ProductoRepository productos,
                         UsuarioRepository usuarios, VentaRepository ventas) {
        this.categorias = categorias;
        this.productos = productos;
        this.usuarios = usuarios;
        this.ventas = ventas;
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categorias.findAll();
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarCategoriasOrdenadas() {
        return categorias.findAll(Sort.by("id"));
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarCategoriasConProductos() {
        return categorias.findAll().stream()
                .filter(categoria -> productos.existsByCategoriaId(categoria.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> primerProductoPorCategoria(Long categoriaId) {
        return productos.findFirstByCategoriaIdOrderByIdAsc(categoriaId);
    }

    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        return ventas.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public Optional<Venta> buscarVenta(Long id) {
        return ventas.findById(id);
    }

    @Transactional(readOnly = true)
    public BigDecimal totalVentas() {
        return listarVentas().stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarProductos() {
        return productos.findAll();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarInventario() {
        return productos.findAll(Sort.by("id"));
    }

    @Transactional(readOnly = true)
    public List<Producto> destacados() {
        return productos.findTop3ByStockGreaterThanOrderByIdDesc(0);
    }

    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Long id) {
        return productos.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarProductos(String q, Long categoriaId) {
        return productos.buscar(q, categoriaId);
    }

    @Transactional(readOnly = true)
    public List<Producto> productosRelacionados(Producto producto) {
        return productos.findTop4ByCategoriaIdAndIdNotAndStockGreaterThan(
                producto.getCategoria().getId(), producto.getId(), 0);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarios.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosOrdenados() {
        return usuarios.findAll(Sort.by("id"));
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuario(Long id) {
        return usuarios.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> buscarCategoria(Long id) {
        return categorias.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeCategoria(Long id) {
        return categorias.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeProducto(Long id) {
        return productos.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeUsuario(Long id) {
        return usuarios.existsById(id);
    }

    @Transactional
    public Categoria guardarCategoria(Categoria categoria) {
        return categorias.save(categoria);
    }

    @Transactional
    public void eliminarCategoria(Long id) {
        categorias.deleteById(id);
    }

    @Transactional
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarios.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        usuarios.deleteById(id);
    }

    @Transactional
    public Producto guardarProducto(Producto producto, Long categoriaId) {
        Categoria categoria = categorias.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Seleccione una categoría válida"));
        producto.setCategoria(categoria);
        return productos.save(producto);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        productos.deleteById(id);
    }

    @Transactional
    public Venta registrarVenta(Long usuarioId, Map<String, String> params) {
        Usuario usuario = usuarios.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Seleccione un vendedor válido"));

        Map<Producto, Integer> pedido = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!entry.getKey().startsWith("cant_")) {
                continue;
            }

            int cantidad;
            try {
                cantidad = Integer.parseInt(entry.getValue().trim());
            } catch (NumberFormatException ex) {
                continue;
            }
            if (cantidad <= 0) {
                continue;
            }

            Long productoId;
            try {
                productoId = Long.valueOf(entry.getKey().substring(5));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Un producto de la venta ya no existe");
            }

            Producto producto = productos.findById(productoId)
                    .orElseThrow(() -> new IllegalArgumentException("Un producto de la venta ya no existe"));
            if (producto.getStock() < cantidad) {
                throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombre()
                        + " (disponible: " + producto.getStock() + ")");
            }
            pedido.put(producto, cantidad);
        }

        if (pedido.isEmpty()) {
            throw new IllegalArgumentException("Indique al menos un producto");
        }

        Venta venta = new Venta(LocalDate.now(), usuario);
        List<Producto> modificados = new ArrayList<>();
        for (Map.Entry<Producto, Integer> entry : pedido.entrySet()) {
            Producto producto = entry.getKey();
            producto.setStock(producto.getStock() - entry.getValue());
            modificados.add(producto);
            venta.agregarDetalle(new DetalleVenta(producto, entry.getValue()));
        }
        productos.saveAll(modificados);
        return ventas.save(venta);
    }

    @Transactional
    public void eliminarVenta(Long id) {
        Venta venta = ventas.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La venta no existe"));
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productos.save(producto);
        }
        ventas.delete(venta);
    }
}
