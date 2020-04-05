package br.com.orlandoburli.livraria.dto;

import br.com.orlandoburli.livraria.enums.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsuarioDto {

	private Long id;

	private String nome;

	private String endereco;

	private String cpf;

	private String telefone;

	private String email;

	private InstituicaoEnsinoDto instituicao;

	private Status status;
}