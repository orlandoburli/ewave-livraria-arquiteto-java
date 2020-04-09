package br.com.orlandoburli.livraria.instituicaoensino;

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
import br.com.orlandoburli.livraria.service.InstituicaoEnsinoService;
import br.com.orlandoburli.livraria.utils.DbPrepareUtils;
import br.com.orlandoburli.livraria.utils.GeraCpfCnpj;
import br.com.orlandoburli.livraria.utils.Utils;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = LivrariaApplication.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@TestPropertySource(locations = "classpath:application.yml")
public class InstituicaoEnsinoResourceTests {

	@Autowired
	private DbPrepareUtils dbPrepareUtils;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private InstituicaoEnsinoService service;

	private final Faker faker = new Faker(new Locale("pt", "BR"));

	private final ObjectMapper mapper = new ObjectMapper();

	private final GeraCpfCnpj geradorCpfCnpj = new GeraCpfCnpj();

	// @formatter:off

	@Test
	public void deveCriarInstiuicaoEnsino() throws Exception {
		final InstituicaoEnsinoDto instituicao = instituicao();

		mvc.perform(
				post("/instituicoes")
					.content(mapper.writeValueAsBytes(instituicao))
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isCreated())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id").isNumber())
					.andExpect(jsonPath("$.nome", is(instituicao.getNome())))
					.andExpect(jsonPath("$.cnpj", is(Utils.numbersOnly(instituicao.getCnpj()))))
					.andExpect(jsonPath("$.telefone", is(Utils.numbersOnly(instituicao.getTelefone()))))
					.andExpect(jsonPath("$.endereco", is(instituicao.getEndereco())));
	}

	@Test
	public void naoDeveCriarInstituicaoSemNome() throws Exception {
		final InstituicaoEnsinoDto instituicao = instituicao();

		instituicao.setNome(null);

		mvc.perform(
				post("/instituicoes")
					.content(mapper.writeValueAsBytes(instituicao))
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isUnprocessableEntity())
					.andExpect(jsonPath("$.message", is("Erro ao salvar dados")))
					.andExpect(jsonPath("$.errors.nome[0]", is("Nome é obrigatório")));
	}

	@Test
	public void deveAtualizarInstituicaoEnsino() throws Exception {
		final InstituicaoEnsinoDto instituicao = service.create(instituicao());

		instituicao.setNome("Novo Nome");

		mvc.perform(
				put("/instituicoes")
					.content(mapper.writeValueAsBytes(instituicao))
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id", is(equalTo(instituicao.getId().intValue()))))
					.andExpect(jsonPath("$.nome", is(instituicao.getNome())))
					.andExpect(jsonPath("$.cnpj", is(Utils.numbersOnly(instituicao.getCnpj()))))
					.andExpect(jsonPath("$.telefone", is(Utils.numbersOnly(instituicao.getTelefone()))))
					.andExpect(jsonPath("$.endereco", is(instituicao.getEndereco())));
	}

	@Test
	public void deveInativarInstituicao() throws Exception {
		final InstituicaoEnsinoDto instituicao = service.create(instituicao());

		mvc.perform(
				delete("/instituicoes/" + instituicao.getId())
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isNoContent());
	}

	@Test
	public void deveRetornarInstituicao() throws Exception {
		final InstituicaoEnsinoDto instituicao = service.create(instituicao());

		mvc.perform(
				get("/instituicoes/" + instituicao.getId())
					.contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id", is(equalTo(instituicao.getId().intValue()))))
					.andExpect(jsonPath("$.nome", is(instituicao.getNome())))
					.andExpect(jsonPath("$.cnpj", is(Utils.numbersOnly(instituicao.getCnpj()))))
					.andExpect(jsonPath("$.telefone", is(Utils.numbersOnly(instituicao.getTelefone()))))
					.andExpect(jsonPath("$.endereco", is(instituicao.getEndereco())));
	}

	private InstituicaoEnsinoDto instituicao() {
		return InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().phoneNumber())
					.endereco(faker.address().fullAddress())
				.build();
	}

	@BeforeEach
	public void prepare() {
		dbPrepareUtils.clean();
	}
}
