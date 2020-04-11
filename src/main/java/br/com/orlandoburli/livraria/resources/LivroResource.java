package br.com.orlandoburli.livraria.resources;

import java.io.IOException;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.exceptions.LivrariaException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroException;
import br.com.orlandoburli.livraria.service.LivroService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("livros")
@Api(tags = "Livros", description = "API para manipulação de livros.")
@Transactional
public class LivroResource {

	// @formatter:off

	@Autowired
	private LivroService service;

	@ApiOperation("Retorna um livro pelo seu id.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Livro retornado com sucesso."),
		@ApiResponse(code = 404, message = "Livro não encontrado.")
	})
	@GetMapping("{id}")
	public LivroDto get(@PathVariable final Long id) throws LivroException {
		return service.get(id);
	}

	@ApiOperation("Cria um livro.")
	@ApiResponses({
		@ApiResponse(code = 201, message = "Livro criado com sucesso."),
		@ApiResponse(code = 422, message = "Dados inválidos")
	})
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public LivroDto create(@RequestBody final LivroDto livro) throws LivrariaException {
		return service.create(livro);
	}

	@ApiOperation("Atualiza um livro.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Livro atualizado com sucesso."),
		@ApiResponse(code = 422, message = "Dados inválidos")
	})
	@PutMapping
	public LivroDto update(@RequestBody final LivroDto livro) throws LivrariaException {
		return service.update(livro);
	}

	@ApiOperation("Inativa um livro.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Livro inativado com sucesso."),
		@ApiResponse(code = 422, message = "Dados inválidos"),
		@ApiResponse(code = 409, message = "Livro não pode ser excluído")
	})
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void destroy(@PathVariable final Long id) throws LivroException {
		service.destroy(id);
	}

	@ApiOperation("Faz o upload da capa de um livro.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Capa salva com sucesso."),
		@ApiResponse(code = 422, message = "Dados inválidos")
	})
	@PostMapping("{id}/capa")
	@ResponseStatus(HttpStatus.CREATED)
	public void uploadCapa(@PathVariable final Long id, @RequestParam("file") final MultipartFile file) throws LivroException, IOException {
		service.saveCapa(id, file.getBytes());
	}

	@ApiOperation("Retorna a capa de um livro.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Capa retornada com sucesso"),
		@ApiResponse(code = 404, message = "Capa não encontrada")
	})
	@GetMapping("{id}/capa")
	public byte[] getCapa(@PathVariable final Long id) throws LivroException {
		return service.getCapa(id);
	}
}
