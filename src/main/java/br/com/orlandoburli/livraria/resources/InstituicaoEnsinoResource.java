package br.com.orlandoburli.livraria.resources;

import javax.transaction.Transactional;

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

import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.exceptions.LivrariaException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoException;
import br.com.orlandoburli.livraria.service.InstituicaoEnsinoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("instituicoes")
@Api(tags = "Instituições de Ensino", description = "API para manipulação das Instituições de Ensino.")
@Transactional
public class InstituicaoEnsinoResource {

	// @formatter:off

		@Autowired
		private InstituicaoEnsinoService service;

		@ApiOperation("Retorna uma instituição de ensino pelo seu id.")
		@ApiResponses({
			@ApiResponse(code = 200, message = "Instituição de Ensino retornada com sucesso."),
			@ApiResponse(code = 404, message = "Instituição de Ensino não encontrado.")
		})
		@GetMapping("{id}")
		public InstituicaoEnsinoDto get(@PathVariable final Long id) throws InstituicaoEnsinoException {
			return service.get(id);
		}

		@ApiOperation("Cria uma instituição de ensino.")
		@ApiResponses({
			@ApiResponse(code = 201, message = "Instituição de Ensino criada com sucesso."),
			@ApiResponse(code = 422, message = "Dados inválidos")
		})
		@PostMapping
		@ResponseStatus(HttpStatus.CREATED)
		public InstituicaoEnsinoDto create(@RequestBody final InstituicaoEnsinoDto usuario) throws LivrariaException {
			return service.create(usuario);
		}

		@ApiOperation("Atualiza uma instituição de ensino.")
		@ApiResponses({
			@ApiResponse(code = 200, message = "Instituição de Ensino atualizado com sucesso."),
			@ApiResponse(code = 422, message = "Dados inválidos"),
			@ApiResponse(code = 409, message = "Cnpj já cadastrado em outra instituição de ensino")
		})
		@PutMapping
		public InstituicaoEnsinoDto update(@RequestBody final InstituicaoEnsinoDto usuario) throws LivrariaException {
			return service.update(usuario);
		}

		@ApiOperation("Inativa uma instituição de ensino.")
		@ApiResponses({
			@ApiResponse(code = 204, message = "Instituição de Ensino inativado com sucesso."),
			@ApiResponse(code = 422, message = "Dados inválidos"),
			@ApiResponse(code = 409, message = "Instituição de Ensino não pode ser excluído")
		})
		@DeleteMapping("{id}")
		@ResponseStatus(HttpStatus.NO_CONTENT)
		public void destroy(@PathVariable final Long id) throws InstituicaoEnsinoException {
			service.destroy(id);
		}
}
