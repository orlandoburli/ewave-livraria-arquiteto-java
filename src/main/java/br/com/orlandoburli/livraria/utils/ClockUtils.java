package br.com.orlandoburli.livraria.utils;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component
public class ClockUtils {

	public LocalDate hoje() {
		return LocalDate.now();
	}
}
