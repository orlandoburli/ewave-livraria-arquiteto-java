package br.com.orlandoburli.livraria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.orlandoburli.livraria.enums.Status;
import br.com.orlandoburli.livraria.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Long countByInstituicaoId(Long id);

	Optional<Usuario> findByIdAndStatus(Long id, Status status);
	
	Optional<Usuario> findByCpfAndIdNot(String cpf, Long id);
}
