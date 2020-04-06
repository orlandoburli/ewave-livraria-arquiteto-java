package br.com.orlandoburli.livraria.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import br.com.orlandoburli.livraria.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "capa", schema = Constants.SCHEMA)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Capa implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CapaId id;

	@Lob
	private byte[] imagem;
}