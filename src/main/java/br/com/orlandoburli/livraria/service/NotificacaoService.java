package br.com.orlandoburli.livraria.service;

import java.time.Period;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.orlandoburli.livraria.dto.EmprestimoDto;
import br.com.orlandoburli.livraria.dto.MensagemDto;
import br.com.orlandoburli.livraria.utils.MessagesService;

@Service
public class NotificacaoService {

	static final String FILA_MENSAGENS = "livraria.notificacoes.queue";

	@Autowired(required = false)
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private MessagesService messages;

	public void notificarEntregaComAtraso(final EmprestimoDto emprestimo) {

		final Period atraso = Period.between(emprestimo.getDataPrevistaDevolucao(), emprestimo.getDataDevolucao());

		// @formatter:off
		final MensagemDto mensagem = MensagemDto
				.builder()
					.titulo(messages.get("notifications.entregaComAtraso.titulo"))
					.mensagem(messages.get("notifications.entregaComAtraso.mensagem",
							emprestimo.getLivro().getTitulo(),
							atraso.getDays(),
							emprestimo.getUsuario().getNome()))
					.destinatario(messages.get("notifications.entregaComAtraso.destinatario"))
				.build();

		rabbitTemplate.convertAndSend(FILA_MENSAGENS, mensagem);
	}
}