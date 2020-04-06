package br.com.orlandoburli.livraria.livro;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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

import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.exceptions.livro.CapaNaoEncontradaException;
import br.com.orlandoburli.livraria.exceptions.livro.CapaNaoInformadaException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.model.Livro;
import br.com.orlandoburli.livraria.repository.LivroRepository;
import br.com.orlandoburli.livraria.service.LivroService;
import br.com.orlandoburli.livraria.utils.DbPrepareUtils;
import br.com.orlandoburli.livraria.utils.ImageUtils;
import br.com.orlandoburli.livraria.utils.Utils;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@TestPropertySource(locations = "classpath:application.yml")
@Transactional
public class LivroServiceTests {

	@Autowired
	private LivroService service;

	@Autowired
	private LivroRepository repository;

	@Autowired
	private DbPrepareUtils dbPrepareUtils;

	@Autowired
	private ImageUtils imageUtils;

	private Faker faker = new Faker(new Locale("pt", "BR"));

	@Test
	public void deveCriarUmLivro() throws LivroNaoInformadoException, ValidationLivrariaException {
		LivroDto livro = buildLivroRandom();

		LivroDto created = service.create(livro);

		assertThat(created, is(notNullValue()));
		assertThat(created.getId(), is(greaterThan(0L)));
		assertThat(created.getTitulo(), is(equalTo(livro.getTitulo())));
		assertThat(created.getGenero(), is(equalTo(livro.getGenero())));
		assertThat(created.getAutor(), is(equalTo(livro.getAutor())));
		assertThat(created.getSinopse(), is(equalTo(livro.getSinopse())));
		assertThat(created.getStatus(), is(equalTo(Status.ATIVO)));
	}

	@Test
	public void naoDeveCriarUmLivroNulo() {
		assertThrows(LivroNaoInformadoException.class, () -> service.create(null));
	}

	@Test
	public void naoDeveCriarUmLivroComTituloNulo() {
		LivroDto livro = buildLivroRandom();

		livro.setTitulo(null);

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("titulo"));

