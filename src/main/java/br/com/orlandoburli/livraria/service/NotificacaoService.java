package br.com.orlandoburli.livraria.service;

import org.springframework.stereotype.Service;

import br.com.orlandoburli.livraria.dto.EmprestimoDto;

@Service
public class NotificacaoService {

	public void notificarAdministrador(final String titulo, final String mensagem) {
		// TODO Implementar notificação
	}

	public void notificarEntregaComAtraso(final EmprestimoDto emprestimo) {
		// TODO Implementar notificação de entrega com atraso
	}
}
