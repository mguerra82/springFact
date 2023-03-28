package com.epq.core.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epq.core.model.entity.Cliente;
import com.epq.core.model.services.IClienteService;

@CrossOrigin()
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
		return clienteService.findAll();
		
	}
	
	@GetMapping("/clientes/{id}")
	//@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		 
		try {
			cliente = clienteService.findById(id);
		}catch(DataAccessException e) {
			response.put("mensaje","Erro al consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			 return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if(cliente == null) {
			 response.put("mensaje","El cliente ID: ".concat(id.toString()).concat(" noexiste en la base de datos"));
			 return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		 }
		 
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	
	}
	
	@PostMapping("clientes")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> create(@RequestBody Cliente cliente) {

		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();

		try {
			clienteNew = clienteService.save(cliente);

			if (clienteNew == null || clienteNew.getNombre().isEmpty() || clienteNew.getApellido().isBlank()
					|| clienteNew.getApellido().isEmpty()) {
				response.put("mensaje", "El nombre y apellido no pueden estar vacios ");

				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_ACCEPTABLE);
			}

		} catch (DataAccessException e) {
			response.put("mensaje", "Erro al insertar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente ha sido creado con exito!.");
		response.put("cliente", clienteNew);

		return new ResponseEntity<Cliente>(clienteNew, HttpStatus.CREATED);

	}
	
	@PutMapping("clientes/{id}")
	public ResponseEntity<?> modificado(@RequestBody Cliente cliente, @PathVariable Long id) {

		Cliente clienteActual = clienteService.findById(id);

		Cliente clienteUpdate = null;

		Map<String, Object> response = new HashMap<>();

		System.out.println("CLIENTE:........"+ clienteActual.getNombre().toString());
		
		
		if (clienteActual == null || clienteActual.getNombre().isEmpty() || clienteActual.getNombre().isBlank() || clienteActual.getApellido().isBlank()
				|| clienteActual.getApellido().isEmpty()) {
			
			
			response.put("mensaje", "El nombre y apellido no pueden estar vacios ");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		
		try {
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setEmail(cliente.getEmail());
			//clienteActual.setCreateAt(cliente.getCreateAt());

			clienteUpdate = clienteService.save(clienteActual);

		} catch (DataAccessException e) {
			response.put("mensaje", "Erro al actualizar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente ha sido actualizado con exito.");
		response.put("cliente", clienteActual);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
	}
	
	@DeleteMapping("clientes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Long id) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
			
			clienteService.delete(id);
			
		} catch (DataAccessException e) {
			response.put("mensaje", "Erro al eliminar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje","El cliente ha sido eliminado con exito.");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
	}

}
