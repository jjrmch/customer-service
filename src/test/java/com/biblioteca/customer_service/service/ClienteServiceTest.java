package com.biblioteca.customer_service.service;

import com.biblioteca.customer_service.dto.ClienteRequest;
import com.biblioteca.customer_service.dto.ClienteResponse;
import com.biblioteca.customer_service.exception.RecursoNoEncontradoException;
import com.biblioteca.customer_service.model.Cliente;
import com.biblioteca.customer_service.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente() {
        return new Cliente(1L, "Ana", "ana@test.com", "600111222");
    }

    private ClienteRequest request() {
        ClienteRequest request = new ClienteRequest();
        request.setNombre("Ana");
        request.setEmail("ana@test.com");
        request.setTelefono("600111222");
        return request;
    }

    @Test
    void listarTodosMapeaLosClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente()));

        List<ClienteResponse> clientes = clienteService.listarTodos();

        assertEquals(1, clientes.size());
        assertEquals("Ana", clientes.get(0).getNombre());
    }

    @Test
    void unaBusquedaVaciaNoConsultaElRepositorio() {
        assertTrue(clienteService.listarPorBusqueda("  ").isEmpty());
        verify(clienteRepository, never())
                .findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(any(), any());
    }

    @Test
    void laBusquedaPorTextoDevuelveCoincidencias() {
        when(clienteRepository.findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase("ana", "ana"))
                .thenReturn(List.of(cliente()));

        List<ClienteResponse> clientes = clienteService.listarPorBusqueda("ana");

        assertEquals(1, clientes.size());
        assertEquals("ana@test.com", clientes.get(0).getEmail());
    }

    @Test
    void buscarPorIdDevuelveElCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente()));

        assertEquals("Ana", clienteService.buscarPorId(1L).getNombre());
    }

    @Test
    void buscarPorIdInexistenteLanza404() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> clienteService.buscarPorId(99L));
    }

    @Test
    void buscarPorEmailDevuelveElCliente() {
        when(clienteRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(cliente()));

        assertEquals(1L, clienteService.buscarPorEmail("ana@test.com").getId());
    }

    @Test
    void buscarPorEmailInexistenteLanza404() {
        when(clienteRepository.findByEmail("nadie@test.com")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> clienteService.buscarPorEmail("nadie@test.com"));
    }

    @Test
    void guardarPersisteElCliente() {
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        ClienteResponse response = clienteService.guardar(request());

        assertEquals("Ana", response.getNombre());
        assertEquals("ana@test.com", response.getEmail());
    }

    @Test
    void actualizarModificaLosCampos() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente()));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        ClienteRequest request = request();
        request.setNombre("Ana Maria");
        request.setTelefono("600999888");
        ClienteResponse response = clienteService.actualizar(1L, request);

        assertEquals("Ana Maria", response.getNombre());
        assertEquals("600999888", response.getTelefono());
    }

    @Test
    void actualizarUnClienteInexistenteLanza404() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> clienteService.actualizar(99L, request()));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void eliminarUnClienteExistenteLoBorra() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        clienteService.eliminar(1L);

        verify(clienteRepository).deleteById(1L);
    }

    @Test
    void eliminarUnClienteInexistenteLanza404() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        assertThrows(RecursoNoEncontradoException.class, () -> clienteService.eliminar(99L));
        verify(clienteRepository, never()).deleteById(any());
    }
}
