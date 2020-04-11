package br.com.orlandoburli.livraria.service;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.orlandoburli.livraria.dto.MensagemDto;

@Service
public class MailSenderService {

	@Autowired(required = false)
	private JavaMailSender javaMailSender;

	@RabbitListener(queuesToDeclare = { @Queue(NotificacaoService.FILA_MENSAGENS) })
	public void receive(@Payload final MensagemDto mensagem) throws JsonMappingException, JsonProcessingException {
		sendEmail(mensagem);
	}

	public void sendEmail(final MensagemDto mensagem) {
		final SimpleMailMessage msg = new SimpleMailMessage();

		msg.setTo(mensagem.getDestinatario());
		msg.setSubject(mensagem.getTitulo());
		msg.setText(mensagem.getMensagem());

		javaMailSender.send(msg);
	}
}