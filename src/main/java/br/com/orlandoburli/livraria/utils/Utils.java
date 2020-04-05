package br.com.orlandoburli.livraria.utils;

import org.apache.commons.lang3.StringUtils;

public final class Utils {

	private Utils() {
	}

	/**
	 * Remove todos os caracteres não-numéricos de uma string
	 * 
	 * @param source String a ser tratada
	 * @return string somente com números
	 */
	public static String numbersOnly(String source) {
		if (!StringUtils.isEmpty(source)) {
			return source.replaceAll("\\D+", "");
		}
		return "";
	}
}
