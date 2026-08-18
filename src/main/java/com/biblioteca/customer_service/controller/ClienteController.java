package com.biblioteca.customer_service.controller;

import com.biblioteca.customer_service.dto.ClienteRequest;
import com.biblioteca.customer_service.dto.ClienteResponse;
import com.biblioteca.customer_service.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteResponse> listarClientes() {
        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ClienteResponse obtenerCliente(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    @GetMapping("/email/{email}")
    public ClienteResponse obtenerPorEmail(@PathVariable String email) {
        return clienteService.buscarPorEmail(email);
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crearCliente(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse creado = clienteService.guardar(request);
        return ResponseEntity.status(201).body(creado);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return clienteService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}