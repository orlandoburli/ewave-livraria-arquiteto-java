package br.com.orlandoburli.livraria.utils;

import java.lang.reflect.Field;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReflectionUtils {

	/**
	 * Seta o valor de um atributo pelo setter
	 *
	 * @param setter Metodo setter
	 * @param vo     Objecto vo a ser alterado
	 * @param value  Valor a ser setado
	 */
	public static void setValue(final String field, final Object vo, final Object value) {
		try {
			final Field f = vo.getClass().getDeclaredField(field);

			f.setAccessible(true);
			f.set(vo, value);

		} catch (final IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			log.error("Class: " + vo.getClass() + " Setter: " + field + " Value: " + value + " Value Type: "
					+ value.getClass(), e);
		}
	}
}
