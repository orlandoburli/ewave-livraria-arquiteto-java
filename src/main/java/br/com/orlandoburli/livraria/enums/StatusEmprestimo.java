package br.com.orlandoburli.livraria.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusEmprestimo {

	ABERTO("A", "Aberto"), DEVOLVIDO("D", "Devolvido");

	private final String valor;
	private final String descricao;

	StatusEmprestimo(final String valor, final String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}

	public String getValor() {
		return valor;
	}

	@JsonValue
	public String getDescricao() {
		return descricao;
	}

	@JsonCreator
	public static StatusEmprestimo from(final String requisicao) {
		for (final StatusEmprestimo s : StatusEmprestimo.values()) {
			if (s.getValor().equals(requisicao) || s.getDescricao().equalsIgnoreCase(requisicao)
					|| s.getValor().equalsIgnoreCase(requisicao)) {
				return s;
			}
		}
		return null;
	}
}