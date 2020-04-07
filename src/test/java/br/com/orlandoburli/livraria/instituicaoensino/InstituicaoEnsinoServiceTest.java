package br.com.orlandoburli.livraria.instituicaoensino;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.exceptions.LivrariaException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.CnpjJaExistenteException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoComUsuariosException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoNaoEncontradaException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoNaoInformadaException;
import br.com.orlandoburli.livraria.exceptions.usuario.CpfJaExistenteException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.model.InstituicaoEnsino;
import br.com.orlandoburli.livraria.repository.InstituicaoEnsinoRepository;
import br.com.orlandoburli.livraria.service.InstituicaoEnsinoService;
import br.com.orlandoburli.livraria.service.UsuarioService;
import br.com.orlandoburli.livraria.utils.DbPrepareUtils;
import br.com.orlandoburli.livraria.utils.GeraCpfCnpj;
import br.com.orlandoburli.livraria.utils.Utils;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@TestPropertySource(locations = "classpath:application.yml")
@Transactional
public class InstituicaoEnsinoServiceTest {

	@Autowired
	private InstituicaoEnsinoService service;

	@Autowired
	private InstituicaoEnsinoRepository repository;

	@Autowired
	private DbPrepareUtils dbPrepareUtils;

	@Autowired
	private UsuarioService usuarioService;

	private final Faker faker = new Faker(new Locale("pt", "BR"));

	private final GeraCpfCnpj geradorCpfCnpj = new GeraCpfCnpj();

	@Test
	public void deveCriarUmaEntidade() throws LivrariaException {

		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome("Faculdade das Américas")
				.cnpj("23.519.978/0001-60").telefone("(65) 2333-2344").endereco("Av das Torres, 2344").build();

		final InstituicaoEnsinoDto created = service.create(instituicaoEnsinoDto);

		assertThat(created, is(notNullValue()));
		assertThat(created.getId(), is(notNullValue()));
		assertThat(created.getId(), is(greaterThan(0L)));

		assertThat(created.getNome(), is(equalTo(instituicaoEnsinoDto.getNome())));
		assertThat(created.getCnpj(), is(equalTo(Utils.numbersOnly(instituicaoEnsinoDto.getCnpj()))));
		assertThat(created.getTelefone(), is(equalTo(Utils.numbersOnly(instituicaoEnsinoDto.getTelefone()))));
		assertThat(created.getEndereco(), is(equalTo(instituicaoEnsinoDto.getEndereco())));
		assertThat(created.getStatus(), is(equalTo(Status.ATIVO)));
	}

