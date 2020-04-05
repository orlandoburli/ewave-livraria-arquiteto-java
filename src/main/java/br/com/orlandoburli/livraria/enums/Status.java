package br.com.orlandoburli.livraria.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {

	ATIVO("A", "Ativo"), INATIVO("I", "Inativo");

	private final String valor;
	private final String descricao;

	Status(final String valor, final String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}

	public String getValor() {
		return valor;
	}

	public String getDescricao() {
		return descricao;
	}

	@JsonCreator
	public static Status from(final String requisicao) {
		for (final Status s : Status.values()) {
			if (s.getValor().equals(requisicao) || s.getDescricao().equalsIgnoreCase(requisicao)
					|| s.getValor().equalsIgnoreCase(requisicao)) {
				return s;
			}
		}
		return null;
	}
}