package br.com.orlandoburli.livraria.emprestimo;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.github.javafaker.Faker;

import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoNaoInformadaException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.service.EmprestimoService;
import br.com.orlandoburli.livraria.service.InstituicaoEnsinoService;
import br.com.orlandoburli.livraria.service.LivroService;
import br.com.orlandoburli.livraria.service.UsuarioService;
import br.com.orlandoburli.livraria.utils.DbPrepareUtils;
import br.com.orlandoburli.livraria.utils.GeraCpfCnpj;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@TestPropertySource(locations = "classpath:application.yml")
@Transactional
public class EmprestimoServiceTests {

	@Autowired
	private EmprestimoService service;

	@Autowired
	private DbPrepareUtils dbPrepareUtils;

	@Autowired
	private LivroService livroService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private InstituicaoEnsinoService insituicaoEnsinoService;

	private final Faker faker = new Faker(new Locale("pt", "BR"));

	private final GeraCpfCnpj geradorCpfCnpj = new GeraCpfCnpj();

	@Test
	public void deveEmprestarLivro() throws UsuarioNaoInformadoException, InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, LivroNaoInformadoException, UsuarioNaoEncontradoException, LivroNaoEncontradoException {
		final LivroDto livro = livro();
		final UsuarioDto usuario = usuario();

		service.realizarEmprestimo(usuario.getId(), livro.getId());
	}

	// @formatter:off
	private LivroDto livro() throws LivroNaoInformadoException, ValidationLivrariaException {
		final LivroDto livro = LivroDto
				.builder()
				.titulo(faker.book().title())
				.genero(faker.book().genre())
				.autor(faker.book().author())
				.sinopse(faker.lorem().characters(100, 200))
				.build();
		return livroService.create(livro);
	}

	private InstituicaoEnsinoDto instituicao() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		final InstituicaoEnsinoDto instituicaoEnsino = InstituicaoEnsinoDto
				.builder()
				.nome(faker.company().name())
				.cnpj(geradorCpfCnpj.cnpj())
				.telefone(faker.phoneNumber().cellPhone())
				.endereco(faker.address().fullAddress())
				.build();

		return insituicaoEnsinoService.create(instituicaoEnsino);
	}

	private UsuarioDto usuario() throws UsuarioNaoInformadoException, ValidationLivrariaException, InstituicaoEnsinoNaoInformadaException {
		final UsuarioDto usuarioDto = UsuarioDto
				.builder()
				.nome(faker.name().fullName())
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.instituicao(instituicao())
				.build();

		return usuarioService.create(usuarioDto);
	}

	@BeforeEach
	public void prepare() {
		dbPrepareUtils.clean();
	}
}
