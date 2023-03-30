package com.epq.core.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.epq.core.model.entity.Cliente;

public interface IClienteDao extends JpaRepository<Cliente, Long> {

}
