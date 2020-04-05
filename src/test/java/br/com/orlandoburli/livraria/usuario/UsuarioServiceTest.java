package br.com.orlandoburli.livraria.usuario;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


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

import br.com.orlandoburli.livraria.repository.UsuarioRepository;
import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoComUsuariosException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoNaoEncontradaException;
import br.com.orlandoburli.livraria.exceptions.instituicaoensino.InstituicaoEnsinoNaoInformadaException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
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

	private Faker faker = new Faker(new Locale("pt", "BR"));

	private GeraCpfCnpj geradorCpfCnpj = new GeraCpfCnpj();
	
	@Test
	public void deveCriarUmUsuario() throws ValidationLivrariaException, InstituicaoEnsinoNaoInformadaException {
		
		InstituicaoEnsinoDto instituicao = createInstituicaoEnsino();

		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome("Orlando Burli Junior")
				.endereco("Av Mario Augusto Vieira, 269")
				.cpf("702.890.181-53")
				.email("orlando.burli@gmail.com")
				.telefone("(65) 99946-3093")
				.instituicao(instituicao)
			.build();
		
		UsuarioDto created = this.service.create(usuarioDto);
		
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
	public void naoDeveCriarUmUsuarioComNomeNulo() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(null)
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome é obrigatório"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComNomeVazio() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome("    ")
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome é obrigatório"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComNomeDeMenosDe3Caracteres() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.lorem().characters(1, 2))
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome deve ter entre 3 e 100 caracteres"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComNomeDeMaisDe100Caracteres() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.lorem().characters(101, 150))
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("nome"));

		assertThat(exception.getErrors().get("nome"), hasItem("Nome deve ter entre 3 e 100 caracteres"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComEnderecoNulo() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(null)
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço é obrigatório"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComEnderecoVazio() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco("     ")
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço é obrigatório"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComEnderecoDeMenosDe3Caracteres() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.lorem().characters(1, 2))
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço deve ter entre 3 e 100 caracteres"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComEnderecoDeMaisDe100Caracteres() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.lorem().characters(101, 150))
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("endereco"));

		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço deve ter entre 3 e 100 caracteres"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComCpfInvalido() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.address().fullAddress())
				.cpf(Integer.toString(faker.random().nextInt(11)))
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("cpf"));

		assertThat(exception.getErrors().get("cpf"), hasItem("CPF inválido"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComCpfNulo() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.address().fullAddress())
				.cpf(null)
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("cpf"));

		assertThat(exception.getErrors().get("cpf"), hasItem("CPF é obrigatório"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComCpfVazio() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.address().fullAddress())
				.cpf("     ")
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("cpf"));

		assertThat(exception.getErrors().get("cpf"), hasItem("CPF é obrigatório"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComTelefoneComMenosDe10Caracteres() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(Integer.toString(faker.random().nextInt(1, 9)))
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("telefone"));

		assertThat(exception.getErrors().get("telefone"), hasItem("Telefone inválido"));
	} 
	
	@Test
	public void naoDeveCriarUmUsuarioComTelefoneComMaisDe11Caracteres() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(Integer.toString(faker.random().nextInt(11, 20)))
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("telefone"));

		assertThat(exception.getErrors().get("telefone"), hasItem("Telefone inválido"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComEmailInvalido() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.lorem().characters(10, 20))
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("email"));

		assertThat(exception.getErrors().get("email"), hasItem("Email inválido"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComEmailComMaisde200Caracteres() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.lorem().characters(201, 250) + "@gmail.com")
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsino())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("email"));
	
		assertThat(exception.getErrors().get("email"), hasItem("Email não pode ter mais de 200 caracteres"));
	}

	@Test
	public void naoDeveCriarUmUsuarioComInstituicaoNula() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(null)
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("instituicao"));

		assertThat(exception.getErrors().get("instituicao"), hasItem("Instituição é obrigatória"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComInstituicaoInexistente() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		InstituicaoEnsinoDto instituicaoFake = InstituicaoEnsinoDto
				.builder()
					.id(faker.random().nextLong(10))
				.build();
		
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(instituicaoFake)
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("instituicao"));

		assertThat(exception.getErrors().get("instituicao"), hasItem("Instituição é obrigatória"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComInstituicaoInexistenteComIdNulo() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException {
		InstituicaoEnsinoDto instituicaoFake = InstituicaoEnsinoDto
				.builder()
				.build();
		
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(instituicaoFake)
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("instituicao"));

		assertThat(exception.getErrors().get("instituicao"), hasItem("Instituição é obrigatória"));
	}
	
	@Test
	public void naoDeveCriarUmUsuarioComInstituicaoInativa() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, InstituicaoEnsinoNaoEncontradaException, InstituicaoEnsinoComUsuariosException {
		UsuarioDto usuarioDto = UsuarioDto
			.builder()
				.nome(faker.name().fullName())
				.endereco(faker.address().fullAddress())
				.cpf(geradorCpfCnpj.cpf())
				.email(faker.internet().emailAddress())
				.telefone(faker.phoneNumber().cellPhone())
				.instituicao(createInstituicaoEnsinoInativa())
			.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () -> service.create(usuarioDto));
		
		assertTrue(exception.getErrors().containsKey("instituicao"));

		assertThat(exception.getErrors().get("instituicao"), hasItem("Instituição é obrigatória"));
	}
	
	private InstituicaoEnsinoDto createInstituicaoEnsino()
			throws ValidationLivrariaException, InstituicaoEnsinoNaoInformadaException {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome("Faculdade das Américas")
					.cnpj("23.519.978/0001-60")
					.telefone("(65) 2333-2344")
					.endereco("Av das Torres, 2344")
				.build();

		InstituicaoEnsinoDto instituicao = instituicaoEnsinoService.create(instituicaoEnsinoDto);
		return instituicao;
	}
	
	private InstituicaoEnsinoDto createInstituicaoEnsinoInativa() throws InstituicaoEnsinoNaoInformadaException, ValidationLivrariaException, InstituicaoEnsinoNaoEncontradaException, InstituicaoEnsinoComUsuariosException {
		InstituicaoEnsinoDto instituicaoEnsino = this.createInstituicaoEnsino();
		
		this.instituicaoEnsinoService.destroy(instituicaoEnsino.getId());
		
		return instituicaoEnsino;
	}
	
	@BeforeEach
	public void prepare() {
		this.dbPrepareUtils.clean();
	}
}
