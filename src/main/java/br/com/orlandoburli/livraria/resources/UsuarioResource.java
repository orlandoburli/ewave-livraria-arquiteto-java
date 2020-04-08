package br.com.orlandoburli.livraria.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.exceptions.LivrariaException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioException;
import br.com.orlandoburli.livraria.service.UsuarioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("usuarios")
@Api(tags = "Usuário", description = "API para manipulação de usuários.")
public class UsuarioResource {

	// @formatter:off

	@Autowired
	private UsuarioService service;

	@ApiOperation("Retorna um usuário pelo seu id.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Usuário retornado com sucesso."),
		@ApiResponse(code = 404, message = "Usuário não encontrado.")
	})
	@GetMapping("{id}")
	public UsuarioDto get(@PathVariable final Long id) throws UsuarioException {
		return service.get(id);
	}

	@ApiOperation("Cria um usuário.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Usuário criado com sucesso."),
		@ApiResponse(code = 422, message = "Dados inválidos")
	})
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioDto create(@RequestBody final UsuarioDto usuario) throws LivrariaException {
		return service.create(usuario);
	}

	@ApiOperation("Atualiza um usuário.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Usuário atualizado com sucesso."),
		@ApiResponse(code = 422, message = "Dados inválidos"),
		@ApiResponse(code = 409, message = "Cpf já cadastrado em outro usuário")
	})
	@PutMapping
	public UsuarioDto update(@RequestBody final UsuarioDto usuario) throws LivrariaException {
		return service.update(usuario);
	}

	@ApiOperation("Inativa um usuário.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Usuário inativado com sucesso."),
		@ApiResponse(code = 422, message = "Dados inválidos"),
		@ApiResponse(code = 409, message = "Usuário não pode ser excluído")
	})
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void destroy(@PathVariable final Long id) throws UsuarioException {
		service.destroy(id);
	}
}