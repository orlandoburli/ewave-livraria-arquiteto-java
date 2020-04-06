package br.com.orlandoburli.livraria.utils;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ImageUtils {

	@Value("classpath:books/book01.jpg")
	private Resource book01;

	@Value("classpath:books/book02.jpg")
	private Resource book02;

	@Value("classpath:books/book03.jpg")
	private Resource book03;

	public byte[] book01() throws IOException {
		return IOUtils.toByteArray(book01.getInputStream());
	}

	public byte[] book02() throws IOException {
		return IOUtils.toByteArray(book02.getInputStream());
	}

	public byte[] book03() throws IOException {
		return IOUtils.toByteArray(book03.getInputStream());
	}
}