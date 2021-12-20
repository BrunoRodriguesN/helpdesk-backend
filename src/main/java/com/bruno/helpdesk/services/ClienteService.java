package com.bruno.helpdesk.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bruno.helpdesk.domain.Cliente;
import com.bruno.helpdesk.domain.Pessoa;
import com.bruno.helpdesk.domain.dtos.ClienteDTO;
import com.bruno.helpdesk.repositories.ClienteRepository;
import com.bruno.helpdesk.repositories.PessoaRepository;
import com.bruno.helpdesk.services.exceptions.DataIntegrityViolationException;
import com.bruno.helpdesk.services.exceptions.ObjectnotFoundException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repository;
	@Autowired
	private PessoaRepository pessoaRepository;
	
	public Cliente findById(Integer id) {
		Optional<Cliente> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectnotFoundException("Objeto nao encontrado! id: " +id));
	}

	public List<Cliente> findAll() {
		return repository.findAll();
	}

	public Cliente create(ClienteDTO objDTO) {	
		objDTO.setId(null);
		validaPorCpfEEmail(objDTO);
		Cliente newObj = new Cliente(objDTO);
		return repository.save(newObj);
		
	}
	
	public Cliente update(Integer id, @Valid ClienteDTO objDTO) {
		objDTO.setId(id);
		Cliente oldObj = findById(id);
		validaPorCpfEEmail(objDTO);
		oldObj = new Cliente(objDTO);
		return repository.save(oldObj);
		
	}
	
	public void delete(Integer id) {
		Cliente obj = findById(id);
		if(obj.getChamados().size() > 0) {
			throw new DataIntegrityViolationException("Cliente possui ordens de serviço e não pode ser deletado!");
		}		
			repository.deleteById(id);		
	}



	private void validaPorCpfEEmail(ClienteDTO objDTO) {
		Optional<Pessoa> obj = pessoaRepository.findByCpf(objDTO.getCpf());
			if(obj.isPresent() && obj.get().getId() != objDTO.getId()) {
				throw new com.bruno.helpdesk.services.exceptions.DataIntegrityViolationException("CPF ja cadastrado no sistema!");
			}
			
			obj = pessoaRepository.findByEmail(objDTO.getEmail());
			if(obj.isPresent() && obj.get().getId() != objDTO.getId()) {
				throw new com.bruno.helpdesk.services.exceptions.DataIntegrityViolationException("EMAIL ja cadastrado no sistema!");
			}
	}

	

}
