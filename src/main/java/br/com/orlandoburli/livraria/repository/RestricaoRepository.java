package br.com.orlandoburli.livraria.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.orlandoburli.livraria.model.Restricao;

@Repository
public interface RestricaoRepository extends JpaRepository<Restricao, Long> {

	Long countByEmprestimoUsuarioIdAndRestritoAteGreaterThanEqual(Long usuarioId, LocalDate data);
}