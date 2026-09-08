package com.biblioteca.customer_service.repository;

import com.biblioteca.customer_service.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    List<Cliente> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(String nombre, String email);
}