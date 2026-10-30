package com.style_store.demo.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.style_store.demo.Repository.CategoriaRepository;
import com.style_store.demo.Repository.ProductoRepository;
import com.style_store.demo.Repository.UsuarioRepository;
import com.style_store.demo.Repository.VentaRepository;
import com.style_store.demo.model.Categoria;
import com.style_store.demo.model.DetalleVenta;
import com.style_store.demo.model.Producto;
import com.style_store.demo.model.Rol;
import com.style_store.demo.model.Usuario;
import com.style_store.demo.model.Venta;

/** Carga datos de ejemplo solo cuando la base de datos está vacia*/
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner cargarDatos(CategoriaRepository categorias, ProductoRepository productos,
                                  UsuarioRepository usuarios, VentaRepository ventas) {
        return args -> {

            // El catalogo de ejemplo se siembra solo si no hay productos
            if (productos.count() == 0) {
                Categoria polosBlusas = categoria(categorias, "Polos y Blusas", "Polos basicos, crop tops y blusas");
                Categoria pantalones = categoria(categorias, "Pantalones", "Pantalones y Jeans, Jeans y pantalones casuales");
                Categoria vestidos = categoria(categorias, "Vestidos", "Vestidos y Faldas, Vestidos casuales y faldas");
                Categoria casacas = categoria(categorias, "Casacas", "Abrigos y Chaquetas, Casacas, chaquetas y cardigans");
                Categoria accesorios = categoria(categorias, "Accesorios", "Gorros, bufandas y más");
                Categoria camisas = categoria(categorias, "Camisas", "Camisas Blanca para hombre");

                //polos y blusas
                producto(productos, "Polo Básico", new BigDecimal("59.90"), 10, "M", "img1.jpeg", polosBlusas);
                producto(productos, "crop top", new BigDecimal("49.90"), 15, "M", "img2.jpeg", polosBlusas);

                //Pantalones y jeans
                producto(productos, "Jean Recto", new BigDecimal("89.90"), 5, "32", "img3.jpeg", pantalones);
                producto(productos, "Pantalón Largo", new BigDecimal("99.90"), 6, "32", "img4.jpeg", pantalones);

                //Vestidos y faldas
                producto(productos, "Vestido Casual", new BigDecimal("129.90"), 8, "M", "img5.jpeg", vestidos);
                producto(productos, "Falda Plisada", new BigDecimal("59.90"), 12, "M", "img6.jpeg", vestidos);

                //Abrigos y chaquetas
                producto(productos, "Casaca denim", new BigDecimal("119.90"), 5, "L", "img7.jpeg", casacas);
                producto(productos, "Cardigan de lana", new BigDecimal("149.90"), 4, "L", "img8.jpeg", casacas);

                //Accesorios
                producto(productos, "Cartera de cuero pequeña", new BigDecimal("59.90"), 10, "Única", "img9.jpeg", accesorios);
                producto(productos, "Gorro de Lana", new BigDecimal("39.90"), 24, "Única", "img10.jpeg", accesorios);

                //Camisas
                producto(productos, "Camisa Blanca", new BigDecimal("79.90"), 7, "M", "img11.jpeg", camisas);
                producto(productos, "Camisa de cuadros", new BigDecimal("89.90"), 5, "M", "img12.jpeg", camisas);
            }

            // Usuarios y ventas de ejemplo, solo la primera vez
            if (usuarios.count() == 0) {
                usuarios.save(new Usuario("Administrador", "admin@stylestore.com", "admin123", Rol.ADMINISTRADOR));
                Usuario vendedor1 = usuarios.save(new Usuario("Vendedor", "vendedor1@stylestore.com", "vendedor1", Rol.VENDEDOR));
                Usuario vendedor2 = usuarios.save(new Usuario("Vendedor", "vendedor2@stylestore.com", "vendedor2", Rol.VENDEDOR));

                if (ventas.count() == 0) {
                    Producto polo = productos.findFirstByNombre("Polo Básico").orElse(null);
                    Producto top = productos.findFirstByNombre("crop top").orElse(null);
                    Producto jean = productos.findFirstByNombre("Jean Recto").orElse(null);
                    Producto pantalon = productos.findFirstByNombre("Pantalón Largo").orElse(null);
                    Producto vestido = productos.findFirstByNombre("Vestido Casual").orElse(null);
                    Producto casaca = productos.findFirstByNombre("Casaca denim").orElse(null);
                    if (polo != null && top != null && jean != null && pantalon != null && vestido != null && casaca != null) {
                        ventas.save(venta(LocalDate.of(2026, 9, 1), vendedor1, new DetalleVenta(polo, 2), new DetalleVenta(jean, 1)));
                        ventas.save(venta(LocalDate.of(2026, 9, 2), vendedor2, new DetalleVenta(top, 1), new DetalleVenta(pantalon, 1)));
                        ventas.save(venta(LocalDate.of(2026, 9, 3), vendedor1, new DetalleVenta(vestido, 1), new DetalleVenta(casaca, 1)));
                    }
                }
            }
        };
    }

    /** Devuelve la categoría si ya existe */
    private static Categoria categoria(CategoriaRepository repo, String nombre, String descripcion) {
        return repo.findByNombre(nombre).orElseGet(() -> repo.save(new Categoria(nombre, descripcion)));
    }

    private static Producto producto(ProductoRepository repo, String nombre, BigDecimal precio, int stock, String talla, String imagen, Categoria categoria) {
        Optional<Producto> producto = repo.findFirstByNombre(nombre);
        if (producto.isPresent()) {
            return producto.get();
        }
        return repo.save(new Producto(nombre, precio, stock, talla, imagen, categoria));
    } 
    private static Venta venta(LocalDate fecha, Usuario vendedor, DetalleVenta... detalles) {
        Venta venta = new Venta(fecha, vendedor);
        for (DetalleVenta detalle : detalles) {
            venta.agregarDetalle(detalle);
        }
        return venta;
    }
}    

