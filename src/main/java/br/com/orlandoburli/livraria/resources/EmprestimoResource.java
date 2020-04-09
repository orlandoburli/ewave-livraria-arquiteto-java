package br.com.orlandoburli.livraria.resources;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.orlandoburli.livraria.dto.EmprestimoDto;
import br.com.orlandoburli.livraria.dto.ReservaDto;
import br.com.orlandoburli.livraria.exceptions.emprestimo.EmprestimoException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.service.EmprestimoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("")
@Api(tags = "Empréstimos", description = "API para realizar empréstimos e devoluções.")
public class EmprestimoResource {

	// @formatter:off
	@Autowired
	private EmprestimoService service;

	@ApiOperation("Empresta um livro a um usuário.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Empréstimo realizado com sucesso."),
		@ApiResponse(code = 404, message = "Usuário ou livro não encontrado."),
		@ApiResponse(code = 422, message = "Dados inválidos.")
	})

	@PostMapping("emprestar/{livro}/{usuario}")
	@ResponseStatus(HttpStatus.CREATED)
	public EmprestimoDto emprestar(
			@ApiParam("Id do livro") @PathVariable("livro") final Long livroId,
			@ApiParam("Id do usuário") @PathVariable("usuario") final Long usuarioId
		) throws UsuarioException, LivroException, EmprestimoException, ValidationLivrariaException {
		return service.emprestar(usuarioId, livroId);
	}

	@ApiOperation("Reserva um livro para um usuário.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Reserva realizada com sucesso."),
		@ApiResponse(code = 404, message = "Usuário ou livro não encontrado."),
		@ApiResponse(code = 422, message = "Dados inválidos.")
	})
	@PostMapping("reservar/{livro}/{usuario}/{data}")
	@ResponseStatus(HttpStatus.CREATED)
	public ReservaDto reservar(
			@ApiParam("Id do livro") @PathVariable("livro") final Long livroId,
			@ApiParam("Id do usuário") @PathVariable("usuario") final Long usuarioId,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam("Data da reserva. Usar o formato YYYY-MM-DD") @PathVariable("data") final LocalDate data
		) throws UsuarioException, LivroException, EmprestimoException {
		return service.reservar(usuarioId, livroId, data);
	}

	@ApiOperation("Devolve um livro.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Livro devolvido com sucesso."),
		@ApiResponse(code = 404, message = "Empréstimo não encontrado."),
		@ApiResponse(code = 422, message = "Dados inválidos.")
	})
	@PostMapping("devolver/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void devolver(@ApiParam("Id do empréstimo") @PathVariable final Long id) throws EmprestimoException, ValidationLivrariaException {
		service.devolver(id);
	}
}