package br.com.orlandoburli.livraria.dto;

import br.com.orlandoburli.livraria.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
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