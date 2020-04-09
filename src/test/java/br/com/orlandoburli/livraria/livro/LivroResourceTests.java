package br.com.orlandoburli.livraria.livro;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.service.LivroService;
import br.com.orlandoburli.livraria.utils.DbPrepareUtils;
import br.com.orlandoburli.livraria.utils.ImageUtils;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = LivrariaApplication.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@TestPropertySource(locations = "classpath:application.yml")
public class LivroResourceTests {

	@Autowired
	private DbPrepareUtils dbPrepareUtils;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private LivroService service;

	private final Faker faker = new Faker(new Locale("pt", "BR"));

	@Autowired
	private ImageUtils imageUtils;

	private final ObjectMapper mapper = new ObjectMapper();

	// @formatter:off

	@Test
	public void deveCriarLivro() throws Exception {
		final LivroDto livro = livro();

		mvc.perform(
				post("/livros")
					.content(mapper.writeValueAsBytes(livro))
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id").isNumber())
					.andExpect(jsonPath("$.titulo", is(livro.getTitulo())))
					.andExpect(jsonPath("$.autor", is(livro.getAutor())))
					.andExpect(jsonPath("$.genero", is(livro.getGenero())))
					.andExpect(jsonPath("$.sinopse", is(livro.getSinopse())));
	}

	@Test
	public void naoDeveCriarLivroSemTitulo() throws Exception {
		final LivroDto livro = livro();

		livro.setTitulo(null);

		mvc.perform(
				post("/livros")
					.content(mapper.writeValueAsBytes(livro))
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isUnprocessableEntity())
					.andExpect(jsonPath("$.message", is("Erro ao salvar dados")))
					.andExpect(jsonPath("$.errors.titulo[0]", is("Título é obrigatório")));
	}

	@Test
	public void deveAtualizarLivro() throws Exception {
		final LivroDto livro = service.create(livro());

		livro.setTitulo("Novo Titulo");

		mvc.perform(
				put("/livros")
					.content(mapper.writeValueAsBytes(livro))
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id", is(equalTo(livro.getId().intValue()))))
					.andExpect(jsonPath("$.titulo", is(livro.getTitulo())))
					.andExpect(jsonPath("$.autor", is(livro.getAutor())))
					.andExpect(jsonPath("$.genero", is(livro.getGenero())))
					.andExpect(jsonPath("$.sinopse", is(livro.getSinopse())));
	}

	@Test
	public void deveInativarLivro() throws Exception {
		final LivroDto livro = service.create(livro());

		mvc.perform(
				delete("/livros/" + livro.getId())
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNoContent());
	}

	@Test
	public void deveRetornarLivro() throws Exception {
		final LivroDto livro = service.create(livro());

		mvc.perform(
				get("/livros/" + livro.getId())
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id", is(equalTo(livro.getId().intValue()))))
					.andExpect(jsonPath("$.titulo", is(livro.getTitulo())))
					.andExpect(jsonPath("$.autor", is(livro.getAutor())))
					.andExpect(jsonPath("$.genero", is(livro.getGenero())))
					.andExpect(jsonPath("$.sinopse", is(livro.getSinopse())));
	}

	@Test
	public void deveSalvarCapa() throws Exception {
		final LivroDto livro = service.create(livro());

		final byte[] book01 = imageUtils.book01();

		mvc.perform(
				multipart("/livros/" + livro.getId() + "/capa")
				.file("file", book01)
				.content(book01))
				.andExpect(status().isCreated());
	}

	@Test
	public void deveRetornarCapa() throws Exception {
		final LivroDto livro = service.create(livro());

		final byte[] book01 = imageUtils.book01();

		service.saveCapa(livro.getId(), book01);

		mvc.perform(
				get("/livros/" + livro.getId() + "/capa"))
				.andExpect(status().isOk());
	}

	private LivroDto livro() {
		final LivroDto livro = LivroDto
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
		dbPrepareUtils.clean();
	}
}