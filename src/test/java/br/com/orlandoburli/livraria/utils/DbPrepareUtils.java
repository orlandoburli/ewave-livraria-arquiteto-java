package br.com.orlandoburli.livraria.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DbPrepareUtils {

	@Autowired
	private JdbcTemplate jdbc;

	@Value("classpath:scripts/001_create_schema.sql")
	private Resource resource001;

	@Value("classpath:scripts/002_tables.sql")
	private Resource resource002;

	@Autowired
	private EntityManager manager;

	/**
	 * Apaga o schema SECORP e todos os objetos juntos, se existirem.
	 */
	public void dropSchema() {
		this.jdbc.execute("DROP SCHEMA IF EXISTS " + Constants.SCHEMA + " CASCADE");
	}

	/**
	 * Le todos os scrips de create e executa.
	 */
	public void createAll() {
		this.executeResource(this.resource001);
		this.executeResource(this.resource002);
	}

	/**
	 * Executa os scripts dentro de um resource.
	 *
	 * @param resource
	 *            Resource que representa o script a ser testado.
	 */
	private void executeResource(final InputStreamSource resource) {
		try {
			final String scripts = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8.name());

			// Quebra o script por ponto e virgula e executa separadamente.
			Arrays.stream(scripts.split(";")).forEach(s -> this.jdbc.execute(s));

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cria uma instância limpa do banco de dados.
	 */
	public void clean() {
		this.dropSchema();
		this.createAll();
		this.showTablesCount();
	}

	public void showTablesCount() {
		final Integer total = this.jdbc.queryForObject("SELECT COUNT(1) AS TOTAL FROM INFORMATION_SCHEMA.TABLES T WHERE UPPER(T.TABLE_SCHEMA) = '" + Constants.SCHEMA.toUpperCase() + "'", Integer.class);

		log.info("Total de Tabelas: " + total);

		final List<String> tabelas = this.jdbc.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES T WHERE UPPER(T.TABLE_SCHEMA) = '" + Constants.SCHEMA.toUpperCase() + "' ORDER BY TABLE_NAME", String.class);

		tabelas.forEach(t -> log.info("-> " + t));
	}

	/**
	 * Executa o flush e commit no entity manager.
	 */
	public void flushAndCommit() {
		this.manager.flush();
	}
}
