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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class ClienteSecurityIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    private Long clienteId;

    private final String tokenAdmin = TestJwtFactory.token("admin@test.com", "ADMIN");
    private final String tokenBibliotecario = TestJwtFactory.token("biblio@test.com", "BIBLIOTECARIO");
    private final String tokenCliente = TestJwtFactory.token("cliente@test.com", "CLIENTE");

    @BeforeEach
    void crearCliente() {
        Cliente cliente = new Cliente(null, "Cliente de pruebas", "cliente-" + UUID.randomUUID() + "@test.com",
                "600111222");
        clienteId = clienteRepository.save(cliente).getId();
    }

    @Test
    void consultarSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/clientes")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/clientes/" + clienteId)).andExpect(status().isUnauthorized());
    }

    @Test
    void consultarConRolClienteDevuelve403() throws Exception {
        mockMvc.perform(get("/clientes").header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenCliente))
                .andExpect(status().isForbidden());
    }

    @Test
    void consultarConAdminDevuelve200() throws Exception {
        mockMvc.perform(get("/clientes").header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk());

        mockMvc.perform(get("/clientes/" + clienteId).header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteId));
    }

    @Test
    void crearSinTokenDevuelve401() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Nuevo","email":"nuevo@test.com","telefono":"600000000"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearConBibliotecarioDevuelve201() throws Exception {
        mockMvc.perform(post("/clientes")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenBibliotecario)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Nuevo","email":"nuevo-%s@test.com","telefono":"600000000"}
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }
}
