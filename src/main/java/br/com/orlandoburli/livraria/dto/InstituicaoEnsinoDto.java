package br.com.orlandoburli.livraria.dto;

import java.io.Serializable;

import br.com.orlandoburli.livraria.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstituicaoEnsinoDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String nome;

	private String endereco;

	private String cnpj;

	private String telefone;
	
	private Status status;
}