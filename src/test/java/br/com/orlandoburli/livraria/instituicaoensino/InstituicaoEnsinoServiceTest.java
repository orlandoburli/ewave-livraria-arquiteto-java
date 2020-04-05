package br.com.orlandoburli.livraria.instituicaoensino;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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

import com.github.javafaker.Faker;

import br.com.orlandoburli.livraria.dto.InstituicaoEnsinoDto;
import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.exceptions.LivrariaException;
import br.com.orlandoburli.livraria.exceptions.validations.ValidationLivrariaException;
import br.com.orlandoburli.livraria.service.InstituicaoEnsinoService;
import br.com.orlandoburli.livraria.utils.DbPrepareUtils;
import br.com.orlandoburli.livraria.utils.GeraCpfCnpj;
import br.com.orlandoburli.livraria.utils.Utils;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableAutoConfiguration
@TestPropertySource(locations = "classpath:application.yml")
public class InstituicaoEnsinoServiceTest {

	@Autowired
	private InstituicaoEnsinoService service;

	@Autowired
	private DbPrepareUtils dbPrepareUtils;
	
	private Faker faker = new Faker(new Locale("pt", "BR"));
	
	private GeraCpfCnpj geradorCpfCnpj = new GeraCpfCnpj();
	
	@Test
	public void deveCriarUmaEntidade() throws LivrariaException {

		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
				.nome("Faculdade das Américas")
				.cnpj("23.519.978/0001-60")
				.telefone("(65) 2333-2344")
				.endereco("Av das Torres, 2344")
				.build();
		
		InstituicaoEnsinoDto created = service.create(instituicaoEnsinoDto);

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
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
				.nome(null)
				.cnpj("23.519.978/0001-60")
				.telefone("(65) 2333-2344")
				.endereco("Av das Torres, 2344")
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("nome"));
		
		assertThat(exception.getErrors().get("nome"), hasItem("Nome é obrigatório"));
	}
	
	@Test
	public void naoDeveCriarEntidadeComNomeVazio() {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
				.nome("        ")
				.cnpj(geradorCpfCnpj.cnpj())
				.telefone(faker.phoneNumber().phoneNumber())
				.endereco(faker.address().fullAddress())
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("nome"));
		
		assertThat(exception.getErrors().get("nome"), hasItem("Nome é obrigatório"));
	}
	
	@Test
	public void naoDeveCriarEntidadeComNomeDeMaisDe100Caracteres() {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome(faker.lorem().characters(101, 150, true, true))
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().phoneNumber())
					.endereco(faker.address().fullAddress())
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("nome"));
		
		assertThat(exception.getErrors().get("nome"), hasItem("Nome deve ter entre 3 e 100 caracteres"));
	}
	
	@Test
	public void naoDeveCriarEntidadeComMenosDe3Caracteres() {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome(faker.lorem().characters(1, 2))
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().phoneNumber())
					.endereco(faker.address().fullAddress())
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("nome"));
		
		assertThat(exception.getErrors().get("nome"), hasItem("Nome deve ter entre 3 e 100 caracteres"));
	}
	
	@Test
	public void naoDeveCriarEntidadeComEnderecoNulo() {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().cellPhone())
					.endereco(null)
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("endereco"));
		
		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço é obrigatório"));
	}
	
	@Test
	public void naoDeveCriarEntidadeComEnderecoVazio() {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().cellPhone())
					.endereco("   ")
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("endereco"));
		
		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço é obrigatório"));
	}
	
	@Test
	public void naoDeveCriarEntidadeComEnderecoComMaisDe100Caracteres() {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().cellPhone())
					.endereco(faker.lorem().characters(101, 150))
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("endereco"));
		
		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço deve ter entre 3 e 100 caracteres"));
	}
	
	@Test
	public void naoDeveCriarEntidadeComEnderecoComMenosDe3Caracteres() {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.phoneNumber().cellPhone())
					.endereco(faker.lorem().characters(1, 2))
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("endereco"));
		
		assertThat(exception.getErrors().get("endereco"), hasItem("Endereço deve ter entre 3 e 100 caracteres"));
	}
	
	@Test
	public void naoDeveCriarEntidadeComCnpjNulo() {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(null)
					.telefone(faker.phoneNumber().cellPhone())
					.endereco(faker.address().fullAddress())
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("cnpj"));
		
		assertThat(exception.getErrors().get("cnpj"), hasItem("Cnpj é obrigatório"));
	}
	
	@Test
	public void naoDeveCriarEntidadeComCnpjVazio() {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj("    ")
					.telefone(faker.phoneNumber().cellPhone())
					.endereco(faker.address().fullAddress())
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("cnpj"));
		
		assertThat(exception.getErrors().get("cnpj"), hasItem("Cnpj é obrigatório"));
	}
	
	@Test
	public void naoDeveCriarEntidadeComCnpjInvalido() {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(faker.random().nextInt(14, 14).toString())
					.telefone(faker.phoneNumber().cellPhone())
					.endereco(faker.address().fullAddress())
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("cnpj"));
		
		assertThat(exception.getErrors().get("cnpj"), hasItem("Cnpj inválido"));
	}
	
	@Test
	public void naoDeveCriarEntidadeComTelefoneComMenosDe10Caracteres() {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.random().nextInt(1, 9).toString())
					.endereco(faker.address().fullAddress())
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("telefone"));
		
		assertThat(exception.getErrors().get("telefone"), hasItem("Telefone inválido"));
	}
	
	@Test
	public void naoDeveCriarEntidadeComTelefoneComMaisDe11Caracteres() {
		InstituicaoEnsinoDto instituicaoEnsinoDto = InstituicaoEnsinoDto
				.builder()
					.nome(faker.company().name())
					.cnpj(geradorCpfCnpj.cnpj())
					.telefone(faker.random().nextInt(12, 30).toString())
					.endereco(faker.address().fullAddress())
				.build();
		
		ValidationLivrariaException exception = assertThrows(ValidationLivrariaException.class, () ->  service.create(instituicaoEnsinoDto));
		
		assertTrue(exception.getErrors().containsKey("telefone"));
		
		assertThat(exception.getErrors().get("telefone"), hasItem("Telefone inválido"));
	}
	
	@BeforeEach
	public void prepare() {
		this.dbPrepareUtils.clean();
	}
}