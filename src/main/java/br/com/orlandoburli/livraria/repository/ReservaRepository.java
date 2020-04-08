package br.com.orlandoburli.livraria.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.orlandoburli.livraria.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

	Optional<Reserva> findByLivroIdAndDataReservaGreaterThanEqualAndUsuarioIdNot(Long livroId, LocalDate dataReservas,
			Long usuarioId);
}