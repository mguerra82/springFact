package com.epq.core.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
	
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page){
		
		Pageable pageable = PageRequest.of(page, 4);
		return clienteService.findAll(pageable);
		
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
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {

		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();
				
				
		/**
		 * Para obtener las validaciones en el modelo, se debe importar @Valid en el encabeza del metodo.												
		 */
		
		if(result.hasErrors()) {
			
			List<String> errors = new ArrayList<>();
			
			for(FieldError err: result.getFieldErrors()) {
				errors.add("El campor'" + err.getField() + "' "+ err.getDefaultMessage());
			}
			
			
		/*	List<String> errors = result.getFieldErrors().stream()
	                 									 .map(err -> "El campo '"+ err.getField() +"' "+ err.getDefaultMessage())
	                 				                     .collect(Collectors.toList());
			*/
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			
		}

		try {
			clienteNew = clienteService.save(cliente);

			if (clienteNew == null || clienteNew.getNombre().isEmpty() || clienteNew.getApellido().isBlank()|| clienteNew.getApellido().isEmpty()) 
			{
				response.put("mensaje", "El nombre y apellido no pueden estar vacios ");

				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_ACCEPTABLE);
			}

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al insertar en la base de datos, verifique los campos obligarios y que el correo no este repetido");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente ha sido creado con exito!.");
		response.put("cliente", clienteNew);

		return new ResponseEntity<Cliente>(clienteNew, HttpStatus.CREATED);

	}
	
	@PutMapping("clientes/{id}")
	public ResponseEntity<?> modificado(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {

		Cliente clienteActual = clienteService.findById(id);

		Cliente clienteUpdate = null;

		Map<String, Object> response = new HashMap<>();

		/**
		 * Para obtener las validaciones en el modelo, se debe importar @Valid en el encabeza del metodo.												
		 */
		
		if(result.hasErrors()) {
			
			List<String> errors = result.getFieldErrors().stream()
	                 									 .map(err -> "El campo '"+ err.getField() +"' "+ err.getDefaultMessage())
	                 				                     .collect(Collectors.toList());
			response.put("errors", errors);
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
			
		}

		
		
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
