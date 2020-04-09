package br.com.orlandoburli.livraria.emprestimo;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import br.com.orlandoburli.livraria.LivrariaApplication;
import br.com.orlandoburli.livraria.dto.EmprestimoDto;
import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.enums.StatusEmprestimo;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.service.EmprestimoService;
import br.com.orlandoburli.livraria.service.InstituicaoEnsinoService;
import br.com.orlandoburli.livraria.service.LivroService;
import br.com.orlandoburli.livraria.service.UsuarioService;
import br.com.orlandoburli.livraria.utils.ClockUtils;
import br.com.orlandoburli.livraria.utils.DbPrepareUtils;
import br.com.orlandoburli.livraria.utils.GeraCpfCnpj;
import br.com.orlandoburli.livraria.utils.ReflectionUtils;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = LivrariaApplication.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@TestPropertySource(locations = "classpath:application.yml")
public class EmprestimoResourceTests {

	@Autowired
	private DbPrepareUtils dbPrepareUtils;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private EmprestimoService service;

	@Autowired
	private LivroService livroService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private InstituicaoEnsinoService insituicaoEnsinoService;

	private final Faker faker = new Faker(new Locale("pt", "BR"));

	private final GeraCpfCnpj geradorCpfCnpj = new GeraCpfCnpj();

	private final ObjectMapper mapper = new ObjectMapper();

	@Mock
	private ClockUtils clock;

	// @formatter:off

	@Test
	public void deveRealizarEmprestimo() throws Exception {
		final UsuarioDto usuario = usuario();
		final LivroDto livro = livro();

		mvc.perform(
				post("/emprestar/" + livro.getId() + "/" + usuario.getId())
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id").isNumber())
					.andExpect(jsonPath("$.usuario.id", is(usuario.getId().intValue())))
					.andExpect(jsonPath("$.livro.id", is(livro.getId().intValue())))
					.andExpect(jsonPath("$.dataEmprestimo", is(clock.hoje().toString())))
					.andExpect(jsonPath("$.status", is(StatusEmprestimo.ABERTO.getDescricao())));
	}

	@Test
	public void deveRealizarReserva() throws Exception {
		final UsuarioDto usuario = usuario();
		final LivroDto livro = livro();

		mvc.perform(
				post("/reservar/" + livro.getId() + "/" + usuario.getId() + "/" + clock.hoje().toString())
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id").isNumber())
					.andExpect(jsonPath("$.usuario.id", is(usuario.getId().intValue())))
					.andExpect(jsonPath("$.livro.id", is(livro.getId().intValue())))
					.andExpect(jsonPath("$.dataReserva", is(clock.hoje().toString())));
	}

	@Test
	public void naoDeveRealizarEmprestimoLivroJaEmprestado() throws Exception {
		final UsuarioDto usuario1 = usuario();
		final UsuarioDto usuario2 = usuario();
		final LivroDto livro = livro();

		service.emprestar(usuario1.getId(), livro.getId());

		mvc.perform(
				post("/emprestar/" + livro.getId() + "/" + usuario2.getId())
					.content(mapper.writeValueAsBytes(usuario1))
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isConflict())
					.andExpect(jsonPath("$.message", is("Livro de id " + livro.getId() + " não pode ser emprestado, já está emprestado a outro usuário")));
	}

	@Test
	public void deveDevolverLivro() throws Exception {
		final UsuarioDto usuario = usuario();
		final LivroDto livro = livro();

		final EmprestimoDto emprestimo = service.emprestar(usuario.getId(), livro.getId());

		mvc.perform(
				post("/devolver/" + emprestimo.getId())
					.content(mapper.writeValueAsBytes(usuario))
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNoContent());
	}

	private UsuarioDto usuario() throws UsuarioException, InstituicaoEnsinoException, ValidationLivrariaException {
		final UsuarioDto usuario = UsuarioDto
				.builder()
					.nome(faker.name().fullName())
					.endereco(faker.address().fullAddress())
					.cpf(geradorCpfCnpj.cpf())
					.email(faker.internet().emailAddress())
					.telefone(faker.phoneNumber().phoneNumber())
					.instituicao(instituicao())
				.build();

		return usuarioService.create(usuario);
	}

	private InstituicaoEnsinoDto instituicao() throws InstituicaoEnsinoException, ValidationLivrariaException {
		final InstituicaoEnsinoDto instituicaoEnsino = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().cellPhone())
					.endereco(faker.address().fullAddress())
				.build();

		return insituicaoEnsinoService.create(instituicaoEnsino);
	}

	private LivroDto livro() throws LivroException, ValidationLivrariaException {
		final LivroDto livro = LivroDto
			.builder()
				.titulo(faker.book().title())
				.genero(faker.book().genre())
				.autor(faker.book().author())
				.sinopse(faker.lorem().characters(100, 200))
			.build();
		return livroService.create(livro);
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
