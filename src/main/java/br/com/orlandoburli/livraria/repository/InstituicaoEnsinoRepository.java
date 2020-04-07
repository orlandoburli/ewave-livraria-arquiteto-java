package br.com.orlandoburli.livraria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.model.InstituicaoEnsino;

@Repository
public interface InstituicaoEnsinoRepository extends JpaRepository<InstituicaoEnsino, Long> {

	Optional<InstituicaoEnsino> findByIdAndStatus(Long id, Status status);

	Optional<InstituicaoEnsino> findByCnpjAndIdNot(String cnpj, Long id);
}