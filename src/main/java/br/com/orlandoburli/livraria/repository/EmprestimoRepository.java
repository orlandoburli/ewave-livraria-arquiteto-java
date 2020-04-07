package br.com.orlandoburli.livraria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.orlandoburli.livraria.enums.StatusEmprestimo;
import br.com.orlandoburli.livraria.model.Emprestimo;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

	Optional<Emprestimo> findByLivroIdAndStatus(Long livroId, StatusEmprestimo status);

	Long countByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);
}
