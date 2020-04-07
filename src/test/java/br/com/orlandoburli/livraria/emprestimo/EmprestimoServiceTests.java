package br.com.orlandoburli.livraria.emprestimo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.github.javafaker.Faker;

import br.com.orlandoburli.livraria.dto.EmprestimoDto;
import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.enums.StatusEmprestimo;
import br.com.orlandoburli.livraria.exceptions.emprestimo.EmprestimoJaDevolvidoException;
import br.com.orlandoburli.livraria.exceptions.emprestimo.EmprestimoNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.emprestimo.EmprestimoNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.emprestimo.LivroJaEmprestadoException;
import br.com.orlandoburli.livraria.exceptions.emprestimo.MaximoPedidosUsuarioException;
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
import br.com.orlandoburli.livraria.utils.ClockUtils;
import br.com.orlandoburli.livraria.utils.DbPrepareUtils;
import br.com.orlandoburli.livraria.utils.GeraCpfCnpj;
import br.com.orlandoburli.livraria.utils.ReflectionUtils;

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

	@Mock
	private ClockUtils clock;

	private final Faker faker = new Faker(new Locale("pt", "BR"));

	private final GeraCpfCnpj geradorCpfCnpj = new GeraCpfCnpj();

	@Test
	public void deveEmprestarLivro() throws UsuarioNaoInformadoException, InstituicaoEnsinoNaoInformadaException,
			ValidationLivrariaException, LivroNaoInformadoException, UsuarioNaoEncontradoException,
			LivroNaoEncontradoException, LivroJaEmprestadoException, MaximoPedidosUsuarioException {

		final LivroDto livro = livro();

		final UsuarioDto usuario = usuario();

		final EmprestimoDto emprestimo = service.realizarEmprestimo(usuario.getId(), livro.getId());

		assertThat(emprestimo, is(notNullValue()));
		assertThat(emprestimo.getId(), is(greaterThan(0L)));
		assertThat(emprestimo.getLivro().getId(), is(equalTo(livro.getId())));
		assertThat(emprestimo.getUsuario().getId(), is(equalTo(usuario.getId())));
		assertThat(emprestimo.getDataEmprestimo(), is(equalTo(LocalDate.now())));
		assertThat(emprestimo.getStatus(), is(equalTo(StatusEmprestimo.ABERTO)));
	}

	@Test
	public void naoDeveEmprestarLivroInexistente()
			throws UsuarioNaoInformadoException, InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException,
			LivroNaoInformadoException, UsuarioNaoEncontradoException, LivroNaoEncontradoException {

		final UsuarioDto usuario = usuario();

		assertThrows(LivroNaoEncontradoException.class,
				() -> service.realizarEmprestimo(usuario.getId(), faker.random().nextLong()));
	}

	@Test
	public void naoDeveEmprestarLivroNulo()
			throws UsuarioNaoInformadoException, InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException,
			LivroNaoInformadoException, UsuarioNaoEncontradoException, LivroNaoEncontradoException {

		final UsuarioDto usuario = usuario();

		assertThrows(LivroNaoInformadoException.class, () -> service.realizarEmprestimo(usuario.getId(), null));
	}

	@Test
	public void naoDeveEmprestarUsuarioInexistente()
			throws UsuarioNaoInformadoException, InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException,
			LivroNaoInformadoException, UsuarioNaoEncontradoException, LivroNaoEncontradoException {

		final LivroDto livro = livro();

		assertThrows(UsuarioNaoEncontradoException.class,
				() -> service.realizarEmprestimo(faker.random().nextLong(), livro.getId()));
	}

	@Test
	public void naoDeveEmprestarUsuarioNulo()
			throws UsuarioNaoInformadoException, InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException,
			LivroNaoInformadoException, UsuarioNaoEncontradoException, LivroNaoEncontradoException {

		final LivroDto livro = livro();

		assertThrows(UsuarioNaoInformadoException.class, () -> service.realizarEmprestimo(null, livro.getId()));
	}

	@Test
	public void deveEncontrarEmprestimo() throws UsuarioNaoInformadoException, InstituicaoEnsinoNaoInformadaException,
			ValidationLivrariaException, LivroNaoInformadoException, UsuarioNaoEncontradoException,
			LivroNaoEncontradoException, EmprestimoNaoEncontradoException, EmprestimoJaDevolvidoException,
			EmprestimoNaoInformadoException, LivroJaEmprestadoException, MaximoPedidosUsuarioException {

		final LivroDto livro = livro();

		final UsuarioDto usuario = usuario();

		final EmprestimoDto emprestimo = service.realizarEmprestimo(usuario.getId(), livro.getId());

		final EmprestimoDto founded = service.get(emprestimo.getId());

		assertThat(founded, is(notNullValue()));
	}

	@Test
	public void deveEmprestarDoisLivros() throws UsuarioNaoInformadoException, InstituicaoEnsinoNaoInformadaException,
			ValidationLivrariaException, LivroNaoInformadoException, UsuarioNaoEncontradoException,
			LivroNaoEncontradoException, LivroJaEmprestadoException, MaximoPedidosUsuarioException {

		final LivroDto livro1 = livro();
		final LivroDto livro2 = livro();

		final UsuarioDto usuario = usuario();

		final EmprestimoDto emprestimo1 = service.realizarEmprestimo(usuario.getId(), livro1.getId());
		assertThat(emprestimo1, is(notNullValue()));

		final EmprestimoDto emprestimo2 = service.realizarEmprestimo(usuario.getId(), livro2.getId());
		assertThat(emprestimo2, is(notNullValue()));
	}

	@Test
	public void naoDeveEmprestarMaisDe2Livros()
			throws UsuarioNaoInformadoException, InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException,
			LivroNaoInformadoException, UsuarioNaoEncontradoException, LivroNaoEncontradoException,
			LivroJaEmprestadoException, MaximoPedidosUsuarioException {

		final LivroDto livro1 = livro();
		final LivroDto livro2 = livro();
		final LivroDto livro3 = livro();

		final UsuarioDto usuario = usuario();

		final EmprestimoDto emprestimo1 = service.realizarEmprestimo(usuario.getId(), livro1.getId());
		assertThat(emprestimo1, is(notNullValue()));

		final EmprestimoDto emprestimo2 = service.realizarEmprestimo(usuario.getId(), livro2.getId());
		assertThat(emprestimo2, is(notNullValue()));

		assertThrows(MaximoPedidosUsuarioException.class,
				() -> service.realizarEmprestimo(usuario.getId(), livro3.getId()));
	}

	@Test
	public void naoDeveEmprestarOMesmoLivroJaEmprestado()
			throws UsuarioNaoInformadoException, InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException,
			LivroNaoInformadoException, UsuarioNaoEncontradoException, LivroNaoEncontradoException,
			LivroJaEmprestadoException, MaximoPedidosUsuarioException {

		final LivroDto livro = livro();

		final UsuarioDto usuario1 = usuario();
		final UsuarioDto usuario2 = usuario();

		final EmprestimoDto emprestimo1 = service.realizarEmprestimo(usuario1.getId(), livro.getId());
		assertThat(emprestimo1, is(notNullValue()));

		assertThrows(LivroJaEmprestadoException.class,
				() -> service.realizarEmprestimo(usuario2.getId(), livro.getId()));
	}

	@Test
	public void naoDeveRetornarEmprestimoInexistente() {
		assertThrows(EmprestimoNaoEncontradoException.class, () -> service.get(faker.random().nextLong()));
	}

	@Test
	public void naoDeveRetornarEmprestimoNulo() {
		assertThrows(EmprestimoNaoInformadoException.class, () -> service.get(null));
	}

	@Test
	public void deveDevolverLivro() throws UsuarioNaoInformadoException, InstituicaoEnsinoNaoInformadaException,
			ValidationLivrariaException, LivroNaoInformadoException, UsuarioNaoEncontradoException,
			LivroNaoEncontradoException, EmprestimoNaoEncontradoException, EmprestimoJaDevolvidoException,
			EmprestimoNaoInformadoException, LivroJaEmprestadoException, MaximoPedidosUsuarioException {

		final LivroDto livro = livro();

		final UsuarioDto usuario = usuario();

		final EmprestimoDto emprestimo = service.realizarEmprestimo(usuario.getId(), livro.getId());

		service.devolverLivro(emprestimo.getId());

		final EmprestimoDto founded = service.get(emprestimo.getId());

		assertThat(founded, is(notNullValue()));
		assertThat(founded.getDataDevolucao(), is(equalTo(LocalDate.now())));
		assertThat(founded.getStatus(), is(equalTo(StatusEmprestimo.DEVOLVIDO)));
	}

	@Test
	public void deveDevolverLivroAposMenosDe30DiasDoEmprestimo()
			throws LivroNaoInformadoException, ValidationLivrariaException, UsuarioNaoInformadoException,
			InstituicaoEnsinoNaoInformadaException, UsuarioNaoEncontradoException, LivroNaoEncontradoException,
			EmprestimoNaoEncontradoException, EmprestimoJaDevolvidoException, EmprestimoNaoInformadoException,
			LivroJaEmprestadoException, MaximoPedidosUsuarioException {

		final LivroDto livro = livro();

		final UsuarioDto usuario = usuario();

		final EmprestimoDto emprestimo = service.realizarEmprestimo(usuario.getId(), livro.getId());

		final LocalDate dataDevolucao = LocalDate.now().plusDays(faker.random().nextInt(1, 29));

		prepareClockMockFor(dataDevolucao);

		service.devolverLivro(emprestimo.getId());

		final EmprestimoDto founded = service.get(emprestimo.getId());

		assertThat(founded, is(notNullValue()));
		assertThat(founded.getDataDevolucao(), is(equalTo(dataDevolucao)));
		assertThat(founded.getStatus(), is(equalTo(StatusEmprestimo.DEVOLVIDO)));
	}

	@Test
	public void naoDeveDevolverLivroEmprestimoNaoExistente() {
		assertThrows(EmprestimoNaoEncontradoException.class, () -> service.devolverLivro(faker.random().nextLong()));
	}

	@Test
	public void naoDeveDevolverLivroEmprestimoNulo() {
		assertThrows(EmprestimoNaoInformadoException.class, () -> service.devolverLivro(null));
	}

	@Test
	public void naoDeveDevolverLivroJaDevolvido()
			throws UsuarioNaoInformadoException, InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException,
			LivroNaoInformadoException, UsuarioNaoEncontradoException, LivroNaoEncontradoException,
			EmprestimoNaoEncontradoException, EmprestimoJaDevolvidoException, EmprestimoNaoInformadoException,
			LivroJaEmprestadoException, MaximoPedidosUsuarioException {

		final LivroDto livro = livro();

		final UsuarioDto usuario = usuario();

		final EmprestimoDto emprestimo = service.realizarEmprestimo(usuario.getId(), livro.getId());

		service.devolverLivro(emprestimo.getId());

		assertThrows(EmprestimoJaDevolvidoException.class, () -> service.devolverLivro(emprestimo.getId()));
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

		ReflectionUtils.setValue("clock", service, clock);

		prepareClockMockForToday();
	}

	private void prepareClockMockForToday() {
		prepareClockMockFor(LocalDate.now());
	}

	private void prepareClockMockFor(final LocalDate date) {
		when(clock.hoje()).thenReturn(date);
	}
}
