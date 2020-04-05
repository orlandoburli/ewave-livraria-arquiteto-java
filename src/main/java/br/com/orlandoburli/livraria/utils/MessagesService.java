package br.com.orlandoburli.livraria.utils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Component
public class MessagesService {

	@Autowired
	private MessageSource messageSource;

	private MessageSourceAccessor accessor;

	@PostConstruct
	private void init() {
		this.accessor = new MessageSourceAccessor(this.messageSource);
	}

	public String get(final String code) {
		return this.accessor.getMessage(code);
	}

	public String get(final String code, final Object... args) {
		return this.accessor.getMessage(code, args);
	}
}