	@Test
	public void naoDeveCriarEntidadeComNomeNulo() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome(null)
				.cnpj("23.519.978/0001-60").telefone("(65) 2333-2344").endereco("Av das Torres, 2344").build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome é obrigatório"));
	}

	@Test
	public void naoDeveCriarEntidadeComNomeVazio() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome("        ")
				.cnpj(geradorCpfCnpj.cnpj()).telefone(faker.phoneNumber().phoneNumber())
				.endereco(faker.address().fullAddress()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome é obrigatório"));
	}

	@Test
	public void naoDeveCriarEntidadeComNomeDeMaisDe100Caracteres() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder()
				.nome(faker.lorem().characters(101, 150, true, true)).cnpj(geradorCpfCnpj.cnpj())
				.telefone(faker.phoneNumber().phoneNumber()).endereco(faker.address().fullAddress()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarEntidadeComMenosDe3Caracteres() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder()
				.nome(faker.lorem().characters(1, 2)).cnpj(geradorCpfCnpj.cnpj())
				.telefone(faker.phoneNumber().phoneNumber()).endereco(faker.address().fullAddress()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarEntidadeComEnderecoNulo() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome(faker.company().name())
				.cnpj(geradorCpfCnpj.cnpj()).telefone(faker.phoneNumber().cellPhone()).endereco(null).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço é obrigatório"));
	}

	@Test
	public void naoDeveCriarEntidadeComEnderecoVazio() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome(faker.company().name())
				.cnpj(geradorCpfCnpj.cnpj()).telefone(faker.phoneNumber().cellPhone()).endereco("   ").build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço é obrigatório"));
	}

	@Test
	public void naoDeveCriarEntidadeComEnderecoComMaisDe100Caracteres() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome(faker.company().name())
				.cnpj(geradorCpfCnpj.cnpj()).telefone(faker.phoneNumber().cellPhone())
				.endereco(faker.lorem().characters(101, 150)).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarEntidadeComEnderecoComMenosDe3Caracteres() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome(faker.company().name())
				.cnpj(geradorCpfCnpj.cnpj()).telefone(faker.phoneNumber().cellPhone())
				.endereco(faker.lorem().characters(1, 2)).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarEntidadeComCnpjNulo() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome(faker.company().name())
				.cnpj(null).telefone(faker.phoneNumber().cellPhone()).endereco(faker.address().fullAddress()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("cnpj"));

		assertThat(exception.getErrors().get("cnpj"), hasItem("Cnpj é obrigatório"));
	}

	@Test
	public void naoDeveCriarEntidadeComCnpjVazio() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome(faker.company().name())
				.cnpj("    ").telefone(faker.phoneNumber().cellPhone()).endereco(faker.address().fullAddress()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("cnpj"));

		assertThat(exception.getErrors().get("cnpj"), hasItem("Cnpj é obrigatório"));
	}

	@Test
	public void naoDeveCriarEntidadeComCnpjInvalido() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome(faker.company().name())
				.cnpj(faker.random().nextInt(14, 14).toString()).telefone(faker.phoneNumber().cellPhone())
				.endereco(faker.address().fullAddress()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("cnpj"));

		assertThat(exception.getErrors().get("cnpj"), hasItem("Cnpj inválido"));
	}

	@Test
	public void naoDeveCriarEntidadeComTelefoneComMenosDe10Caracteres() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome(faker.company().name())
				.cnpj(geradorCpfCnpj.cnpj()).telefone(faker.random().nextInt(1, 9).toString())
				.endereco(faker.address().fullAddress()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("telefone"));

		assertThat(exception.getErrors().get("telefone"), hasItem("Telefone inválido"));
	}

	@Test
	public void naoDeveCriarEntidadeComTelefoneComMaisDe11Caracteres() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome(faker.company().name())
				.cnpj(geradorCpfCnpj.cnpj()).telefone(faker.random().nextInt(12, 30).toString())
				.endereco(faker.address().fullAddress()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(instituicaoEnsinoDto));

		assertTrue(exception.getErrors().containsKey("telefone"));

		assertThat(exception.getErrors().get("telefone"), hasItem("Telefone inválido"));
	}

	@Test
	public void naoDeveCriarEntidadePassandoNulo() throws ValidationLivrariaException {
		assertThrows(InstituicaoEnsinoNaoInformadaException.class, () -> service.create(null));
	}

	@Test
	public void naoDeveCriarEntidadeComCnpjRepetido()
			throws InstituicaoEnsinoNaoInformadaException, CnpjJaExistenteException, ValidationLivrariaException {
		final String cnpj = geradorCpfCnpj.cnpj();

		// @formatter:off
		final InstituicaoEnsinoDto instituicao1 = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(cnpj)
					.telefone(faker.phoneNumber().phoneNumber())
					.endereco(faker.address().fullAddress())
				.build();

		final InstituicaoEnsinoDto instituicao2 = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(cnpj)
					.telefone(faker.phoneNumber().phoneNumber())
					.endereco(faker.address().fullAddress())
				.build();

		// @formatter:on

		service.create(instituicao1);

		assertThrows(CnpjJaExistenteException.class, () -> service.create(instituicao2));
	}

	@Test
	public void deveAtualizarInstituicao() throws ValidationLivrariaException, InstituicaoEnsinoNaoEncontradaException,
			InstituicaoEnsinoNaoInformadaException, CnpjJaExistenteException {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome(faker.company().name())
				.cnpj(geradorCpfCnpj.cnpj()).telefone(faker.phoneNumber().cellPhone())
				.endereco(faker.address().fullAddress()).build();

		final InstituicaoEnsinoDto created = service.create(instituicaoEnsinoDto);

		created.setNome("Novo nome da instituição");

		final InstituicaoEnsinoDto updated = service.update(created);

		assertThat(updated.getId(), is(equalTo(created.getId())));
		assertThat(updated.getNome(), is(equalTo(created.getNome())));
	}

	@Test
	public void naoDeveAtualizarEntidadeInexistente() {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder()
				.id(faker.number().randomNumber()).nome(faker.company().name()).cnpj(geradorCpfCnpj.cnpj())
				.telefone(faker.phoneNumber().cellPhone()).endereco(faker.address().fullAddress()).build();

		assertThrows(InstituicaoEnsinoNaoEncontradaException.class, () -> service.update(instituicaoEnsinoDto));
	}

	@Test
	public void naoDeveAtualizarEntidadeComCnpjRepetido()
			throws InstituicaoEnsinoNaoInformadaException, CnpjJaExistenteException, ValidationLivrariaException {

		// @formatter:off
		final InstituicaoEnsinoDto instituicao1 = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().phoneNumber())
					.endereco(faker.address().fullAddress())
				.build();

		final InstituicaoEnsinoDto instituicao2 = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().phoneNumber())
					.endereco(faker.address().fullAddress())
				.build();

		// @formatter:on

		final InstituicaoEnsinoDto created1 = service.create(instituicao1);
		final InstituicaoEnsinoDto created2 = service.create(instituicao2);

		created2.setCnpj(created1.getCnpj());

		assertThrows(CnpjJaExistenteException.class, () -> service.update(created2));
	}

	@Test
	public void deveInativarInstituicaoEnsino()
			throws ValidationLivrariaException, InstituicaoEnsinoNaoEncontradaException,
			InstituicaoEnsinoComUsuariosException, InstituicaoEnsinoNaoInformadaException, CnpjJaExistenteException {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder()
				.id(faker.number().randomNumber()).nome(faker.company().name()).cnpj(geradorCpfCnpj.cnpj())
				.telefone(faker.phoneNumber().cellPhone()).endereco(faker.address().fullAddress()).build();

		final InstituicaoEnsinoDto created = service.create(instituicaoEnsinoDto);

		service.destroy(created.getId());

		assertThrows(InstituicaoEnsinoNaoEncontradaException.class, () -> service.get(created.getId()));

		final InstituicaoEnsino entityDestroyed = repository.findById(created.getId())
				.orElseThrow(() -> new InstituicaoEnsinoNaoEncontradaException(""));

		assertThat(entityDestroyed, is(notNullValue()));
		assertThat(entityDestroyed.getId(), is(equalTo(created.getId())));
		assertThat(entityDestroyed.getStatus(), is(equalTo(Status.INATIVO)));
	}

	@Test
	public void naoDeveInativarInstituicaoEnsinoInexistente() {
		assertThrows(InstituicaoEnsinoNaoEncontradaException.class, () -> service.destroy(faker.random().nextLong()));
	}

	@Test
	public void naoDeveInativarInstituicaoEnsinoComUsuarios()
			throws ValidationLivrariaException, InstituicaoEnsinoNaoInformadaException, UsuarioNaoInformadoException,
			CpfJaExistenteException, CnpjJaExistenteException {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder()
				.id(faker.number().randomNumber()).nome(faker.company().name()).cnpj(geradorCpfCnpj.cnpj())
				.telefone(faker.phoneNumber().cellPhone()).endereco(faker.address().fullAddress()).build();

		final InstituicaoEnsinoDto instituicaoCriada = service.create(instituicaoEnsinoDto);

		UsuarioDto usuario = UsuarioDto.builder().nome(faker.name().fullName()).endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf()).telefone(faker.phoneNumber().cellPhone())
				.email(faker.internet().emailAddress()).instituicao(instituicaoCriada).build();

		usuario = usuarioService.create(usuario);

		assertThrows(InstituicaoEnsinoComUsuariosException.class, () -> service.destroy(instituicaoCriada.getId()));
	}

	@Test
	public void naoDeveAtualizarEntidadePassandoNulo() throws ValidationLivrariaException {
		assertThrows(InstituicaoEnsinoNaoInformadaException.class, () -> service.update(null));
	}

	@Test
	public void deveRetornarInstituicaoEnsino() throws ValidationLivrariaException,
			InstituicaoEnsinoNaoEncontradaException, InstituicaoEnsinoNaoInformadaException, CnpjJaExistenteException {
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto.builder().nome(faker.company().name())
				.cnpj(geradorCpfCnpj.cnpj()).telefone(faker.phoneNumber().cellPhone())
				.endereco(faker.address().fullAddress()).build();

		final InstituicaoEnsinoDto created = service.create(instituicaoEnsinoDto);

		final InstituicaoEnsinoDto founded = service.get(created.getId());

		assertThat(founded, is(notNullValue()));
		assertThat(founded.getId(), is(equalTo(created.getId())));
		assertThat(founded.getStatus(), is(equalTo(Status.ATIVO)));
	}

	@Test
	public void naoDeveEncontrarInstituicaoEnsino() {
		assertThrows(InstituicaoEnsinoNaoEncontradaException.class, () -> service.get(faker.random().nextLong()));
	}

	@BeforeEach
	public void prepare() {
		dbPrepareUtils.clean();
	}
}