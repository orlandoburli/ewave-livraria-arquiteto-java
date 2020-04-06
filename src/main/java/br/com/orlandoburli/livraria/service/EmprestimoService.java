package br.com.orlandoburli.livraria.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import br.com.orlandoburli.livraria.dto.EmprestimoDto;
import br.com.orlandoburli.livraria.dto.LivroDto;
import br.com.orlandoburli.livraria.dto.UsuarioDto;
import br.com.orlandoburli.livraria.enums.StatusEmprestimo;
import br.com.orlandoburli.livraria.exceptions.emprestimo.EmprestimoJaDevolvidoException;
import br.com.orlandoburli.livraria.exceptions.emprestimo.EmprestimoNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.livro.LivroNaoEncontradoException;
import br.com.orlandoburli.livraria.exceptions.usuario.UsuarioNaoEncontradoException;
import br.com.orlandoburli.livraria.model.Emprestimo;
import br.com.orlandoburli.livraria.repository.EmprestimoRepository;
import br.com.orlandoburli.livraria.utils.MessagesService;

@Service
public class EmprestimoService {

	private static final int PRAZO_DEVOLUCAO = 30;

	@Autowired
	private EmprestimoRepository repository;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private LivroService livroService;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private MessagesService messages;

	/**
	 * Retorna um emprestimo pelo seu id
	 *
	 * @param id Id do empréstimo
	 * @return Emprestimo localizado
	 * @throws EmprestimoNaoEncontradoException Exceção disparada caso o empréstimo
	 *                                          não tenha sido localizado.
	 */
	public EmprestimoDto get(final Long id) throws EmprestimoNaoEncontradoException {
		final Emprestimo entity = repository.findById(id).orElseThrow(() -> new EmprestimoNaoEncontradoException(
				messages.get("exceptions.EmprestimoNaoEncontradoException", id)));
	
		return conversionService.convert(entity, EmprestimoDto.class);
	}

	public EmprestimoDto realizarEmprestimo(final Long usuarioId, final Long livroId)
			throws UsuarioNaoEncontradoException, LivroNaoEncontradoException {

		final UsuarioDto usuario = usuarioService.get(usuarioId);

		final LivroDto livro = livroService.get(livroId);

		validaImpedimentosUsuario(usuario);

		validaImpedimentosLivro(livro);

		final EmprestimoDto emprestimo = EmprestimoDto.builder().livro(livro).usuario(usuario)
				.dataEmprestimo(dataAtual()).build();

		final Emprestimo entity = repository.save(conversionService.convert(emprestimo, Emprestimo.class));

		final EmprestimoDto emprestimoDto = conversionService.convert(entity, EmprestimoDto.class);

		calculaDataDevolucao(emprestimoDto);

		return emprestimoDto;
	}

	/**
	 * Devolve um livro
	 *
	 * @param id Id do empréstimo
	 * @throws EmprestimoNaoEncontradoException Exceção disparada caso o empréstimo
	 *                                          não seja localizado
	 * @throws EmprestimoJaDevolvidoException   Exceção disparada caso o empréstimo
	 *                                          já tenha sido devolvido
	 */
	public void devolverLivro(final Long id) throws EmprestimoNaoEncontradoException, EmprestimoJaDevolvidoException {
		final EmprestimoDto emprestimo = get(id);

		validaLivroPodeSerDevolvido(emprestimo);

		emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
		emprestimo.setDataDevolucao(dataAtual());

		repository.save(conversionService.convert(emprestimo, Emprestimo.class));

		if (!isEmprestimoDevolvidoNoPrazo(emprestimo)) {
			registraInadimplenciaUsuario(emprestimo.getUsuario());
		}
	}

	/**
	 * Cria um registro de inadimplência para o usuário, bloqueando-o por 30 dias.
	 *
	 * @param usuario usuário a ser bloqueado.
	 */
	private void registraInadimplenciaUsuario(final UsuarioDto usuario) {
		// TODO Implementar inadimplencia
	}

	/**
	 * Identifica se o empréstimo foi devolvido no prazo
	 *
	 * @param emprestimo Empréstimo a ser verificado
	 * @return <b>true</b> se foi devolvido no prazo, caso contrário <b>false</b>
	 */
	private boolean isEmprestimoDevolvidoNoPrazo(final EmprestimoDto emprestimo) {
		calculaDataDevolucao(emprestimo);

		return emprestimo.getDataPrevistaDevolucao().isAfter(emprestimo.getDataDevolucao());
	}

	/**
	 * Verifica se existe algum impedimento para o emprestimo deste livro
	 *
	 * @param livro Livro a ser verificado
	 */
	private void validaImpedimentosLivro(final LivroDto livro) {
		// TODO Auto-generated method stub

	}

	/**
	 * Verifica se existe algum impedimento para o usuário emprestar livros
	 *
	 * @param usuario Usuário a ser verificado
	 */
	private void validaImpedimentosUsuario(final UsuarioDto usuario) {
		// TODO Auto-generated method stub
	}

	/**
	 * Verifica se o livro pode ser devolvido
	 *
	 * @param emprestimo Empréstimo a ser validado
	 * @throws EmprestimoJaDevolvidoException Exceção disparada caso o empréstimo já
	 *                                        tenha sido devolvido
	 */
	private void validaLivroPodeSerDevolvido(final EmprestimoDto emprestimo) throws EmprestimoJaDevolvidoException {
		if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
			throw new EmprestimoJaDevolvidoException(
					messages.get("exceptions.EmprestimoJaDevolvidoException", emprestimo.getId()));
		}
	}

	/**
	 * Retorna a data atual. <br/>
	 *
	 * <b>Atenção: O motivo de existir este método é para facilitar o mock dos
	 * testes.</b>
	 *
	 * @return Data atual do sistema.
	 */
	private LocalDate dataAtual() {
		return LocalDate.now();
	}

	private void calculaDataDevolucao(final EmprestimoDto emprestimoDto) {
		emprestimoDto.setDataPrevistaDevolucao(emprestimoDto.getDataEmprestimo().plusDays(PRAZO_DEVOLUCAO));
	}
}