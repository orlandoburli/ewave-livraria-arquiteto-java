package br.com.orlandoburli.livraria.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatusReserva {

	RESERVADO("R", "Reservado"), CANCELADO("C", "Cancelado"), FINALIZADO("E", "Empréstimo realizado");

	private final String valor;
	private final String descricao;

	StatusReserva(final String valor, final String descricao) {
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
	public static StatusReserva from(final String requisicao) {
		for (final StatusReserva s : StatusReserva.values()) {
			if (s.getValor().equals(requisicao) || s.getDescricao().equalsIgnoreCase(requisicao)
					|| s.getValor().equalsIgnoreCase(requisicao)) {
				return s;
			}
		}
		return null;
	}
}
