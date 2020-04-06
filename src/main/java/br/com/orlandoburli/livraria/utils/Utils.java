package br.com.orlandoburli.livraria.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;

public final class Utils {

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

	/**
	 * Calcula o SHA1 de um conjunto de bytes.
	 * @param dados Bytes a serem calculados
	 * @return String com o SHA1 calculado
	 * @throws NoSuchAlgorithmException
	 */
	public static String sha1(byte[] dados) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(dados);
		return String.format("%040x", new BigInteger(1, digest.digest()));
	}
}
