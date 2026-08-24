package com.biblioteca.customer_service.service;

import com.biblioteca.customer_service.dto.ClienteRequest;
import com.biblioteca.customer_service.dto.ClienteResponse;
import com.biblioteca.customer_service.model.Cliente;
import com.biblioteca.customer_service.repository.ClienteRepository;
import com.biblioteca.customer_service.exception.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponse> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::aResponse)
                .toList();
    }

    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));
        return aResponse(cliente);
    }

    public ClienteResponse buscarPorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con email: " + email));
        return aResponse(cliente);
    }

    public ClienteResponse guardar(ClienteRequest request) {
        Cliente cliente = aEntidad(request);
        return aResponse(clienteRepository.save(cliente));
    }

    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));

        cliente.setNombre(request.getNombre());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());

        return aResponse(clienteRepository.save(cliente));
    }

    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteResponse aResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNombre(),
                cliente.getEmail(), cliente.getTelefono());
    }

    private Cliente aEntidad(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        return cliente;
    }
}