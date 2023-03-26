package com.epq.core.model.dao;

import org.springframework.data.repository.CrudRepository;

import com.epq.core.model.entity.Cliente;

public interface IClienteDao extends CrudRepository<Cliente, Long> {

}
