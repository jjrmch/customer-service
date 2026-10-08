# Changelog

Todos los cambios relevantes de este proyecto se documentan en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/)
y este proyecto sigue [Semantic Versioning](https://semver.org/lang/es/).

## [1.1.0] - 2026-10-08

### Añadido

- 13 tests de integración HTTP de clientes: listado, búsqueda, consulta por id y email, validaciones 400, 404, actualización y borrado

### Cambiado

- Los tests de integración comparten un único contenedor PostgreSQL
- README actualizado con el total de tests (30)

## [1.0.0] - 2026-10-05

### Añadido

- CRUD de clientes (nombre, email, teléfono)
- Búsqueda de clientes por email, usada por el resto del sistema
- Seguridad con JWT (HS256): solo `ADMIN` / `BIBLIOTECARIO`, con 401/403 en JSON
- Validación de entrada (`@NotBlank`, `@Email`) y manejo global de excepciones
- Documentación OpenAPI/Swagger y registro en Eureka
