package com.biblioteca.customer_service;

import com.biblioteca.customer_service.model.Cliente;
import com.biblioteca.customer_service.repository.ClienteRepository;
import com.biblioteca.customer_service.support.TestJwtFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ClienteControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    private final String tokenAdmin = TestJwtFactory.token("admin@test.com", "ADMIN");

    private String nombre;
    private String email;
    private Long clienteId;

    @BeforeEach
    void crearClienteDePruebas() {
        String sufijo = UUID.randomUUID().toString();
        nombre = "Cliente " + sufijo;
        email = "cliente-" + sufijo + "@test.com";
        Cliente cliente = new Cliente(null, nombre, email, "600111222");
        clienteId = clienteRepository.save(cliente).getId();
    }

    private String body(String nombre, String email, String telefono) {
        return """
                {"nombre":"%s","email":"%s","telefono":"%s"}
                """.formatted(nombre, email, telefono);
    }

    @Test
    void listarClientesIncluyeElClienteGuardado() throws Exception {
        mockMvc.perform(get("/clientes").header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(email)));
    }

    @Test
    void obtenerClientePorIdDevuelveElCliente() throws Exception {
        mockMvc.perform(get("/clientes/" + clienteId).header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteId))
                .andExpect(jsonPath("$.nombre").value(nombre))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void obtenerClienteInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/clientes/99999999").header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value(containsString("Cliente no encontrado")));
    }

    @Test
    void obtenerClientePorEmailDevuelveElCliente() throws Exception {
        mockMvc.perform(get("/clientes/email/" + email).header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void obtenerClientePorEmailInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/clientes/email/no-existe-" + UUID.randomUUID() + "@test.com")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(containsString("Cliente no encontrado")));
    }

    @Test
    void buscarClientesPorTextoDevuelveCoincidencias() throws Exception {
        mockMvc.perform(get("/clientes/buscar")
                        .param("q", nombre)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(email)));
    }

    @Test
    void buscarClientesSinCoincidenciasDevuelveListaVacia() throws Exception {
        mockMvc.perform(get("/clientes/buscar")
                        .param("q", "sin-coincidencias-" + UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void crearClienteDevuelve201YPersisteElCliente() throws Exception {
        String nuevoEmail = "nuevo-" + UUID.randomUUID() + "@test.com";

        mockMvc.perform(post("/clientes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Nuevo Cliente", nuevoEmail, "699888777")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(nuevoEmail));

        assertTrue(clienteRepository.findByEmail(nuevoEmail).isPresent());
    }

    @Test
    void crearClienteConDatosInvalidosDevuelve400ConErrores() throws Exception {
        mockMvc.perform(post("/clientes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("", "no-es-un-email", "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Error de validación"))
                .andExpect(jsonPath("$.errores.nombre").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.errores.email").value("El email no es válido"))
                .andExpect(jsonPath("$.errores.telefono").value("El teléfono es obligatorio"));
    }

    @Test
    void actualizarClienteCambiaLosDatos() throws Exception {
        mockMvc.perform(put("/clientes/" + clienteId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Nombre Actualizado", email, "611222333")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nombre Actualizado"))
                .andExpect(jsonPath("$.telefono").value("611222333"));
    }

    @Test
    void actualizarClienteInexistenteDevuelve404() throws Exception {
        mockMvc.perform(put("/clientes/99999999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Nadie", "nadie@test.com", "600000000")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(containsString("Cliente no encontrado")));
    }

    @Test
    void eliminarClienteDevuelve204YLoBorra() throws Exception {
        mockMvc.perform(delete("/clientes/" + clienteId).header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());

        assertTrue(clienteRepository.findById(clienteId).isEmpty());
    }

    @Test
    void eliminarClienteInexistenteDevuelve404() throws Exception {
        mockMvc.perform(delete("/clientes/99999999").header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(containsString("Cliente no encontrado")));
    }
}