		assertThat(exception.getErrors().get("titulo"), hasItem("Título é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmLivroComTituloVazio() {
		LivroDto livro = buildLivroRandom();

		livro.setTitulo("   ");

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("titulo"));

		assertThat(exception.getErrors().get("titulo"), hasItem("Título é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmLivroComTituloComMenosDe3Caracteres() {
		LivroDto livro = buildLivroRandom();

		livro.setTitulo(faker.lorem().characters(1, 2));

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("titulo"));

		assertThat(exception.getErrors().get("titulo"), hasItem("Título deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmLivroComTituloComMaisDe100Caracteres() {
		LivroDto livro = buildLivroRandom();

		livro.setTitulo(faker.lorem().characters(101, 200));

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("titulo"));

		assertThat(exception.getErrors().get("titulo"), hasItem("Título deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmLivroComGeneroNulo() {
		LivroDto livro = buildLivroRandom();

		livro.setGenero(null);

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("genero"));

		assertThat(exception.getErrors().get("genero"), hasItem("Gênero é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmLivroComGeneroVazio() {
		LivroDto livro = buildLivroRandom();

		livro.setGenero("    ");

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("genero"));

		assertThat(exception.getErrors().get("genero"), hasItem("Gênero é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmLivroComGeneroComMenosDe3Caracteres() {
		LivroDto livro = buildLivroRandom();

		livro.setGenero(faker.lorem().characters(1, 2));

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("genero"));

		assertThat(exception.getErrors().get("genero"), hasItem("Gênero deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmLivroComGeneroComMaisDe100Caracteres() {
		LivroDto livro = buildLivroRandom();

		livro.setGenero(faker.lorem().characters(101, 200));

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("genero"));

		assertThat(exception.getErrors().get("genero"), hasItem("Gênero deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmLivroComAutorNulo() {
		LivroDto livro = buildLivroRandom();

		livro.setAutor(null);

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("autor"));

		assertThat(exception.getErrors().get("autor"), hasItem("Autor é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmLivroComAutorVazio() {
		LivroDto livro = buildLivroRandom();

		livro.setAutor("    ");

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("autor"));

		assertThat(exception.getErrors().get("autor"), hasItem("Autor é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmLivroComAutorComMenosDe3Caracteres() {
		LivroDto livro = buildLivroRandom();

		livro.setAutor(faker.lorem().characters(1, 2));

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("autor"));

		assertThat(exception.getErrors().get("autor"), hasItem("Autor deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmLivroComAutorComMaisDe100Caracteres() {
		LivroDto livro = buildLivroRandom();

		livro.setAutor(faker.lorem().characters(101, 200));

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("autor"));

		assertThat(exception.getErrors().get("autor"), hasItem("Autor deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmLivroComSinopseComMaisDe1000Caracteres() {
		LivroDto livro = buildLivroRandom();

		livro.setSinopse(faker.lorem().characters(1001, 2000));

		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(livro));

		assertTrue(exception.getErrors().containsKey("sinopse"));

		assertThat(exception.getErrors().get("sinopse"), hasItem("Sinopse deve ter até 1000 caracteres"));
	}

	@Test
	public void deveAtualizarLivro()
			throws LivroNaoInformadoException, ValidationLivrariaException, LivroNaoEncontradoException {
		LivroDto livro = buildLivroRandom();

		LivroDto created = service.create(livro);

		created.setTitulo("Novo titulo");

		LivroDto updated = service.update(created);

		assertThat(updated, is(notNullValue()));
		assertThat(updated.getId(), is(equalTo(created.getId())));
		assertThat(updated.getTitulo(), is(equalTo(created.getTitulo())));
		assertThat(updated.getStatus(), is(equalTo(Status.ATIVO)));
	}

	@Test
	public void naoDeveAtualizarLivroInexistente()
			throws LivroNaoInformadoException, ValidationLivrariaException, LivroNaoEncontradoException {
		assertThrows(LivroNaoEncontradoException.class, () -> service.update(buildLivroRandom()));
	}

	@Test
	public void naoDeveAtualizarLivroExcluido()
			throws LivroNaoInformadoException, ValidationLivrariaException, LivroNaoEncontradoException {
		LivroDto livro = buildLivroRandom();

		LivroDto created = service.create(livro);

		service.destroy(created.getId());

		assertThrows(LivroNaoEncontradoException.class, () -> service.update(created));
	}

	@Test
	public void deveExcluirLivro()
			throws LivroNaoInformadoException, ValidationLivrariaException, LivroNaoEncontradoException {
		LivroDto livro = buildLivroRandom();

		LivroDto created = service.create(livro);

		service.destroy(created.getId());

		Livro excluido = this.repository.findById(created.getId())
				.orElseThrow(() -> new LivroNaoEncontradoException("Livro não encontrado nos testes"));

		assertThat(excluido, is(notNullValue()));
		assertThat(excluido.getId(), is(equalTo(created.getId())));
		assertThat(excluido.getStatus(), is(equalTo(Status.INATIVO)));
	}

	@Test
	public void naoDeveExcluirLivroInexistente()
			throws LivroNaoInformadoException, ValidationLivrariaException, LivroNaoEncontradoException {
		assertThrows(LivroNaoEncontradoException.class, () -> service.destroy(faker.random().nextLong()));
	}

	@Test
	public void deveEncontrarLivro()
			throws LivroNaoInformadoException, ValidationLivrariaException, LivroNaoEncontradoException {
		LivroDto livro = buildLivroRandom();

		LivroDto created = service.create(livro);

		LivroDto founded = service.get(created.getId());

		assertThat(founded, is(notNullValue()));
		assertThat(founded.getId(), is(equalTo(created.getId())));
		assertThat(founded.getTitulo(), is(equalTo(created.getTitulo())));
		assertThat(founded.getGenero(), is(equalTo(created.getGenero())));
		assertThat(founded.getAutor(), is(equalTo(created.getAutor())));
		assertThat(founded.getSinopse(), is(equalTo(created.getSinopse())));
		assertThat(founded.getStatus(), is(equalTo(Status.ATIVO)));
	}

	@Test
	public void naoDeveEncontrarLivroInexistente() {
		assertThrows(LivroNaoEncontradoException.class, () -> service.get(faker.random().nextLong()));
	}

	@Test
	public void naoDeveEncontrarLivroExcluido()
			throws LivroNaoEncontradoException, LivroNaoInformadoException, ValidationLivrariaException {
		LivroDto livro = buildLivroRandom();

		LivroDto created = service.create(livro);

		service.destroy(created.getId());

		assertThrows(LivroNaoEncontradoException.class, () -> service.get(created.getId()));
	}

	@Test
	public void deveCriarCapaLivro()
			throws LivroNaoInformadoException, ValidationLivrariaException, LivroNaoEncontradoException, IOException,
			CapaNaoEncontradaException, NoSuchAlgorithmException, CapaNaoInformadaException {
		LivroDto livro = buildLivroRandom();

		LivroDto created = service.create(livro);

		byte[] book01 = imageUtils.book01();

		service.saveCapa(created.getId(), book01);

		byte[] capaEncontrada = service.getCapa(created.getId());

		assertThat(Utils.sha1(book01), is(equalTo(Utils.sha1(capaEncontrada))));
	}

	@Test
	public void naoDeveCriarCapaLivroComBytesNulos()
			throws LivroNaoInformadoException, ValidationLivrariaException, LivroNaoEncontradoException, IOException,
			CapaNaoEncontradaException, NoSuchAlgorithmException, CapaNaoInformadaException {
		LivroDto livro = buildLivroRandom();

		LivroDto created = service.create(livro);

		assertThrows(CapaNaoInformadaException.class, () -> service.saveCapa(created.getId(), null));
	}

	@Test
	public void naoDeveCriarCapaLivroComMenosDe10Bytes()
			throws LivroNaoInformadoException, ValidationLivrariaException, LivroNaoEncontradoException, IOException,
			CapaNaoEncontradaException, NoSuchAlgorithmException, CapaNaoInformadaException {
		LivroDto livro = buildLivroRandom();

		LivroDto created = service.create(livro);

		assertThrows(CapaNaoInformadaException.class,
				() -> service.saveCapa(created.getId(), faker.lorem().characters(1, 9).getBytes()));
	}

	@Test
	public void deveExcluirCapaLivro()
			throws LivroNaoInformadoException, ValidationLivrariaException, LivroNaoEncontradoException, IOException,
			CapaNaoEncontradaException, NoSuchAlgorithmException, CapaNaoInformadaException {
		LivroDto livro = buildLivroRandom();

		LivroDto created = service.create(livro);

		byte[] book01 = imageUtils.book01();

		service.saveCapa(created.getId(), book01);

		service.destroyCapa(created.getId());

		assertThrows(CapaNaoEncontradaException.class, () -> service.getCapa(created.getId()));
	}
	
	@Test
	public void naoDeveExcluirCapaNaoExistente()
			throws LivroNaoInformadoException, ValidationLivrariaException, LivroNaoEncontradoException, IOException,
			CapaNaoEncontradaException, NoSuchAlgorithmException, CapaNaoInformadaException {
		LivroDto created = service.create(buildLivroRandom());

		assertThrows(CapaNaoEncontradaException.class, () -> service.destroyCapa(created.getId()));
	}
	
	@Test
	public void naoDeveExcluirCapaCujoLivroNaoExiste()
			throws LivroNaoInformadoException, ValidationLivrariaException, LivroNaoEncontradoException, IOException,
			CapaNaoEncontradaException, NoSuchAlgorithmException, CapaNaoInformadaException {
		assertThrows(LivroNaoEncontradoException.class, () -> service.destroyCapa(faker.random().nextLong()));
	}

	private LivroDto buildLivroRandom() {
		// @formatter:off
		LivroDto livro = LivroDto
			.builder()
				.titulo(faker.book().title())
				.genero(faker.book().genre())
				.autor(faker.book().author())
				.sinopse(faker.lorem().characters(100, 200))
			.build();
		return livro;
	}

	@BeforeEach
	public void prepare() {
		this.dbPrepareUtils.clean();
	}
}