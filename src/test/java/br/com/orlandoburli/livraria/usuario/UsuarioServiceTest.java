package br.com.orlandoburli.livraria.usuario;

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
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.CnpjJaExistenteException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoComUsuariosException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoNaoEncontradaException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoNaoInformadaException;
import br.com.orlandoburli.livraria.exceptions.usuario.CpfJaExistenteException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioPossuiEmprestimosAbertosException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.model.Usuario;
import br.com.orlandoburli.livraria.repository.UsuarioRepository;
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
public class UsuarioServiceTest {

	@Autowired
	private UsuarioService service;

	@Autowired
	private UsuarioRepository repository;

	@Autowired
	private InstituicaoEnsinoService instituicaoEnsinoService;

	@Autowired
	private DbPrepareUtils dbPrepareUtils;

	private final Faker faker = new Faker(new Locale("pt", "BR"));

	private final GeraCpfCnpj geradorCpfCnpj = new GeraCpfCnpj();

	@Test
	public void deveCriarUmUsuario() throws ValidationLivrariaException, InstituicaoEnsinoNaoInformadaException,
			UsuarioNaoInformadoException, CpfJaExistenteException, CnpjJaExistenteException {

		final InstituicaoEnsinoDto instituicao = createInstituicaoEnsino();

		final UsuarioDto usuarioDto = UsuarioDto.builder().nome("Orlando Burli Junior")
				.endereco("Av Mario Augusto Vieira, 269").cpf("702.890.181-53").email("orlando.burli@gmail.com")
				.telefone("(65) 99946-3093").instituicao(instituicao).build();

		final UsuarioDto created = service.create(usuarioDto);

		assertThat(created, is(notNullValue()));
		assertThat(created.getId(), is(greaterThan(0L)));
		assertThat(created.getNome(), is(equalTo(usuarioDto.getNome())));
		assertThat(created.getEndereco(), is(equalTo(usuarioDto.getEndereco())));
		assertThat(created.getCpf(), is(equalTo(Utils.numbersOnly(usuarioDto.getCpf()))));
		assertThat(created.getTelefone(), is(equalTo(Utils.numbersOnly(usuarioDto.getTelefone()))));
		assertThat(created.getInstituicao(), is(notNullValue()));
		assertThat(created.getInstituicao().getId(), is(equalTo(instituicao.getId())));
	}

