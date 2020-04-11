package br.com.orlandoburli.livraria.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MensagemDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String titulo;

	private String mensagem;

	private String destinatario;
}
