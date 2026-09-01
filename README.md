# Customer Service

Microservicio de clientes de la plataforma de gestión de biblioteca. Hace el CRUD de clientes y permite buscarlos por email, que es lo que usan otros servicios del ecosistema para validar o enriquecer sus operaciones (por ejemplo, transactions-service al registrar una venta o un alquiler).

Es el servicio más sencillo del sistema y me sirvió para asentar la estructura base que luego repetí en el resto de microservicios: paquetes de controller, service, repository, dto y manejo global de excepciones.

## Qué hace

- CRUD de clientes (nombre, email, teléfono)
- Búsqueda por email
- Validación de datos de entrada (`@NotBlank`, `@Email`)
- Swagger UI en `/swagger-ui.html`

## Stack

- Java 17
- Spring Boot 4.1
- Spring Cloud 2025.1.2 (Eureka client)
- Spring Data JPA
- PostgreSQL
- springdoc-openapi

## Cómo ejecutarlo

Necesitas PostgreSQL y el discovery-service (Eureka) levantados. Puedes levantar todo el stack con docker-compose desde `biblioteca-deploy`, o ejecutar este servicio solo:

```bash
./mvnw spring-boot:run
```

La configuración de la base de datos se hace por variables de entorno:

| Variable | Descripción |
|---|---|
| `DB_URL` | JDBC URL de PostgreSQL (default `jdbc:postgresql://localhost:5432/clientes`) |
| `DB_USER` | Usuario de PostgreSQL |
| `DB_PASSWORD` | Contraseña de PostgreSQL |
| `EUREKA_URL` | URL del servidor Eureka (default `http://localhost:8761/eureka/`) |

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/clientes` | Lista todos los clientes |
| GET | `/clientes/{id}` | Obtiene un cliente por id |
| GET | `/clientes/email/{email}` | Obtiene un cliente por email |
| POST | `/clientes` | Crea un cliente |
| PUT | `/clientes/{id}` | Actualiza un cliente |
| DELETE | `/clientes/{id}` | Elimina un cliente (404 si no existe) |

## Parte de un sistema más grande

La plataforma completa se compone de:

- [discovery-service](https://github.com/jjrmch/discovery-service) — servidor Eureka
- [gateway-service](https://github.com/jjrmch/gateway-service) — API Gateway (punto de entrada, `localhost:8080`)
- [catalog-service](https://github.com/jjrmch/catalog-service) — catálogo de libros y stock
- [transactions-service](https://github.com/jjrmch/transactions-service) — ventas, alquileres, reservas y multas
- [biblioteca-frontend](https://github.com/jjrmch/biblioteca-frontend) — panel web en React
- [biblioteca-deploy](https://github.com/jjrmch/biblioteca-deploy) — docker-compose con el stack completo

## Por mejorar

- No hay tests de negocio todavía, solo el test de contexto de Spring.
- El listado de clientes no tiene paginación.

## Licencia

MIT
