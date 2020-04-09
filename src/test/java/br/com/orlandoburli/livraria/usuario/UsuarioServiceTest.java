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

import br.com.orlandoburli.livraria.dto.EmprestimoDto;
import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.exceptions.LivrariaException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.CnpjJaExistenteException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoComUsuariosException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoNaoEncontradaException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoNaoInformadaException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.usuario.CpfJaExistenteException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoInformadoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioPossuiEmprestimosAbertosException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.model.Usuario;
import br.com.orlandoburli.livraria.repository.UsuarioRepository;
import br.com.orlandoburli.livraria.service.EmprestimoService;
import br.com.orlandoburli.livraria.service.InstituicaoEnsinoService;
import br.com.orlandoburli.livraria.service.LivroService;
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
	private LivroService livroService;

	@Autowired
	private EmprestimoService emprestimoService;

	@Autowired
	private DbPrepareUtils dbPrepareUtils;

	private final Faker faker = new Faker(new Locale("pt", "BR"));

	private final GeraCpfCnpj geradorCpfCnpj = new GeraCpfCnpj();

	@Test
	public void deveCriarUmUsuario() throws LivrariaException {

		final InstituicaoEnsinoDto instituicao = instituicao();

		// @formatter:off
		final UsuarioDto usuario = UsuarioDto
				.builder()
					.nome(faker.name().fullName())
					.endereco(faker.address().fullAddress())
					.cpf(geradorCpfCnpj.cpf())
					.email(faker.internet().emailAddress())
					.instituicao(instituicao)
				.build();
		// @formatter:on

		final UsuarioDto created = service.create(usuario);

		assertThat(created, is(notNullValue()));
		assertThat(created.getId(), is(greaterThan(0L)));
		assertThat(created.getNome(), is(equalTo(usuario.getNome())));
		assertThat(created.getEndereco(), is(equalTo(usuario.getEndereco())));
		assertThat(created.getCpf(), is(equalTo(Utils.numbersOnly(usuario.getCpf()))));
		assertThat(created.getTelefone(), is(equalTo(Utils.numbersOnly(usuario.getTelefone()))));
		assertThat(created.getInstituicao(), is(notNullValue()));
		assertThat(created.getInstituicao().getId(), is(equalTo(instituicao.getId())));
	}

	@Test
	public void deveCriarUmUsuarioComEmailSemTelefone() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setTelefone(null);

		final UsuarioDto created = service.create(usuario);

		assertThat(created, is(notNullValue()));
		assertThat(created.getId(), is(greaterThan(0L)));
		assertThat(created.getNome(), is(equalTo(usuario.getNome())));
		assertThat(created.getEndereco(), is(equalTo(usuario.getEndereco())));
		assertThat(created.getCpf(), is(equalTo(Utils.numbersOnly(usuario.getCpf()))));
		assertThat(created.getTelefone(), is(equalTo(Utils.numbersOnly(usuario.getTelefone()))));
		assertThat(created.getInstituicao(), is(notNullValue()));
	}

	@Test
	public void deveCriarUmUsuarioSemEmailComTelefone() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setEmail(null);

		final UsuarioDto created = service.create(usuario);

		assertThat(created, is(notNullValue()));
		assertThat(created.getId(), is(greaterThan(0L)));
		assertThat(created.getNome(), is(equalTo(usuario.getNome())));
		assertThat(created.getEndereco(), is(equalTo(usuario.getEndereco())));
		assertThat(created.getCpf(), is(equalTo(Utils.numbersOnly(usuario.getCpf()))));
		assertThat(created.getTelefone(), is(equalTo(Utils.numbersOnly(usuario.getTelefone()))));
		assertThat(created.getInstituicao(), is(notNullValue()));
		assertThat(created.getInstituicao().getId(), is(equalTo(usuario.getInstituicao().getId())));
	}

	@Test
	public void naoDeveCriarUmUsuarioNulo() {
		assertThrows(UsuarioNaoInformadoException.class, () -> service.create(null));
	}

	@Test
	public void naoDeveCriarUmUsuarioComNomeNulo() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setNome(null);

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComNomeVazio() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setNome("   ");

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComNomeDeMenosDe3Caracteres() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setNome(faker.lorem().characters(1, 2));

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComNomeDeMaisDe100Caracteres() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setNome(faker.lorem().characters(101, 150));

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComEnderecoNulo() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setEndereco(null);

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComEnderecoVazio() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setEndereco("   ");

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComEnderecoDeMenosDe3Caracteres() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setEndereco(faker.lorem().characters(1, 2));

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComEnderecoDeMaisDe100Caracteres() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setEndereco(faker.lorem().characters(101, 150));

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço deve ter entre 3 e 100 caracteres"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComCpfInvalido() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setCpf(Integer.toString(faker.random().nextInt(11)));

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("cpf"));

		assertThat(exception.getErrors().get("cpf"), hasItem("CPF inválido"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComCpfNulo() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setCpf(null);

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("cpf"));

		assertThat(exception.getErrors().get("cpf"), hasItem("CPF é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComCpfVazio() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setCpf("   ");

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("cpf"));

		assertThat(exception.getErrors().get("cpf"), hasItem("CPF é obrigatório"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComTelefoneComMenosDe10Caracteres() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setTelefone(Integer.toString(faker.random().nextInt(1, 9)));

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("telefone"));

		assertThat(exception.getErrors().get("telefone"), hasItem("Telefone inválido"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComTelefoneComMaisDe11Caracteres() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setTelefone(Integer.toString(faker.random().nextInt(11, 20)));

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("telefone"));

		assertThat(exception.getErrors().get("telefone"), hasItem("Telefone inválido"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComEmailInvalido() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setEmail(faker.lorem().characters(10, 20));

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("email"));

		assertThat(exception.getErrors().get("email"), hasItem("Email inválido"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComEmailComMaisde200Caracteres() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setEmail(faker.lorem().characters(201, 250) + "@gmail.com");

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("email"));

		assertThat(exception.getErrors().get("email"), hasItem("Email não pode ter mais de 200 caracteres"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComInstituicaoNula() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setInstituicao(null);

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("instituicao"));

		assertThat(exception.getErrors().get("instituicao"), hasItem("Instituição é obrigatória"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComInstituicaoInexistente() throws LivrariaException {
		final InstituicaoEnsinoDto instituicaoFake = InstituicaoEnsinoDto.builder().id(faker.random().nextLong(10))
				.build();

		final UsuarioDto usuario = usuario();

		usuario.setInstituicao(instituicaoFake);

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("instituicao"));

		assertThat(exception.getErrors().get("instituicao"), hasItem("Instituição é obrigatória"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComInstituicaoInexistenteComIdNulo() throws LivrariaException {
		final InstituicaoEnsinoDto instituicaoFake = InstituicaoEnsinoDto.builder().build();

		final UsuarioDto usuario = usuario();

		usuario.setInstituicao(instituicaoFake);

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("instituicao"));

		assertThat(exception.getErrors().get("instituicao"), hasItem("Instituição é obrigatória"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComInstituicaoInativa() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setInstituicao(instituicaoEnsinoInativa());

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		assertTrue(exception.getErrors().containsKey("instituicao"));

		assertThat(exception.getErrors().get("instituicao"), hasItem("Instituição é obrigatória"));
	}

	@Test
	public void naoDeveCriarUmUsuarioSemEmailOuTelefone() throws LivrariaException {
		final UsuarioDto usuario = usuario();

		usuario.setTelefone(null);
		usuario.setEmail(null);

		final ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class,
				() -> service.create(usuario));

		System.out.println(exception.getErrors());

		assertTrue(exception.getErrors().containsKey("entity"));

		assertThat(exception.getErrors().get("entity"), hasItem("Usuário precisa ter email ou telefone preenchidos"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComCpfRepetido() throws LivrariaException {

		final String cpf = geradorCpfCnpj.cpf();

		// @formatter:off
		final UsuarioDto usuario1 = UsuarioDto
				.builder()
					.nome(faker.name().fullName())
					.endereco(faker.address().fullAddress())
					.cpf(cpf)
					.email(faker.internet().emailAddress())
					.instituicao(instituicao())
				.build();

		final UsuarioDto usuario2 = UsuarioDto
				.builder()
					.nome(faker.name().fullName())
					.endereco(faker.address().fullAddress())
					.cpf(cpf)
					.email(faker.internet().emailAddress())
					.instituicao(instituicao())
				.build();
		// @formatter:on

		service.create(usuario1);

		assertThrows(CpfJaExistenteException.class, () -> service.create(usuario2));
	}

	@Test
	public void deveAtualizarUsuario() throws LivrariaException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().nome(faker.name().fullName())
				.endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(instituicao()).build();

		final UsuarioDto created = service.create(usuarioDto);

		created.setNome("Novo nome");

		final UsuarioDto updated = service.update(created);

		assertThat(updated, is(notNullValue()));
		assertThat(updated.getId(), is(equalTo(created.getId())));
		assertThat(updated.getNome(), is(equalTo(created.getNome())));
	}

	@Test
	public void naoDeveAtualizarUsuarioInexistente() throws LivrariaException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().id(faker.random().nextLong(100))
				.nome(faker.name().fullName()).endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(instituicao()).build();

		assertThrows(UsuarioNaoEncontradoException.class, () -> service.update(usuarioDto));
	}

	@Test
	public void naoDeveAtualizarUmUsuarioNulo() {
		assertThrows(UsuarioNaoInformadoException.class, () -> service.create(null));
	}

	@Test
	public void naoDeveAtualizarUsuarioExcluido() throws LivrariaException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().id(faker.random().nextLong(100))
				.nome(faker.name().fullName()).endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(instituicao()).build();

		final UsuarioDto created = service.create(usuarioDto);

		service.destroy(created.getId());

		assertThrows(UsuarioNaoEncontradoException.class, () -> service.update(created));
	}

	@Test
	public void naoDeveAtualizarUmUsuarioComCpfRepetido() throws LivrariaException {

		// @formatter:off
		final UsuarioDto usuario1 = UsuarioDto
				.builder()
					.nome(faker.name().fullName())
					.endereco(faker.address().fullAddress())
					.cpf(geradorCpfCnpj.cpf())
					.email(faker.internet().emailAddress())
					.instituicao(instituicao())
				.build();

		final UsuarioDto usuario2 = UsuarioDto
				.builder()
					.nome(faker.name().fullName())
					.endereco(faker.address().fullAddress())
					.cpf(geradorCpfCnpj.cpf())
					.email(faker.internet().emailAddress())
					.instituicao(instituicao())
				.build();
		// @formatter:on

		final UsuarioDto created1 = service.create(usuario1);
		final UsuarioDto created2 = service.create(usuario2);

		created2.setCpf(created1.getCpf());

		assertThrows(CpfJaExistenteException.class, () -> service.update(created2));
	}

	@Test
	public void deveInativarUsuario() throws LivrariaException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().id(faker.random().nextLong(100))
				.nome(faker.name().fullName()).endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(instituicao()).build();

		final UsuarioDto created = service.create(usuarioDto);

		service.destroy(created.getId());

		final Usuario entity = repository.findById(created.getId())
				.orElseThrow(() -> new UsuarioNaoEncontradoException("Erro, usuário não encontrado!"));

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getId(), is(equalTo(created.getId())));
		assertThat(entity.getStatus(), is(equalTo(Status.INATIVO)));
	}

	@Test
	public void deveInativarUsuarioComEmprestimosDevolvidos() throws LivrariaException {
		final UsuarioDto usuario = service.create(usuario());

		final LivroDto livro1 = livro();

		final EmprestimoDto emprestimo1 = emprestimoService.emprestar(usuario.getId(), livro1.getId());

		emprestimoService.devolver(emprestimo1.getId());

		service.destroy(usuario.getId());

		final Usuario entity = repository.findById(usuario.getId())
				.orElseThrow(() -> new UsuarioNaoEncontradoException("Erro, usuário não encontrado!"));

		assertThat(entity, is(notNullValue()));
		assertThat(entity.getId(), is(equalTo(usuario.getId())));
		assertThat(entity.getStatus(), is(equalTo(Status.INATIVO)));

	}

	@Test
	public void naoDeveInativarUsuarioInexistente() {
		assertThrows(UsuarioNaoEncontradoException.class, () -> service.destroy(faker.random().nextLong()));
	}

	@Test
	public void naoDeveInativarUsuarioComEmprestimosAtivos() throws LivrariaException {
		final UsuarioDto usuario = service.create(usuario());

		final LivroDto livro1 = livro();

		emprestimoService.emprestar(usuario.getId(), livro1.getId());

		assertThrows(UsuarioPossuiEmprestimosAbertosException.class, () -> service.destroy(usuario.getId()));
	}

	@Test
	public void deveEncontrarUsuario() throws LivrariaException {
		final UsuarioDto usuarioDto = UsuarioDto.builder().id(faker.random().nextLong(100))
				.nome(faker.name().fullName()).endereco(faker.address().fullAddress()).cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress()).telefone(faker.phoneNumber().cellPhone())
				.instituicao(instituicao()).build();

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

	private UsuarioDto usuario() throws UsuarioNaoInformadoException, ValidationLivrariaException, InstituicaoEnsinoNaoInformadaException, CpfJaExistenteException, CnpjJaExistenteException {
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

	private InstituicaoEnsinoDto instituicao() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, CnpjJaExistenteException {
		final InstituicaoEnsinoDto instituicaoEnsino = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().cellPhone())
					.endereco(faker.address().fullAddress())
				.build();

		return instituicaoEnsinoService.create(instituicaoEnsino);
	}

	private InstituicaoEnsinoDto instituicaoEnsinoInativa()
			throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException,
			InstituicaoEnsinoNaoEncontradaException, InstituicaoEnsinoComUsuariosException, CnpjJaExistenteException {
		final InstituicaoEnsinoDto instituicaoEnsino = instituicao();

		instituicaoEnsinoService.destroy(instituicaoEnsino.getId());

		return instituicaoEnsino;
	}

	@BeforeEach
	public void prepare() {
		dbPrepareUtils.clean();
	}
}
