package br.com.orlandoburli.livraria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.orlandoburli.livraria.model.Capa;
import br.com.orlandoburli.livraria.model.CapaId;

@Repository
public interface CapaRepository extends JpaRepository<Capa, CapaId>{

}