	@Test
	public void deveCriarUmUsuarioComEmailSemTelefone()
			throws ValidationLivrariaException, InstituicaoEnsinoNaoInformadaException, UsuarioNaoInformadoException,
			CpfJaExistenteException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).instituicao(createInstituicaoEnsino()).build();

		final UsuarioDto created = service.create(usuarioDto);

		assertThat(created, is(notNullValue()));
		assertThat(created.getId(), is(greaterThan(0L)));
		assertThat(created.getNome(), is(equalTo(usuarioDto.getNome())));
		assertThat(created.getEndereco(), is(equalTo(usuarioDto.getEndereco())));
		assertThat(created.getCpf(), is(equalTo(Utils.numbersOnly(usuarioDto.getCpf()))));
		assertThat(created.getTelefone(), is(equalTo(Utils.numbersOnly(usuarioDto.getTelefone()))));
		assertThat(created.getInstituicao(), is(notNullValue()));
		assertThat(created.getInstituicao().getId(), is(equalTo(usuarioDto.getInstituicao().getId())));
	}

	@Test
	public void deveCriarUmUsuarioSemEmailComTelefone()
			throws ValidationLivrariaException, InstituicaoEnsinoNaoInformadaException, UsuarioNaoInformadoException,
			CpfJaExistenteException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.telefone(faker.phoneNumber().cellPhone()).instituicao(createInstituicaoEnsino()).build();

		final UsuarioDto created = service.create(usuarioDto);

		assertThat(created, is(notNullValue()));
		assertThat(created.getId(), is(greaterThan(0L)));
		assertThat(created.getNome(), is(equalTo(usuarioDto.getNome())));
		assertThat(created.getEndereco(), is(equalTo(usuarioDto.getEndereco())));
		assertThat(created.getCpf(), is(equalTo(Utils.numbersOnly(usuarioDto.getCpf()))));
		assertThat(created.getTelefone(), is(equalTo(Utils.numbersOnly(usuarioDto.getTelefone()))));
		assertThat(created.getInstituicao(), is(notNullValue()));
		assertThat(created.getInstituicao().getId(), is(equalTo(usuarioDto.getInstituicao().getId())));
	}

	@Test
	public void naoDeveCriarUmUsuarioNulo() {
		assertThrows(UsuarioNaoInformadoException.class, () -> service.create(null));
	}

	@Test
	public void naoDeveCriarUmUsuarioComNomeNulo()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(null).endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf()).email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone()).instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComNomeVazio()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome("    ").endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf()).email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone()).instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComNomeDeMenosDe3Caracteres()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.lorem().characters(1, 2))
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComNomeDeMaisDe100Caracteres()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.lorem().characters(101, 150))
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComEnderecoNulo()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName()).endereco(null)
				.cpf(geradorCpfCnpj.cpf()).email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone()).instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComEnderecoVazio()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName()).endereco("     ")
				.cpf(geradorCpfCnpj.cpf()).email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone()).instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComEnderecoDeMenosDe3Caracteres()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.lorem().characters(1, 2)).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComEnderecoDeMaisDe100Caracteres()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.lorem().characters(101, 150)).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComCpfInvalido()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(Integer.toString(faker.random().nextInt(11)))
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("cpf"));

		assertThat(exception.getErrors().get("cpf"), hasItem("CPF inválido"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComCpfNulo()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(null).email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone()).instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("cpf"));

		assertThat(exception.getErrors().get("cpf"), hasItem("CPF é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComCpfVazio()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf("     ").email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone()).instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("cpf"));

		assertThat(exception.getErrors().get("cpf"), hasItem("CPF é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComTelefoneComMenosDe10Caracteres()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(Integer.toString(faker.random().nextInt(1, 9)))
				.instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("telefone"));

		assertThat(exception.getErrors().get("telefone"), hasItem("Telefone inválido"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComTelefoneComMaisDe11Caracteres()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(Integer.toString(faker.random().nextInt(11, 20)))
				.instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("telefone"));

		assertThat(exception.getErrors().get("telefone"), hasItem("Telefone inválido"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComEmailInvalido()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.lorem().characters(10, 20)).telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("email"));

		assertThat(exception.getErrors().get("email"), hasItem("Email inválido"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComEmailComMaisde200Caracteres()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.lorem().characters(201, 250) + "@gmail.com").telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("email"));

		assertThat(exception.getErrors().get("email"), hasItem("Email não pode ter mais de 200 caracteres"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComInstituicaoNula()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone()).instituicao(null)
				.build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("instituicao"));

		assertThat(exception.getErrors().get("instituicao"), hasItem("Instituição é obrigatória"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComInstituicaoInexistente()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		final InstituicaoEnsinoDto instituicaoFake = InstituicaoEnsinoDto.builder().id(faker.random().nextLong(10))
				.build();

		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(instituicaoFake).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("instituicao"));

		assertThat(exception.getErrors().get("instituicao"), hasItem("Instituição é obrigatória"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComInstituicaoInexistenteComIdNulo()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		final InstituicaoEnsinoDto instituicaoFake = InstituicaoEnsinoDto.builder().build();

		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(instituicaoFake).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("instituicao"));

		assertThat(exception.getErrors().get("instituicao"), hasItem("Instituição é obrigatória"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComInstituicaoInativa()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException,
			InstituicaoEnsinoNaoEncontradaException, InstituicaoEnsinoComUsuariosException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsinoInativa()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		assertTrue(exception.getErrors().containsKey("instituicao"));

		assertThat(exception.getErrors().get("instituicao"), hasItem("Instituição é obrigatória"));
	}

	@Test
	public void naoDeveCriarUmUsuarioSemEmailOuTelefone()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException,
			InstituicaoEnsinoNaoEncontradaException, InstituicaoEnsinoComUsuariosException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.instituicao(createInstituicaoEnsino()).build();

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuarioDto));

		System.out.println(exception.getErrors());

		assertTrue(exception.getErrors().containsKey("entity"));

		assertThat(exception.getErrors().get("entity"), hasItem("Usuário precisa ter email ou telefone preenchidos"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComCpfRepetido() throws InstituicaoEnsinoNaoInformadaException,
			ValidationLivrariaException, InstituicaoEnsinoNaoEncontradaException, InstituicaoEnsinoComUsuariosException,
			CnpjJaExistenteException, UsuarioNaoInformadoException, CpfJaExistenteException {

		final String cpf = geradorCpfCnpj.cpf();

		// @formatter:off
		final UsuarioDto usuario1 = UsuarioDto
				.builder()
					.nome(faker.name().fullName())
					.endereco(faker.address().fullAddress())
					.cpf(cpf)
					.email(faker.internet().emailAddress())
					.instituicao(createInstituicaoEnsino())
				.build();

		final UsuarioDto usuario2 = UsuarioDto
				.builder()
					.nome(faker.name().fullName())
					.endereco(faker.address().fullAddress())
					.cpf(cpf)
					.email(faker.internet().emailAddress())
					.instituicao(createInstituicaoEnsino())
				.build();
		// @formatter:on

		service.create(usuario1);

		assertThrows(CpfJaExistenteException.class, () -> service.create(usuario2));
	}

	@Test
	public void deveAtualizarUsuario()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, UsuarioNaoInformadoException,
			UsuarioNaoEncontradoException, CpfJaExistenteException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino()).build();

		final UsuarioDto created = service.create(usuarioDto);

		created.setNome("Novo nome");

		final UsuarioDto updated = service.update(created);

		assertThat(updated, is(notNullValue()));
		assertThat(updated.getId(), is(equalTo(created.getId())));
		assertThat(updated.getNome(), is(equalTo(created.getNome())));
	}

	@Test
	public void naoDeveAtualizarUsuarioInexistente()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().id(faker.random().nextLong(100))
				.nome(faker.name().fullName()).endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino()).build();

		assertThrows(UsuarioNaoEncontradoException.class, () -> service.update(usuarioDto));
	}

	@Test
	public void naoDeveAtualizarUmUsuarioNulo() {
		assertThrows(UsuarioNaoInformadoException.class, () -> service.create(null));
	}

	@Test
	public void naoDeveAtualizarUsuarioExcluido() throws InstituicaoEnsinoNaoInformadaException,
			ValidationLivrariaException, UsuarioNaoInformadoException, UsuarioNaoEncontradoException,
			CpfJaExistenteException, UsuarioPossuiEmprestimosAbertosException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().id(faker.random().nextLong(100))
				.nome(faker.name().fullName()).endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino()).build();

		final UsuarioDto created = service.create(usuarioDto);

		service.destroy(created.getId());

		assertThrows(UsuarioNaoEncontradoException.class, () -> service.update(created));
	}

	@Test
	public void naoDeveAtualizarUmUsuarioComCpfRepetido() throws InstituicaoEnsinoNaoInformadaException,
			ValidationLivrariaException, InstituicaoEnsinoNaoEncontradaException, InstituicaoEnsinoComUsuariosException,
			CnpjJaExistenteException, UsuarioNaoInformadoException, CpfJaExistenteException {

		// @formatter:off
		final UsuarioDto usuario1 = UsuarioDto
				.builder()
					.nome(faker.name().fullName())
					.endereco(faker.address().fullAddress())
					.cpf(geradorCpfCnpj.cpf())
					.email(faker.internet().emailAddress())
					.instituicao(createInstituicaoEnsino())
				.build();

		final UsuarioDto usuario2 = UsuarioDto
				.builder()
					.nome(faker.name().fullName())
					.endereco(faker.address().fullAddress())
					.cpf(geradorCpfCnpj.cpf())
					.email(faker.internet().emailAddress())
					.instituicao(createInstituicaoEnsino())
				.build();
		// @formatter:on

		final UsuarioDto created1 = service.create(usuario1);
		final UsuarioDto created2 = service.create(usuario2);

		created2.setCpf(created1.getCpf());

		assertThrows(CpfJaExistenteException.class, () -> service.update(created2));
	}

	@Test
	public void deveInativarUsuario() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException,
			UsuarioNaoInformadoException, UsuarioNaoEncontradoException, CpfJaExistenteException,
			UsuarioPossuiEmprestimosAbertosException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().id(faker.random().nextLong(100))
				.nome(faker.name().fullName()).endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino()).build();

		final UsuarioDto created = service.create(usuarioDto);

		service.destroy(created.getId());

		final Usuario entity = repository.findById(created.getId())
				.orElseThrow(() -> new UsuarioNaoEncontradoException("Erro, usuário não encontrado!"));

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getId(), is(equalTo(created.getId())));
		assertThat(entity.getStatus(), is(equalTo(Status.INATIVO)));
	}

	@Test
	public void naoDeveInativarUsuarioInexistente() {
		assertThrows(UsuarioNaoEncontradoException.class, () -> service.destroy(faker.random().nextLong()));
	}

	@Test
	public void deveEncontrarUsuario()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, UsuarioNaoInformadoException,
			UsuarioNaoEncontradoException, CpfJaExistenteException, CnpjJaExistenteException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().id(faker.random().nextLong(100))
				.nome(faker.name().fullName()).endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino()).build();

		final UsuarioDto created = service.create(usuarioDto);

		final UsuarioDto founded = service.get(created.getId());

		assertThat(founded, is(notNullValue()));
		assertThat(founded.getId(), is(equalTo(created.getId())));
		assertThat(founded.getStatus(), is(equalTo(Status.ATIVO)));
	}

	@Test
	public void naoDeveEncontrarUsuario() {
		assertThrows(UsuarioNaoEncontradoException.class, () -> service.get(faker.random().nextLong()));
	}

	private InstituicaoEnsinoDto createInstituicaoEnsino()
			throws ValidationLivrariaException, InstituicaoEnsinoNaoInformadaException, CnpjJaExistenteException {
		// @formatter:off
		final InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().phoneNumber())
					.endereco(faker.address().fullAddress())
				.build();

		// @formatter:on
		return instituicaoEnsinoService.create(instituicaoEnsinoDto);
	}

	private InstituicaoEnsinoDto createInstituicaoEnsinoInativa()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException,
			InstituicaoEnsinoNaoEncontradaException, InstituicaoEnsinoComUsuariosException, CnpjJaExistenteException {
		final InstituicaoEnsinoDto instituicaoEnsino = createInstituicaoEnsino();

		instituicaoEnsinoService.destroy(instituicaoEnsino.getId());

		return instituicaoEnsino;
	}

	@BeforeEach
	public void prepare() {
		dbPrepareUtils.clean();
	}
}
