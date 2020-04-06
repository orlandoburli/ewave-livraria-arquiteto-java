package br.com.orlandoburli.livraria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.model.Livro;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long>{

	Optional<Livro> findByIdAndStatus(Long id, Status status);
	
}