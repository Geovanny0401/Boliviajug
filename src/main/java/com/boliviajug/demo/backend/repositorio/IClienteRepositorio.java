package com.boliviajug.demo.backend.repositorio;

import com.boliviajug.demo.backend.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClienteRepositorio extends JpaRepository<Cliente, Long> {


}
