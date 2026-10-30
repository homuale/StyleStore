# StyleStore – APF3

Sistema web de gestión de ventas e inventario para una tienda de ropa.
Spring Boot + Thymeleaf + Bootstrap + JPA/Hibernate + MySQL + Bean Validation.

## Ejecución
1. MySQL 8 encendido en `localhost:3306` (usuario `root`, clave `admin`; se pueden cambiar con las variables `DB_USER` y `DB_PASSWORD`).
2. (Opcional) cargar datos con `mysql -u root -p < DB_AFP3.sql`. Si no, el `DataLoader` crea los datos de ejemplo al iniciar.
3. `cd demo && ./mvnw spring-boot:run` y abrir http://localhost:8080
