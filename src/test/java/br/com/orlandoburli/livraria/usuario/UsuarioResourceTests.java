package br.com.orlandoburli.livraria.usuario;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.service.InstituicaoEnsinoService;
import br.com.orlandoburli.livraria.service.UsuarioService;
import br.com.orlandoburli.livraria.utils.DbPrepareUtils;
import br.com.orlandoburli.livraria.utils.GeraCpfCnpj;
import br.com.orlandoburli.livraria.utils.Utils;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = LivrariaApplication.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@TestPropertySource(locations = "classpath:application.yml")
public class UsuarioResourceTests {

	@Autowired
	private DbPrepareUtils dbPrepareUtils;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private UsuarioService service;

	@Autowired
	private InstituicaoEnsinoService instituicaoEnsinoService;

	private final Faker faker = new Faker(new Locale("pt", "BR"));

	private final ObjectMapper mapper = new ObjectMapper();

	private final GeraCpfCnpj geradorCpfCnpj = new GeraCpfCnpj();

	// @formatter:off

	@Test
	public void deveCriarUsuario() throws Exception {
		final UsuarioDto usuario = usuario();

		mvc.perform(
				post("/usuarios")
					.content(mapper.writeValueAsBytes(usuario))
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id").isNumber())
					.andExpect(jsonPath("$.nome", is(usuario.getNome())))
					.andExpect(jsonPath("$.endereco", is(usuario.getEndereco())))
					.andExpect(jsonPath("$.cpf", is(Utils.numbersOnly(usuario.getCpf()))))
					.andExpect(jsonPath("$.telefone", is(Utils.numbersOnly(usuario.getTelefone()))))
					.andExpect(jsonPath("$.email", is(usuario.getEmail())))
					.andExpect(jsonPath("$.instituicao.id", is(usuario.getInstituicao().getId().intValue())));
	}

	@Test
	public void naoDeveCriarUsuarioSemNome() throws Exception {
		final UsuarioDto usuario = usuario();

		usuario.setNome(null);

		mvc.perform(
				post("/usuarios")
					.content(mapper.writeValueAsBytes(usuario))
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isUnprocessableEntity())
					.andExpect(jsonPath("$.message", is("Erro ao salvar dados")))
					.andExpect(jsonPath("$.errors.nome[0]", is("Nome é obrigatório")));
	}

	@Test
	public void deveAtualizarUsuario() throws Exception {
		final UsuarioDto usuario = service.create(usuario());

		usuario.setNome("Novo nome");

		mvc.perform(
				put("/usuarios")
					.content(mapper.writeValueAsBytes(usuario))
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id", is(equalTo(usuario.getId().intValue()))))
					.andExpect(jsonPath("$.nome", is(usuario.getNome())))
					.andExpect(jsonPath("$.endereco", is(usuario.getEndereco())))
					.andExpect(jsonPath("$.cpf", is(Utils.numbersOnly(usuario.getCpf()))))
					.andExpect(jsonPath("$.telefone", is(Utils.numbersOnly(usuario.getTelefone()))))
					.andExpect(jsonPath("$.email", is(usuario.getEmail())))
					.andExpect(jsonPath("$.instituicao.id", is(usuario.getInstituicao().getId().intValue())));
	}

	@Test
	public void deveInativarLivro() throws Exception {
		final UsuarioDto usuario = service.create(usuario());

		mvc.perform(
				delete("/usuarios/" + usuario.getId())
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNoContent());
	}

	@Test
	public void deveRetornarLivro() throws Exception {
		final UsuarioDto usuario = service.create(usuario());

		mvc.perform(
				get("/usuarios/" + usuario.getId())
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id", is(equalTo(usuario.getId().intValue()))))
					.andExpect(jsonPath("$.nome", is(usuario.getNome())))
					.andExpect(jsonPath("$.endereco", is(usuario.getEndereco())))
					.andExpect(jsonPath("$.cpf", is(Utils.numbersOnly(usuario.getCpf()))))
					.andExpect(jsonPath("$.telefone", is(Utils.numbersOnly(usuario.getTelefone()))))
					.andExpect(jsonPath("$.email", is(usuario.getEmail())))
					.andExpect(jsonPath("$.instituicao.id", is(usuario.getInstituicao().getId().intValue())));
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

		return usuario;
	}

	private InstituicaoEnsinoDto instituicao() throws InstituicaoEnsinoException, ValidationLivrariaException {
		final InstituicaoEnsinoDto instituicaoEnsino = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().cellPhone())
					.endereco(faker.address().fullAddress())
				.build();

		return instituicaoEnsinoService.create(instituicaoEnsino);
	}

	@BeforeEach
	public void prepare() {
		dbPrepareUtils.clean();
	}
}
